package com.trutek.looped.chatmodule.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.AttachmentModel;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.contracts.models.State;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatNotificationUtils;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.chatmodule.utils.DateUtilsCore;
import com.trutek.looped.chatmodule.utils.DbUtils;
import com.trutek.looped.chatmodule.utils.FinderUnknownUsers;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BaseChatHelper extends BaseHelper {

    private static final String TAG = BaseChatHelper.class.getSimpleName();
    protected QBChatService chatService;
    protected QBUser chatCreator;
    protected DataManager dataManager;

    protected GroupChatMessageListener groupChatMessageListener;
    private PrivateChatManagerListener privateChatManagerListener;
    protected PrivateChatMessageListener privateChatMessageListener;
    protected QBPrivateChatManager privateChatManager;
    protected QBChatDialog currentDialog;
    private List<QBNotificationChatListener> notificationChatListeners;

    protected QBGroupChatManager groupChatManager;
    private QBSystemMessagesManager systemMessagesManager;

    public BaseChatHelper(Context context) {
        super(context);

        privateChatManagerListener = new PrivateChatManagerListener();
        privateChatMessageListener = new PrivateChatMessageListener();

        groupChatMessageListener = new GroupChatMessageListener();
        notificationChatListeners = new CopyOnWriteArrayList<QBNotificationChatListener>();

        dataManager = DataManager.getInstance();
    }

    public abstract void closeChat(QBChatDialog dialogId, Bundle additional);

    /*
        Call this method when you want start chating by existing dialog
   */
    public abstract QBChat createChatLocally(QBChatDialog dialog, Bundle additional) throws QBResponseException;

    public void init(QBUser chatCreator) {
        this.chatService = QBChatService.getInstance();
        this.chatCreator = chatCreator;

        privateChatManager = chatService.getPrivateChatManager();
        privateChatManager.addPrivateChatManagerListener(privateChatManagerListener);

        groupChatManager = chatService.getGroupChatManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
    }

    public List<QBChatDialog> getDialogs(QBRequestGetBuilder qbRequestGetBuilder, Bundle returnedBundle) throws QBResponseException {
        Log.d("Fix double message", "currentDialog = " + currentDialog);
        List<QBChatDialog> qbDialogsList = QBRestChatService.getChatDialogs(null, qbRequestGetBuilder).perform();

        if (qbDialogsList != null && !qbDialogsList.isEmpty()) {
            FinderUnknownUsers finderUnknownUsers = new FinderUnknownUsers(context, AppSession.getSession().getQbUser(), qbDialogsList, dataManager);
            finderUnknownUsers.find();
            DbUtils.saveDialogsToCache(dataManager, qbDialogsList);
//            DbUtils.updateDialogsOccupantsStatusesIfNeeded(dataManager, qbDialogsList);
        }

        return qbDialogsList;
    }

    protected void addSystemMessageListener(QBSystemMessageListener systemMessageListener) {
        systemMessagesManager.addSystemMessageListener(systemMessageListener);
    }

    public List<QBChatMessage> getDialogMessages(QBRequestGetBuilder customObjectRequestBuilder,
                                                 Bundle returnedBundle, QBChatDialog qbDialog,
                                                 long lastDateLoad) throws QBResponseException {
        List<QBChatMessage> qbMessagesList = QBRestChatService.getDialogMessages(qbDialog,
                customObjectRequestBuilder).perform();
        try {
            QBRestChatService.markMessagesAsRead(qbDialog.getDialogId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (qbMessagesList != null && !qbMessagesList.isEmpty()) {
            DbUtils.saveMessagesToCache(context, dataManager, qbMessagesList, qbDialog.getDialogId());
        }

        return qbMessagesList;
    }

    public void deleteDialog(String dialogId, DialogModel.Type dialogType) {
        try {
            if (DialogModel.Type.PRIVATE.equals(dialogType)) {
                QBChatService.getInstance().getPrivateChatManager().deleteDialog(dialogId);
            } else {
                QBChatService.getInstance().getGroupChatManager().deleteDialog(dialogId);
            }
        } catch (QBResponseException e) {
            ErrorUtils.logError(e);
        }
        DbUtils.deleteDialogLocal(dataManager, dialogId);
    }
    /* private chats*/

    public  QBPrivateChat createPrivateChatIfNotExist(int userId) throws QBResponseException {
        if (privateChatManager == null) {
            ErrorUtils.logError(TAG, " private chats is NULL");
            throw new QBResponseException(context.getString(R.string.dlg_fail_create_chat));
        }

        QBPrivateChat privateChat = privateChatManager.getChat(userId);
        if (privateChat == null) {
            privateChat = privateChatManager.createChat(userId, null);
        }

        return privateChat;
    }

    public void sendPrivateMessage(QBChatMessage qbChatMessage, int opponentId, String dialogId) throws QBResponseException {
        addNecessaryPropertyForQBChatMessage(qbChatMessage, dialogId);

        sendPrivateMessage(qbChatMessage, opponentId);
        DbUtils.saveMessageOrNotificationToCache(context, dataManager, dialogId, qbChatMessage, null, true);
        DbUtils.updateDialogModifiedDate(dataManager, dialogId, qbChatMessage);
    }

    public void sendPrivateMessage(QBChatMessage qbChatMessage, int opponentId) throws QBResponseException {
        QBPrivateChat privateChat = createPrivateChatIfNotExist(opponentId);

        qbChatMessage.setMarkable(true);

        String error = null;
        try {
            if (privateChat != null) {
                privateChat.sendMessage(qbChatMessage);
            }
        } catch (SmackException.NotConnectedException e) {
            error = context.getString(R.string.dlg_fail_connection);
        }
        if (error != null) {
            throw new QBResponseException(error);
        }
    }

    public interface QBNotificationChatListener {

        void onReceivedNotification(String notificationType, QBChatMessage chatMessage);
    }






    protected void addNotificationChatListener(QBNotificationChatListener notificationChatListener) {
        notificationChatListeners.add(notificationChatListener);
    }

    public void onPrivateMessageReceived(QBChat privateChat, final QBChatMessage chatMessage) {
    }

    protected void checkForSendingNotification(boolean ownMessage, QBChatMessage qbChatMessage, ChatUserModel user,
                                               boolean isPrivateChat) {
        String dialogId = (String) qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_DIALOG_ID);
        if (qbChatMessage.getId() == null || dialogId == null) {
            return;
        }

        sendNotificationBroadcast(QuickBloxServiceConsts.GOT_CHAT_MESSAGE, qbChatMessage, user, dialogId,
                isPrivateChat);

        if (currentDialog != null) {
            if (!ownMessage && !currentDialog.getDialogId().equals(dialogId)) {
                sendNotificationBroadcast(QuickBloxServiceConsts.GOT_CHAT_MESSAGE_LOCAL, qbChatMessage, user, dialogId, isPrivateChat);
            }
        } else {
            sendNotificationBroadcast(QuickBloxServiceConsts.GOT_CHAT_MESSAGE_LOCAL, qbChatMessage, user, dialogId,
                    isPrivateChat);
        }
    }

    private void sendNotificationBroadcast(String action, QBChatMessage chatMessage, ChatUserModel user, String dialogId,
                                           boolean isPrivateMessage) {
        Intent intent = new Intent(action);
        String messageBody = chatMessage.getBody();
        String extraChatMessage;

        if (chatMessage.getAttachments() != null && !chatMessage.getAttachments().isEmpty()) {
            extraChatMessage = context.getResources().getString(R.string.file_was_attached);
        } else {
            extraChatMessage = messageBody;
        }

        intent.putExtra(QuickBloxServiceConsts.EXTRA_CHAT_MESSAGE, extraChatMessage);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_USER, user);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_IS_PRIVATE_MESSAGE, isPrivateMessage);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        context.sendBroadcast(intent);
    }

    protected void addNecessaryPropertyForQBChatMessage(QBChatMessage qbChatMessage, String dialogId) {
        long time = DateUtilsCore.getCurrentTime();
        qbChatMessage.setProperty(ChatNotificationUtils.PROPERTY_DIALOG_ID, dialogId);
        qbChatMessage.setProperty(ChatNotificationUtils.PROPERTY_DATE_SENT, time + Constants.EMPTY_STRING);
    }

    protected QBChatMessage getQBChatMessage(String body, QBFile qbFile) {
        long time = DateUtilsCore.getCurrentTime();
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(body);

        if (qbFile != null) {
            QBAttachment attachment = getAttachment(qbFile);
            chatMessage.addAttachment(attachment);
        }

        chatMessage.setProperty(ChatNotificationUtils.PROPERTY_DATE_SENT, time + Constants.EMPTY_STRING);
        chatMessage.setProperty(ChatNotificationUtils.PROPERTY_SAVE_TO_HISTORY,
                ChatNotificationUtils.VALUE_SAVE_TO_HISTORY);

        // for IOS additional property
        chatMessage.setProperty(ChatNotificationUtils.PROPERTY_SENDER_NAME,
                PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.FULL_NAME, ""));

        return chatMessage;
    }

    private QBAttachment getAttachment(QBFile file) {
        // TODO temp value
        String contentType = "image/jpeg";

        QBAttachment attachment = new QBAttachment(QBAttachment.PHOTO_TYPE);
        attachment.setId(file.getUid());
        attachment.setName(file.getName());
        attachment.setContentType(contentType);
        attachment.setUrl(file.getPublicUrl());
        attachment.setSize(file.getSize());

        return attachment;
    }




    private class PrivateChatManagerListener implements QBPrivateChatManagerListener {

        @Override
        public void chatCreated(QBPrivateChat privateChat, boolean b) {
            privateChat.addMessageListener(privateChatMessageListener);
        }
    }

    private class PrivateChatMessageListener implements QBMessageListener<QBPrivateChat> {

        @Override
        public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {
            try {
                QBRestChatService.markMessagesAsRead(chatMessage.getDialogId(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ChatNotificationUtils.isNotificationMessage(chatMessage)) {
                for (QBNotificationChatListener notificationChatListener : notificationChatListeners) {
                    notificationChatListener.onReceivedNotification((String) chatMessage.getProperty(
                            ChatNotificationUtils.PROPERTY_NOTIFICATION_TYPE), chatMessage);
                }
            } else {
                onPrivateMessageReceived(privateChat, chatMessage);
            }
        }

        @Override
        public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {
            // TODO: need to be implemented
        }
    }

    // group chat

    public void onGroupMessageReceived(QBChat groupChat, final QBChatMessage chatMessage) {
    }

    private class GroupChatMessageListener implements QBMessageListener<QBGroupChat> {

        @Override
        public void processMessage(QBGroupChat groupChat, QBChatMessage chatMessage) {
            onGroupMessageReceived(groupChat, chatMessage);
        }

        @Override
        public void processError(QBGroupChat groupChat, QBChatException error, QBChatMessage originMessage) {

        }
    }

    protected MessageModel parseReceivedMessage(QBChatMessage qbChatMessage) {
        long dateSent = ChatUtils.getMessageDateSent(qbChatMessage);
        String attachUrl = ChatUtils.getAttachUrlIfExists(qbChatMessage);
        String dialogId = (String) qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_DIALOG_ID);

        MessageModel message = new MessageModel();
        message.setMessageId(qbChatMessage.getId());
        message.setBody(qbChatMessage.getBody());
        message.setDateSent(dateSent);
        message.setState(State.DELIVERED);
        message.setSenderId(qbChatMessage.getSenderId());

        DialogUserModel dialogOccupant = dataManager.getDialogUsersRepository().get(new PageQuery().add("dialogId", dialogId).add("userId", qbChatMessage.getSenderId()));
        if (dialogOccupant == null) {
            dialogOccupant = new DialogUserModel();
            DialogModel dialog = dataManager.getDialogRepository().getByServerId(dialogId);
            if (dialog != null) {
                dialogOccupant.setDialog(dialog);
            }
            ChatUserModel user = dataManager.getChatUserRepository().get(new PageQuery().add("userId", qbChatMessage.getSenderId()));
            if (user != null) {
                dialogOccupant.setChatUser(user);
            }
        }

        message.setDialogUserModel(dialogOccupant);

        if (qbChatMessage.getAttachments()!= null && !qbChatMessage.getAttachments().isEmpty()) {
            AttachmentModel attachment = new AttachmentModel();
            attachment.setType(AttachmentModel.Type.PHOTO);
            attachment.setURL(attachUrl);
            message.setAttachment(attachment);
        }

        return message;
    }

    public void sendSystemMessage(QBChatMessage chatMessage, int opponentId, String dialogId) {
        addNecessaryPropertyForQBChatMessage(chatMessage, dialogId);
        chatMessage.setRecipientId(opponentId);
        try {
            systemMessagesManager.sendSystemMessage(chatMessage);
        } catch (SmackException.NotConnectedException e) {
            ErrorUtils.logError(e);
        }
    }
}
