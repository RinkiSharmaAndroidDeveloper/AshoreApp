package com.trutek.looped.chatmodule.utils;

import android.content.Context;

import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.trutek.looped.chatmodule.data.contracts.models.AttachmentModel;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.contracts.models.State;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.helpers.RestHelper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by msas on 9/15/2016.
 */
public class DbUtils {

    public static void saveMessageOrNotificationToCache(Context context, DataManager dataManager, String dialogId, QBChatMessage qbChatMessage,
                                                        State state, boolean notify) {
        PageQuery query = new PageQuery();
        DialogUserModel dialogOccupant;
        if (qbChatMessage.getSenderId() == null) {
            dialogOccupant = dataManager.getDialogUsersRepository().get(query.add("dialogId", dialogId).add("userId", AppSession.getSession().getUser().getQbId()));
        } else {
            dialogOccupant = dataManager.getDialogUsersRepository().get(query.add("dialogId", dialogId).add("userId", qbChatMessage.getSenderId()));
        }

        if (dialogOccupant == null && qbChatMessage.getSenderId() != null) {
            saveDialogUserIfUserNotExists(dataManager, dialogId, qbChatMessage.getSenderId(), DialogUserModel.Status.DELETED);
            dialogOccupant = dataManager.getDialogUsersRepository().get(query.add("dialogId", dialogId).add("userId", qbChatMessage.getSenderId()));
        }

        if (ChatNotificationUtils.isNotificationMessage(qbChatMessage)) {
            saveDialogNotificationToCache(context, dataManager, dialogOccupant, qbChatMessage, notify);
        } else {
            MessageModel message = ChatUtils.createLocalMessage(qbChatMessage, dialogOccupant, state);
            if (qbChatMessage.getAttachments() != null && !qbChatMessage.getAttachments().isEmpty()) {
                ArrayList<QBAttachment> attachmentsList = new ArrayList<QBAttachment>(
                        qbChatMessage.getAttachments());
                AttachmentModel attachment = ChatUtils.createLocalAttachment(attachmentsList.get(0));
                message.setAttachment(attachment);

                dataManager.getAttachmentRepository().create(attachment, null);
            }

            dataManager.getMessageRepository().create(message,null);
        }
    }

    public static void saveDialogNotificationToCache(Context context, DataManager dataManager,
                                                     DialogUserModel dialogOccupant, QBChatMessage qbChatMessage, boolean notify) {
        DialogNotificationModel dialogNotification = ChatUtils.createLocalDialogNotification(context, dataManager,
                qbChatMessage, dialogOccupant);
        saveDialogNotificationToCache(dataManager, dialogNotification, notify);
    }

    private static void saveDialogNotificationToCache(DataManager dataManager,
                                                      DialogNotificationModel dialogNotification, boolean notify) {
        if (dialogNotification.getDialogOccupant() != null) {
            DialogNotificationModel model = dataManager.getDialogNotificationRepository().get(new PageQuery().add("notificationId", dialogNotification.getServerId()));
            if (model == null){
                dataManager.getDialogNotificationRepository().create(dialogNotification,null);
            } else {
                dialogNotification.setId(model.getId());
                dataManager.getDialogNotificationRepository().update(model.getId(), dialogNotification,null);
            }
        }
    }

    public static DialogUserModel saveDialogUserIfUserNotExists(DataManager dataManager, String dialogId, int userId,
                                                                DialogUserModel.Status status) {
        PageQuery query = new PageQuery();
        RestHelper.loadAndSaveUser(dataManager, userId);

        ChatUserModel user = dataManager.getChatUserRepository().get(query.add("userId", userId));
        DialogUserModel dialogUser = ChatUtils.createDialogOccupant(dataManager, dialogId, user);
        dialogUser.setUserStatus(status);

        saveDialogOccupant(dataManager, dialogUser);

        return dialogUser;
    }

    public static void saveDialogOccupant(DataManager dataManager, DialogUserModel dialogUser) {
        dataManager.getDialogUsersRepository().create(dialogUser,null);
    }

    public static void updateDialogModifiedDate(DataManager dataManager, String dialogId, QBChatMessage qbChatMessage) {
        DialogModel dialog = dataManager.getDialogRepository().getByServerId(dialogId);
        long modifiedDate = ChatUtils.getMessageDateSent(qbChatMessage);
        updateDialogModifiedDate(dataManager, dialog, modifiedDate);
        updateDialogLastMessage(dataManager, dialog, qbChatMessage.getBody());
    }

