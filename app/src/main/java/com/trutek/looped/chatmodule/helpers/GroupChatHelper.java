package com.trutek.looped.chatmodule.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBParticipantListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.contracts.models.NotificationType;
import com.trutek.looped.chatmodule.data.contracts.models.State;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatNotificationUtils;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.chatmodule.utils.DbUtils;
import com.trutek.looped.chatmodule.utils.FinderUnknownUsers;
import com.trutek.looped.chatmodule.utils.Utils;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupChatHelper extends BaseChatHelper {

    private static final String TAG = GroupChatHelper.class.getSimpleName();

    private QBParticipantListener participantListener;
    private List<QBChatDialog> groupDialogsList;

    public GroupChatHelper(Context context) {
        super(context);
    }

    public void init(QBUser user) {
        super.init(user);
        addSystemMessageListener(new SystemMessageListener());
    }

    @Override
    public synchronized QBChat createChatLocally(QBChatDialog dialog, Bundle additional) throws QBResponseException {
        Log.d("Fix double message", "createChatLocally from " + GroupChatHelper.class.getSimpleName());
        Log.d("Fix double message", "dialog = " + dialog);
        currentDialog = dialog;
        QBGroupChat roomChat = createGroupChatIfNotExist(dialog);
        roomChat.addParticipantListener(participantListener);
        return roomChat;
    }

    @Override
    public synchronized void closeChat(QBChatDialog qbDialog, Bundle additional) {
        Log.d("Fix double message", "closeChat from " + GroupChatHelper.class.getSimpleName());
        if (currentDialog != null && currentDialog.getDialogId().equals(qbDialog.getDialogId())) {
            currentDialog = null;
        }
    }

    public void sendGroupMessage(String roomJidId, String message) throws Exception {
        QBChatMessage chatMessage = getQBChatMessage(message, null);
        sendGroupMessage(chatMessage, roomJidId, currentDialog.getDialogId());
    }

    private void sendGroupMessage(QBChatMessage chatMessage, String roomJId, String dialogId) throws QBResponseException {
        QBGroupChat groupChat = groupChatManager.getGroupChat(roomJId);
        QBChatDialog existingDialog = null;
        if (groupChat == null) {
            existingDialog = ChatUtils.createQBDialogFromLocalDialog(dataManager, dataManager.getDialogRepository().get(new PageQuery().add("dialogId", dialogId)));
            groupChat = (QBGroupChat) createChatLocally(existingDialog, null);
        }
        String error = null;

        addNecessaryPropertyForQBChatMessage(chatMessage, dialogId);

        try {
            groupChat.sendMessage(chatMessage);
        } catch (SmackException.NotConnectedException e) {
            error = context.getString(R.string.dlg_fail_connection);
        } catch (IllegalStateException e) {
            existingDialog = ChatUtils.createQBDialogFromLocalDialog(dataManager, dataManager.getDialogRepository().get(new PageQuery().add("dialogId", dialogId)));
            tryJoinRoomChat(existingDialog);
            throw new QBResponseException(e.getMessage());
        }
        if (error != null) {
            throw new QBResponseException(error);
        }
    }

    public void sendGroupMessageWithAttachImage(String roomJidId, QBFile file) throws QBResponseException {
        QBChatMessage chatMessage = getQBChatMessage(context.getString(R.string.dlg_attached_last_message),
                file);
        sendGroupMessage(chatMessage, roomJidId, currentDialog.getDialogId());
    }

    public void tryJoinRoomChats(List<QBChatDialog> qbDialogsList) {
        if (!qbDialogsList.isEmpty()) {
            initGroupDialogsList();
            for (QBChatDialog dialog : qbDialogsList) {
                if (!QBDialogType.PRIVATE.equals(dialog.getType())) {
                    groupDialogsList.add(dialog);
                    tryJoinRoomChat(dialog);
                }
            }
        }
    }

    private void initGroupDialogsList() {
        if (groupDialogsList == null) {
            groupDialogsList = new ArrayList<QBChatDialog>();
        } else {
            groupDialogsList.clear();
        }
    }

    public QBChatDialog createGroupChat(String name, List<Integer> friendIdsList, String photoUrl) throws Exception {
        ArrayList<Integer> occupantIdsList = (ArrayList<Integer>) ChatUtils.getOccupantIdsWithUser(friendIdsList);

        QBChatDialog dialogToCreate = new QBChatDialog();
        dialogToCreate.setName(name);
        dialogToCreate.setType(QBDialogType.GROUP);
        dialogToCreate.setOccupantsIds(occupantIdsList);
        dialogToCreate.setPhoto(photoUrl);

        QBChatDialog qbDialog = QBRestChatService.createChatDialog(dialogToCreate).perform();
        DbUtils.saveDialogToCache(dataManager, qbDialog);

        joinRoomChat(qbDialog);

        sendSystemMessageAboutCreatingGroupChat(qbDialog, friendIdsList);

        QBChatMessage chatMessage = ChatNotificationUtils.createGroupMessageAboutCreateGroupChat(context, dataManager, qbDialog, photoUrl);
        sendGroupMessage(chatMessage, qbDialog.getRoomJid(), qbDialog.getDialogId());

        return qbDialog;
    }

    public void leaveDialogs() throws XMPPException, SmackException.NotConnectedException {
        if (groupDialogsList != null) {
            for (QBChatDialog dialog : groupDialogsList) {
                QBGroupChat roomChat = groupChatManager.getGroupChat(dialog.getRoomJid());
                if (roomChat != null && roomChat.isJoined()) {
                    roomChat.leave();
                }
            }
        }
    }

    protected QBGroupChat createGroupChatIfNotExist(QBChatDialog dialog) throws QBResponseException {
        boolean notNull = Utils.validateNotNull(groupChatManager);
        if (!notNull) {
            ErrorUtils.logError(TAG, " groupChatManager is NULL");
            throw new QBResponseException(context.getString(R.string.dlg_fail_create_chat));
        }
        QBGroupChat groupChat = groupChatManager.getGroupChat(dialog.getRoomJid());
        if (groupChat == null && dialog.getRoomJid() != null) {
            groupChat = groupChatManager.createGroupChat(dialog.getRoomJid());
            groupChat.addMessageListener(groupChatMessageListener);
        }else if(null != groupChat){
            groupChat.addMessageListener(groupChatMessageListener);
        }
        return groupChat;
    }

    public boolean isDialogJoined(DialogModel dialog) {
        QBChatDialog qbDialog = new QBChatDialog();
        qbDialog.setRoomJid(dialog.getXmppRoomJid());

        QBGroupChat roomChat;
        boolean joined = false;
        try {
            roomChat = createGroupChatIfNotExist(qbDialog);
            joined = roomChat.isJoined();
        } catch (QBResponseException e) {
            ErrorUtils.logError(e);
        }
        return joined;
    }

    public boolean isDialogJoined(QBChatDialog dialog) {
        QBChatDialog qbDialog = new QBChatDialog();
        qbDialog.setRoomJid(dialog.getRoomJid());

        QBGroupChat roomChat;
        boolean joined = false;
        try {
            roomChat = createGroupChatIfNotExist(qbDialog);
            joined = roomChat.isJoined();
        } catch (QBResponseException e) {
            ErrorUtils.logError(e);
        }
        return joined;
    }

    public void joinRoomChat(QBChatDialog dialog) throws Exception {
        QBGroupChat roomChat = createGroupChatIfNotExist(dialog);
        if (roomChat != null && !roomChat.isJoined()) {
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0); // without getting messages
            roomChat.join(history);
        }
    }

    public void tryJoinRoomChat(QBChatDialog dialog) {
        try {
            joinRoomChat(dialog);
        } catch (Exception e) {
            ErrorUtils.logError(e);
        }
    }

    public void onGroupMessageReceived(QBChat chat, QBChatMessage qbChatMessage) {
        String dialogId = (String) qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_DIALOG_ID);
        ChatUserModel user = DataManager.getInstance().getChatUserRepository().get(new PageQuery().add("userId", qbChatMessage.getSenderId()));
        MessageModel message = parseReceivedMessage(qbChatMessage);

        String currentlyVisibleDialogID = PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.CURRENTLY_VISIBLE_DIALOG_ID,"");

        boolean ownMessage = !message.isIncoming(chatCreator.getId());

        if (ChatNotificationUtils.isNotificationMessage(qbChatMessage)) {
            DialogNotificationModel dialogNotification = ChatUtils.convertMessageToDialogNotification(message);
            dialogNotification.setType(ChatNotificationUtils.getUpdateChatLocalNotificationType(qbChatMessage));
            dialogNotification.setBody(ChatNotificationUtils.getBodyForUpdateChatNotificationMessage(context, dataManager, qbChatMessage, false));

            if (!ownMessage) {
                updateDialogByNotification(qbChatMessage);
            }
        }

        DbUtils.saveMessageOrNotificationToCache(context, dataManager, dialogId, qbChatMessage, State.DELIVERED, true);
        DbUtils.updateDialogModifiedDate(dataManager, dialogId, ChatUtils.getMessageDateSent(qbChatMessage), true);

        if(!currentlyVisibleDialogID.equals(dialogId) || currentlyVisibleDialogID.isEmpty()) {
            checkForSendingNotification(ownMessage, qbChatMessage, user, false);
        }
    }

    private void updateDialogByNotification(QBChatMessage qbChatMessage) {
        String dialogId = (String) qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_DIALOG_ID);
        DialogModel dialog = dataManager.getDialogRepository().getByServerId(dialogId);
        QBChatDialog qbDialog;
        if (dialog == null) {
            qbDialog = ChatNotificationUtils.parseDialogFromQBMessage(context, qbChatMessage, QBDialogType.GROUP);
        } else {
            qbDialog = ChatUtils.createQBDialogFromLocalDialog(dataManager, dialog);
        }

        ChatNotificationUtils.updateDialogFromQBMessage(context, dataManager, qbChatMessage, qbDialog);
        DbUtils.saveDialogToCache(dataManager, qbDialog);

        notifyUpdatingDialog();
    }

    private void createDialogByNotification(QBChatMessage qbChatMessage, DialogNotificationModel.Type notificationType) {
        qbChatMessage.setBody(context.getString(R.string.cht_notification_message));

        QBChatDialog qbDialog = ChatNotificationUtils.parseDialogFromQBMessage(context, qbChatMessage, qbChatMessage.getBody(), QBDialogType.GROUP);

        qbDialog.getOccupants().add(chatCreator.getId());
        DbUtils.saveDialogToCache(dataManager, qbDialog);

        String roomJidId = qbDialog.getRoomJid();
        if (roomJidId != null) {
            tryJoinRoomChat(qbDialog);
            new FinderUnknownUsers(context, chatCreator, qbDialog).find();
        }

        DialogNotificationModel dialogNotification = ChatUtils.convertMessageToDialogNotification(parseReceivedMessage(qbChatMessage));
        dialogNotification.setType(notificationType);
        MessageModel message = ChatUtils.createTempLocalMessage(dialogNotification);
        DbUtils.saveTempMessage(dataManager, message);

        boolean ownMessage = !message.isIncoming(chatCreator.getId());
        ChatUserModel user = DataManager.getInstance().getChatUserRepository().get(new PageQuery().add("userId", qbChatMessage.getSenderId()));
        checkForSendingNotification(ownMessage, qbChatMessage, user, false);
    }

    protected void notifyUpdatingDialog() {
        Intent intent = new Intent(QuickBloxServiceConsts.UPDATE_DIALOG);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void leaveRoomChat(DialogModel dialog) throws Exception {
        groupChatManager.getGroupChat(dialog.getXmppRoomJid()).leave();
        List<Integer> userIdsList = new ArrayList<Integer>();
        userIdsList.add(chatCreator.getId());
        removeUsersFromDialog(dialog, userIdsList);
    }

    public void removeUsersFromDialog(DialogModel dialog, List<Integer> userIdsList) throws QBResponseException {
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        requestBuilder.pullAll(com.quickblox.chat.Consts.DIALOG_OCCUPANTS, userIdsList.toArray());
        updateDialog(ChatUtils.createQBDialogFromLocalDialog(dataManager, dialog), requestBuilder);
        DataManager.getInstance().getDialogRepository().remove(dialog.getId());
    }

    private QBChatDialog updateDialog(QBChatDialog dialog, QBRequestUpdateBuilder requestBuilder) throws QBResponseException {
        QBChatDialog updatedDialog = QBRestChatService.updateGroupChatDialog(dialog,requestBuilder).perform();
        return updatedDialog;
    }

    public QBChatDialog updateDialog(QBChatDialog dialog) throws QBResponseException {
        return updateDialog(dialog, (QBRequestUpdateBuilder) null);
    }

    public QBChatDialog updateDialog(QBChatDialog dialog, String imageUrl) throws QBResponseException {
        dialog.setPhoto(imageUrl);
        return updateDialog(dialog, (QBRequestUpdateBuilder) null);
    }

    public void sendGroupMessageToFriends(QBChatDialog qbDialog, DialogNotificationModel.Type notificationType,
                                          Collection<Integer> occupantsIdsList, boolean leavedFromDialog, DataManager dataManager) throws QBResponseException {
        QBChatMessage chatMessage = ChatNotificationUtils.createGroupMessageAboutUpdateChat(context, qbDialog,
                notificationType, occupantsIdsList, leavedFromDialog, dataManager);
        sendGroupMessage(chatMessage, qbDialog.getRoomJid(), qbDialog.getDialogId());
    }

    public void sendSystemMessageAboutCreatingGroupChat(QBChatDialog dialog, List<Integer> friendIdsList) throws Exception {
        for (Integer friendId : friendIdsList) {
            try {
                sendSystemMessageAboutCreatingGroupChat(dialog, friendId);
            } catch (QBResponseException e) {
                ErrorUtils.logError(e);
            }
        }
    }

    private void sendSystemMessageAboutCreatingGroupChat(QBChatDialog dialog, Integer friendId) throws Exception {
        QBChatMessage chatMessageForSending = ChatNotificationUtils
                .createSystemMessageAboutCreatingGroupChat(context, dialog);

        addNecessaryPropertyForQBChatMessage(chatMessageForSending, dialog.getDialogId());
        sendSystemMessage(chatMessageForSending, friendId, dialog.getDialogId());
    }

    public QBChatDialog addUsersToDialog(String dialogId, List<Integer> userIdsList) throws Exception {
        QBChatDialog dialog = ChatUtils.createQBDialogFromLocalDialog(dataManager,
                dataManager.getDialogRepository().getByServerId(dialogId));

        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        requestBuilder.push(com.quickblox.chat.Consts.DIALOG_OCCUPANTS, userIdsList.toArray());
        return updateDialog(dialog, requestBuilder);
    }

    private class SystemMessageListener implements QBSystemMessageListener {

        @Override
        public void processMessage(QBChatMessage qbChatMessage) {
            String notificationTypeString = (String) qbChatMessage
                    .getProperty(ChatNotificationUtils.PROPERTY_NOTIFICATION_TYPE);
            NotificationType notificationType = NotificationType.parseByValue(
                    Integer.parseInt(notificationTypeString));
            if (NotificationType.GROUP_CHAT_CREATE.equals(notificationType)) {
                createDialogByNotification(qbChatMessage, DialogNotificationModel.Type.CREATE_DIALOG);
            }
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {
            ErrorUtils.logError(e);
        }
    }

}