package com.trutek.looped.chatmodule.helpers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.State;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatNotificationUtils;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.chatmodule.utils.DbUtils;
import com.trutek.looped.msas.common.contracts.PageQuery;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by msas on 9/15/2016.
 */
public class PrivateChatHelper extends BaseChatHelper{

    private static final String TAG = PrivateChatHelper.class.getSimpleName();

    public PrivateChatHelper(Context context) {
        super(context);
        QBNotificationChatListener notificationChatListener = new PrivateChatNotificationListener();
        addNotificationChatListener(notificationChatListener);
    }

    public void init(QBUser user) {
        super.init(user);
    }


    public void sendPrivateMessage(String message, int userId) throws QBResponseException {
        sendPrivateMessage(null, message, userId);
    }

    private void sendPrivateMessage(QBFile file, String message, int userId) throws QBResponseException {
        QBChatMessage qbChatMessage = getQBChatMessage(message, file);
        String dialogId = null;
        if (currentDialog != null) {
            dialogId = currentDialog.getDialogId();
        }
        sendPrivateMessage(qbChatMessage, userId, dialogId);
    }
    @Override
    public synchronized void closeChat(QBChatDialog qbDialog, Bundle additional) {
        Log.d("Fix double message", "closeChat " + PrivateChatHelper.class.getSimpleName());
        if (currentDialog != null && currentDialog.getDialogId().equals(qbDialog.getDialogId())) {
            currentDialog = null;
        }
    }

    @Override
    public synchronized QBPrivateChat createChatLocally(QBChatDialog dialog, Bundle additional) throws QBResponseException {
        Log.d("Fix double message", "createChatLocally from " + PrivateChatHelper.class.getSimpleName());
        Log.d("Fix double message", "dialog = " + dialog);
        currentDialog = dialog;
        int opponentId = additional.getInt(QuickBloxServiceConsts.EXTRA_OPPONENT_ID);
        return createPrivateChatIfNotExist(opponentId);
    }

    public void onPrivateMessageReceived(QBChat chat, QBChatMessage qbChatMessage) {
        Log.e(TAG, "onPrivateMessageReceive");
        String dialogId = (String) qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_DIALOG_ID);
        if (qbChatMessage.getId() != null && dialogId != null) {
            ChatUserModel user = dataManager.getChatUserRepository().get(new PageQuery().add("userId", qbChatMessage.getSenderId()));
            DialogModel dialog = dataManager.getDialogRepository().getByServerId(dialogId);
            if (dialog == null) {
                final QBChatDialog qbDialog = ChatNotificationUtils.parseDialogFromQBMessage(context, qbChatMessage, QBDialogType.PRIVATE);
                ChatUtils.addOccupantsToQBDialog(qbDialog, qbChatMessage);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DbUtils.saveDialogToCache(dataManager, qbDialog);
                    }
                }).start();

            }
            DbUtils.saveMessageOrNotificationToCache(context, dataManager, dialogId, qbChatMessage, State.DELIVERED, true);
            DbUtils.updateDialogModifiedDate(dataManager, dialogId, ChatUtils.getMessageDateSent(qbChatMessage), true);

            checkForSendingNotification(false, qbChatMessage, user, true);
        }
    }

    private class PrivateChatNotificationListener implements QBNotificationChatListener {

        @Override
        public void onReceivedNotification(String notificationTypeString, QBChatMessage chatMessage) {

        }
    }

    public void sendPrivateMessageWithAttachImage(QBFile file, int userId) throws QBResponseException {
        sendPrivateMessage(file, context.getString(R.string.dlg_attached_last_message), userId);
    }

    public QBFile loadAttachFile(File inputFile) throws Exception {
        QBFile file = null;

        try {
            file = QBContent.uploadFileTask(inputFile, true, (String) null).perform();
        } catch (QBResponseException exc) {
            exc.printStackTrace();
            throw new Exception(context.getString(R.string.dlg_fail_upload_attach));
        }

        return file;
    }

    public QBChatDialog createPrivateDialogIfNotExist(int userId) throws QBResponseException {
        QBChatDialog existingPrivateDialog = ChatUtils.getExistPrivateDialog(dataManager, userId);
        if (existingPrivateDialog == null) {
            existingPrivateDialog = createPrivateChatOnRest(userId);
            DbUtils.saveDialogToCache(dataManager, existingPrivateDialog);
        }
        return existingPrivateDialog;
    }

    public QBChatDialog createPrivateChatOnRest(int opponentId) throws QBResponseException {
        QBUser qbUser = new QBUser(opponentId);
        QBChatDialog dialog = privateChatManager.createDialog(qbUser);
        return dialog;
    }

}