    private static void updateDialogModifiedDate(DataManager dataManager, DialogModel dialog, long modifiedDate) {
        if (dialog != null) {
            dialog.setLastMessageDateSent(modifiedDate);
            dataManager.getDialogRepository().update(dialog.getId(), dialog,null);
        }
    }

    private static void updateDialogLastMessage(DataManager dataManager, DialogModel dialog, String lastMessage) {
        if (dialog != null) {
            dialog.setLastMessage(lastMessage);
            dataManager.getDialogRepository().update(dialog.getId(), dialog,null);
        }
    }


    public static void saveDialogsToCache(DataManager dataManager, List<QBChatDialog> qbDialogsList) {

        List<DialogModel> dialogs = ChatUtils.createLocalDialogsList(qbDialogsList);
        for (DialogModel dialog : dialogs) {
            DialogModel localModel = dataManager.getDialogRepository().getByServerId(dialog.getServerId());
        if(localModel == null){
            dataManager.getDialogRepository().create(dialog,null);
        } else {
            dialog.setId(localModel.getId());
            dataManager.getDialogRepository().update(dialog.getId(), dialog,null);
        }
    }

        saveDialogsOccupants(dataManager, qbDialogsList);
//        saveTempMessages(dialogService, qbDialogsList, currentDialog);
    }

    public static void saveDialogsOccupants(DataManager dataManager,  List<QBChatDialog> qbDialogsList) {
        for (QBChatDialog qbDialog : qbDialogsList) {
            saveDialogsOccupants(dataManager, qbDialog, false);
        }
    }

    public static List<DialogUserModel> saveDialogsOccupants(DataManager dataManager, QBChatDialog qbDialog, boolean onlyNewOccupant) {
        List<DialogUserModel> dialogOccupantsList = ChatUtils.createDialogOccupantsList(dataManager, qbDialog, onlyNewOccupant);
        if (!dialogOccupantsList.isEmpty()) {
            for (DialogUserModel model : dialogOccupantsList){
                dataManager.getDialogUsersRepository().create(model,null);
            }
        }
        return dialogOccupantsList;
    }

    public static void updateDialogsOccupantsStatusesIfNeeded(DataManager dataManager, List<QBChatDialog> qbDialogsList) {
        for (QBChatDialog qbDialog : qbDialogsList) {
            updateDialogOccupantsStatusesIfNeeded(dataManager, qbDialog);
        }
    }

    public static void updateDialogOccupantsStatusesIfNeeded(DataManager dataManager, QBChatDialog qbDialog) {
        PageInput input = new PageInput();
        input.query.add("dialogId", qbDialog.getDialogId());
        List<DialogUserModel> oldDialogOccupantsList = dataManager.getDialogUsersRepository().page(input).items;
        List<DialogUserModel> updatedDialogOccupantsList = new ArrayList<>();
//        List<DialogUserModel> newDialogOccupantsList = dataManager.getDialogUsersRepository().getActualDialogOccupantsByIds(
//                qbDialog.getDialogId(), qbDialog.getOccupants());

        for (DialogUserModel oldDialogOccupant : oldDialogOccupantsList) {
//            if (!newDialogOccupantsList.contains(oldDialogOccupant)) {
//                oldDialogOccupant.setUserStatus(DialogUserModel.Status.DELETED);
//                updatedDialogOccupantsList.add(oldDialogOccupant);
//            }
        }

        if (!updatedDialogOccupantsList.isEmpty()) {
            for (DialogUserModel model : updatedDialogOccupantsList){
                dataManager.getDialogUsersRepository().update(model.getId(), model,null);
            }
        }
    }

    public static void deleteDialogLocal(DataManager dataManager, String dialogId) {
        DialogModel dialogModel = dataManager.getDialogRepository().get(new PageQuery().add("dialogId", dialogId));
        dataManager.getDialogRepository().remove(dialogModel.getId());
    }





    /*messages*/


    public static void saveMessagesToCache(Context context,DataManager dataManager, List<QBChatMessage> qbMessagesList, String dialogId) {
        for (int i = 0; i < qbMessagesList.size(); i++) {
            QBChatMessage qbChatMessage = qbMessagesList.get(i);
            boolean notify = i == qbMessagesList.size() - 1;
            saveMessageOrNotificationToCache(context, dataManager, dialogId, qbChatMessage, null, notify);
        }

        updateDialogModifiedDate(dataManager, dialogId, true);
    }

    private static void updateDialogModifiedDate(DataManager dataManager, String dialogId, boolean notify) {
        long modifiedDate = getDialogModifiedDate(dataManager, dialogId);
        updateDialogModifiedDate(dataManager, dialogId, modifiedDate, notify);
    }

    public static long getDialogModifiedDate(DataManager dataManager, String dialogId) {
        PageInput input = new PageInput();
        input.query.add("dialogId", dialogId);
        List<DialogUserModel> dialogOccupantsList = dataManager.getDialogUsersRepository().page(input).items;
        List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogOccupantsList);

//        MaessageModel message = messageService.getLastMessageByDialogId(dialogOccupantsIdsList);
//        return ChatUtils.getDialogMessageCreatedDate(true, message, dialogNotification);
        return new Date().getTime()/1000;
    }

    public static void updateDialogModifiedDate(DataManager dataManager, String dialogId, long modifiedDate,
                                                boolean notify) {
        DialogModel dialog = dataManager.getDialogRepository().getByServerId(dialogId);
        updateDialogModifiedDate(dataManager, dialog, modifiedDate, notify);
    }


    private static void updateDialogModifiedDate(DataManager dataManager, DialogModel dialog, long modifiedDate,
                                                 boolean notify) {
        if (dialog != null) {
            dialog.setLastMessageDateSent(modifiedDate);
            dataManager.getDialogRepository().update(dialog.getId(), dialog,null);
        }
    }

    public static DialogUserModel saveDialogOccupantIfUserNotExists(DataManager dataManager, String dialogId,
                                                                    int userId, DialogUserModel.Status status) {
        RestHelper.loadAndSaveUser(dataManager, userId);

        ChatUserModel user = dataManager.getChatUserRepository().get(new PageQuery().add("userId", userId));
        DialogUserModel dialogUserModel = ChatUtils.createDialogOccupant(dataManager, dialogId, user);
        dialogUserModel.setUserStatus(status);

        saveDialogOccupant(dataManager, dialogUserModel);

        return dialogUserModel;
    }

    public static void saveDialogToCache(DataManager dataManager, QBChatDialog qbDialog) {
        DialogModel dialog = ChatUtils.createLocalDialog(qbDialog);
        DialogModel model = dataManager.getDialogRepository().getByServerId(dialog.getDialogId());
        if(model == null){
            dataManager.getDialogRepository().create(dialog,null);
        } else {
            dialog.setId(model.getId());
            dataManager.getDialogRepository().update(model.getId(), dialog,null);
        }

        if (qbDialog.getOccupants() != null && !qbDialog.getOccupants().isEmpty()) {
            saveDialogsOccupants(dataManager, qbDialog, false);
        }
    }

    public static void saveTempMessage(DataManager dataManager, MessageModel message) {
        MessageModel messageModel = dataManager.getMessageRepository().getByServerId(message.getMessageId());
        if (messageModel == null){
            dataManager.getMessageRepository().create(message,null);
        } else {
            message.setId(messageModel.getId());
            dataManager.getMessageRepository().update(messageModel.getId(), message,null);
        }

        updateDialogModifiedDate(dataManager, message.getDialogUserModel().getDialog().getDialogId(),
                message.getDateSent(), true);
    }

    public static void updateDialogOccupants(DataManager dataManager, String dialogId,
                                             List<Integer> dialogOccupantIdsList, DialogUserModel.Status status) {
        List<DialogUserModel> dialogOccupantsList = ChatUtils.
                getUpdatedDialogOccupantsList(dataManager, dialogId, dialogOccupantIdsList, status);

        for (DialogUserModel dialogUserModel : dialogOccupantsList) {
            DialogUserModel model = dataManager.getDialogUsersRepository().get(new PageQuery().add("dialogId", dialogUserModel.getDialogId()).add("userId", dialogUserModel.getUserId()));
            if(model == null){
                dataManager.getDialogUsersRepository().create(dialogUserModel,null);
            } else {
                dataManager.getDialogUsersRepository().update(dialogUserModel.getId(), dialogUserModel,null);
            }
        }
    }

}