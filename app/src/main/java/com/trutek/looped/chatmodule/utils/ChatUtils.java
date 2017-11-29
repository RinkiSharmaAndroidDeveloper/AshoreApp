package com.trutek.looped.chatmodule.utils;

import android.content.Context;
import android.text.TextUtils;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.data.contracts.models.AttachmentModel;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.CombinationMessage;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.contracts.models.NotificationType;
import com.trutek.looped.chatmodule.data.contracts.models.ParcelableQBDialog;
import com.trutek.looped.chatmodule.data.contracts.models.State;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.helpers.RestHelper;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.data.impl.entities.DialogNotification;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.models.UserModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChatUtils {

    public static final String OCCUPANT_IDS_DIVIDER = ",";

    public static ChatUserModel getOpponentFromPrivateDialog(ChatUserModel currentUser, List<DialogUserModel> occupantsList) {
        for (DialogUserModel dialogOccupant : occupantsList) {
            if (dialogOccupant != null && currentUser.getUserId() != dialogOccupant.getUserId()) {
                return dialogOccupant.getChatUser();
            }
        }
        return new ChatUserModel();
    }

    public static long getMessageDateSent(QBChatMessage qbChatMessage) {
        long dateSent;
        String dateSentString = (String) qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_DATE_SENT);
        try {
            dateSent = dateSentString != null ? Long.parseLong(dateSentString) : qbChatMessage.getDateSent();
        } catch (NumberFormatException e) {
            dateSent = DateUtilsCore.getCurrentTime();
        }
        return dateSent;
    }

    public static MessageModel createLocalMessage(QBChatMessage qbChatMessage, DialogUserModel dialogOccupant, State state) {
        long dateSent = getMessageDateSent(qbChatMessage);
        MessageModel message = new MessageModel();
        message.setMessageId(qbChatMessage.getId());
        message.setDialogUsers(dialogOccupant);
        message.setDateSent(dateSent);
//        message.setState(qbChatMessage.isRead() ? State.READ : state);
        message.setBody(qbChatMessage.getBody());
        if(qbChatMessage.getSenderId() != null)
            message.setSenderId(qbChatMessage.getSenderId());
        else
            message.setSenderId(dialogOccupant.getUserId());
        message.setRecipientId(qbChatMessage.getRecipientId());
        if(qbChatMessage.getDialogId() != null)
            message.setDialogId(qbChatMessage.getDialogId());
        else
            message.setDialogId(dialogOccupant.getDialogId());
        return message;
    }

    public static AttachmentModel createLocalAttachment(QBAttachment qbAttachment) {
        AttachmentModel attachment = new AttachmentModel();
        attachment.setAttachmentId(qbAttachment.getId());
        attachment.setURL(qbAttachment.getUrl());
        attachment.setName(qbAttachment.getName());
        attachment.setSize(qbAttachment.getSize());
        attachment.setType(AttachmentModel.Type.valueOf(qbAttachment.getType().toUpperCase()));
        return attachment;
    }

    public static DialogUserModel createDialogOccupant(DataManager dataManager, String dialogId, ChatUserModel chatUser) {
        DialogUserModel dialogOccupant = new DialogUserModel();
        dialogOccupant.setChatUser(chatUser);
        dialogOccupant.setDialog(dataManager.getDialogRepository().getByServerId(dialogId));
        return dialogOccupant;
    }

    public static List<ParcelableQBDialog> qbDialogsToParcelableQBDialogs(List<QBChatDialog> dialogList){
        List<ParcelableQBDialog> parcelableDialogList = new ArrayList<ParcelableQBDialog>(dialogList.size());
        for (QBChatDialog dialog : dialogList) {
            ParcelableQBDialog parcelableQBDialog = new ParcelableQBDialog(dialog);
            parcelableDialogList.add(parcelableQBDialog);
        }
        return parcelableDialogList;
    }

    public static List<DialogModel> createLocalDialogsList(List<QBChatDialog> qbDialogsList) {
        List<DialogModel> dialogsList = new ArrayList<>(qbDialogsList.size());

        for (QBChatDialog qbDialog : qbDialogsList) {
            dialogsList.add(createLocalDialog(qbDialog));
        }

        return dialogsList;
    }


    public static MessageModel createTempLocalMessage(DialogNotificationModel dialogNotification) {
        MessageModel message = new MessageModel();
        message.setMessageId(dialogNotification.getNotificationId());
        message.setDialogUserModel(dialogNotification.getDialogOccupant());
        message.setState(State.TEMP_LOCAL_UNREAD);
        message.setBody(dialogNotification.getBody());
        message.setDateSent(dialogNotification.getCreatedDate());
        return message;
    }

    public static DialogModel createLocalDialog(QBChatDialog qbDialog) {
        DialogModel dialog = new DialogModel();
        dialog.setDialogId(qbDialog.getDialogId());
        dialog.setXmppRoomJid(qbDialog.getRoomJid());
        dialog.setName(qbDialog.getName());
        dialog.setLastMessage(qbDialog.getLastMessage());
        dialog.setUnreadMessagesCount(qbDialog.getUnreadMessageCount() == null ? 0 : qbDialog.getUnreadMessageCount());
        dialog.setLastMessageUserId(qbDialog.getLastMessageUserId());
        dialog.setUserId(qbDialog.getUserId());
        dialog.setImageUrl(qbDialog.getPhoto());
        if (qbDialog.getUpdatedAt() != null) {
            dialog.setTimeStamp(qbDialog.getUpdatedAt());
        }
        dialog.setLastMessageDateSent(qbDialog.getLastMessageDateSent());

        if (QBDialogType.PRIVATE.equals(qbDialog.getType())) {
            dialog.setType(DialogModel.Type.PRIVATE);
        } else if (QBDialogType.GROUP.equals(qbDialog.getType())){
            dialog.setType(DialogModel.Type.GROUP);
        }

        return dialog;
    }

    public static List<DialogUserModel> createDialogOccupantsList(DataManager dataManager, QBChatDialog qbDialog, boolean onlyNewOccupant) {
        List<DialogUserModel> dialogOccupantsList = new ArrayList<>(qbDialog.getOccupants().size());

        for (Integer userId : qbDialog.getOccupants()) {
            DialogUserModel dialogOccupant;
            if (onlyNewOccupant) {
                PageQuery query = new PageQuery();
                query.add("dialogId", qbDialog.getDialogId());
                query.add("userId", userId);
                dialogOccupant = dataManager.getDialogUsersRepository().get(query);
                if (dialogOccupant == null) {
                    dialogOccupant = createDialogOccupant(dataManager, qbDialog.getDialogId(), dataManager.getChatUserRepository().get(new PageQuery().add("userId", userId)));
                } else {
                    dialogOccupant.setUserStatus(DialogUserModel.Status.ACTUAL);
                }
                dialogOccupantsList.add(dialogOccupant);
            } else {
                PageQuery query = new PageQuery();
                query.add("dialogId", qbDialog.getDialogId());
                query.add("userId", userId);
                dialogOccupant = dataManager.getDialogUsersRepository().get(query);
                if (dialogOccupant == null) {
                    ChatUserModel user = dataManager.getChatUserRepository().get(new PageQuery().add("userId", userId));
                    if (user == null) {
                        user = RestHelper.loadAndSaveUser(dataManager, userId);
                    }
                    dialogOccupant = createDialogOccupant(dataManager, qbDialog.getDialogId(), user);
                    dialogOccupantsList.add(dialogOccupant);
                }
            }
        }

        return dialogOccupantsList;
    }

    /*messages*/

    public static List<Long> getIdsFromDialogOccupantsList(List<DialogUserModel> dialogOccupantsList) {
        List<Long> idsList = new ArrayList<>(dialogOccupantsList.size());

        for (DialogUserModel dialogOccupant : dialogOccupantsList) {
            idsList.add(dialogOccupant.getId());
        }

        return idsList;
    }

    public static QBChatDialog createQBDialogFromLocalDialog(DataManager dataManager, DialogModel dialog) {
        List<DialogUserModel> dialogOccupantsList = dataManager.getDialogUsersRepository().page(new PageInput(new PageQuery().add("dialogId", dialog.getDialogId()))).items;
        QBChatDialog qbDialog = createQBDialogFromLocalDialog(dialog, dialogOccupantsList);
        return qbDialog;
    }

    private static QBChatDialog createQBDialogFromLocalDialog(DialogModel dialog, List<DialogUserModel> dialogOccupantsList) {
        QBChatDialog qbDialog = new QBChatDialog();
        qbDialog.setDialogId(dialog.getDialogId());
        qbDialog.setRoomJid(dialog.getXmppRoomJid());
        qbDialog.setPhoto(dialog.getImageUrl());
        qbDialog.setName(dialog.getName());
        qbDialog.setUserId(dialog.getUserId());
        qbDialog.setLastMessageDateSent(dialog.getLastMessageDateSent());
        qbDialog.setLastMessage(dialog.getLastMessage());
        qbDialog.setOccupantsIds(createOccupantsIdsFromDialogOccupantsList(dialogOccupantsList));
        qbDialog.setType(
                DialogModel.Type.PRIVATE.equals(dialog.getType()) ? QBDialogType.PRIVATE : QBDialogType.GROUP);
        qbDialog.setUpdatedAt(dialog.getTimeStamp());
        return qbDialog;
    }

    public static ArrayList<Integer> createOccupantsIdsFromDialogOccupantsList(
            List<DialogUserModel> dialogOccupantsList) {
        ArrayList<Integer> occupantsIdsList = new ArrayList<>(dialogOccupantsList.size());
        for (DialogUserModel dialogOccupant : dialogOccupantsList) {
            occupantsIdsList.add(dialogOccupant.getUserId());
        }
        return occupantsIdsList;
    }

    public static long getDialogMessageCreatedDate(boolean lastMessage, MessageModel message, DialogNotificationModel dialogNotification) {
        long createdDate = 0;

        if (message == null && dialogNotification == null) {
            createdDate = 0;
        } else if (message != null && dialogNotification != null) {
            createdDate = lastMessage
                    ? (message.getDateSent() > dialogNotification.getCreatedDate() ? message.getDateSent() : dialogNotification.getCreatedDate())
                    : (message.getDateSent() < dialogNotification.getCreatedDate() ? message.getDateSent() : dialogNotification.getCreatedDate());
        } else if (message != null && dialogNotification == null) {
            createdDate = message.getDateSent();
        } else if (dialogNotification != null && message == null) {
            createdDate = dialogNotification.getCreatedDate();
        }

        return createdDate;
    }

    public static List<Integer> getOccupantsIdsListFromString(String occupantIds) {
        List<Integer> occupantIdsList = new ArrayList<Integer>();
        String[] occupantIdsArray = occupantIds.split(OCCUPANT_IDS_DIVIDER);
        for (String occupantId : occupantIdsArray) {
            occupantIdsList.add(Integer.valueOf(occupantId));
        }
        return occupantIdsList;
    }

    public static String getRoomJid(String dialogId) {
        return QBSettings.getInstance().getApplicationId()
                .concat("_")
                .concat(dialogId)
                .concat(Constants.CHAT_MUC)
                .concat(QBSettings.getInstance().getChatServerDomain());
    }

    public static void addOccupantsToQBDialog(QBChatDialog qbDialog, QBChatMessage qbChatMessage) {
        qbDialog.setOccupantsIds(new ArrayList<Integer>(2));
        qbDialog.getOccupants().add(qbChatMessage.getSenderId());
        qbDialog.getOccupants().add(qbChatMessage.getRecipientId());
    }

    public static DialogNotificationModel createLocalDialogNotification(Context context, DataManager dataManager, QBChatMessage qbChatMessage, DialogUserModel dialogOccupant) {
        DialogNotificationModel dialogNotification = new DialogNotificationModel();
        dialogNotification.setNotificationId(qbChatMessage.getId());
        dialogNotification.setDialogOccupant(dialogOccupant);

        String chatNotificationTypeString = qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_NOTIFICATION_TYPE).toString();

        if (chatNotificationTypeString != null) {

            int chatNotificationTypeInt = Integer.parseInt(chatNotificationTypeString);
            NotificationType chatNotificationType = NotificationType.parseByValue(chatNotificationTypeInt);

            switch (chatNotificationType) {
                case GROUP_CHAT_CREATE:
                case GROUP_CHAT_UPDATE:
                    dialogNotification.setType(
                            ChatNotificationUtils.getUpdateChatLocalNotificationType(qbChatMessage));
                    dialogNotification.setBody(ChatNotificationUtils
                            .getBodyForUpdateChatNotificationMessage(context, dataManager, qbChatMessage, false));
                    break;
            }
        }

        long dateSent = getMessageDateSent(qbChatMessage);
        dialogNotification.setCreatedDate(dateSent);
//        dialogNotification.setState(qbChatMessage.isRead() ? State.READ : State.DELIVERED);
        dialogNotification.setState(State.DELIVERED);
        dialogNotification.setDialogId(qbChatMessage.getDialogId());
        return dialogNotification;
    }

    public static String getFullNamesFromOpponentId(DataManager dataManager, Integer userId,
                                                    String occupantsIdsString) {
        List<Integer> occupantsIdsList = getOccupantsIdsListFromString(occupantsIdsString);
        occupantsIdsList.remove(userId);
        return getFullNamesFromOpponentIdsList(dataManager, occupantsIdsList);
    }

    private static String getFullNamesFromOpponentIdsList(DataManager dataManager, List<Integer> occupantsIdsList) {
        StringBuilder stringBuilder = new StringBuilder(occupantsIdsList.size());
        for (Integer id : occupantsIdsList) {
            stringBuilder.append(getFullNameById(dataManager, id)).append(OCCUPANT_IDS_DIVIDER).append(" ");
        }
        return stringBuilder.toString().substring(0, stringBuilder.length() - 2);
    }

    public static String getFullNameById(DataManager dataManager, int userId) {
        ChatUserModel user = dataManager.getChatUserRepository().get(new PageQuery().add("userId", userId));

        if (user == null) {
            try {
                QBUser qbUser = QBUsers.getUser(userId).perform();
                user = UserFriendUtils.createLocalUser(qbUser);

                ChatUserModel chatUser = dataManager.getChatUserRepository().get(new PageQuery().add("userId", userId));
                if(chatUser == null){
                    dataManager.getChatUserRepository().create(user,null);
                } else {
                    dataManager.getChatUserRepository().update(chatUser.id, user,null);
                }
            } catch (QBResponseException e) {
                ErrorUtils.logError(e);
            }
        }

        return user != null ? user.getName() : "";
    }

    public static String getFullNamesFromOpponentIds(DataManager dataManager, String occupantsIdsString) {
        List<Integer> occupantsIdsList = getOccupantsIdsListFromString(occupantsIdsString);
        return getFullNamesFromOpponentIdsList(dataManager, occupantsIdsList);
    }

    public static List<CombinationMessage> createCombinationMessagesList(List<MessageModel> messagesList,
                                                                         List<DialogNotificationModel> dialogNotificationsList) {
        List<CombinationMessage> combinationMessagesList = new ArrayList<>();
        combinationMessagesList.addAll(getCombinationMessagesListFromMessagesList(messagesList));
        combinationMessagesList.addAll(getCombinationMessagesListFromDialogNotificationsList(
                dialogNotificationsList));
        Collections.sort(combinationMessagesList, new CombinationMessage.DateComparator());
        return combinationMessagesList;
    }

    public static List<CombinationMessage> getCombinationMessagesListFromMessagesList(List<MessageModel> messagesList) {
        List<CombinationMessage> combinationMessagesList = new ArrayList<>(messagesList.size());

        for (MessageModel message : messagesList) {
            combinationMessagesList.add(new CombinationMessage(message));
        }

        return combinationMessagesList;
    }

    public static List<CombinationMessage> getCombinationMessagesListFromDialogNotificationsList(List<DialogNotificationModel> dialogNotificationsList) {
        List<CombinationMessage> combinationMessagesList = new ArrayList<>(dialogNotificationsList.size());

        for (DialogNotificationModel dialogNotification : dialogNotificationsList) {
            combinationMessagesList.add(new CombinationMessage(dialogNotification));
        }

        return combinationMessagesList;
    }

    //new dialog create

    public static QBChatDialog getExistPrivateDialog(DataManager dataManager, int opponentId) {

        PageQuery query = new PageQuery();
        query.add(com.trutek.looped.msas.common.Utils.Constants.QUERY_KEY_OCCUPANT_1_ID, opponentId);
        query.add(com.trutek.looped.msas.common.Utils.Constants.QUERY_KEY_OCCUPANT_2_ID, AppSession.getSession().getUser().getQbId());
        List<DialogUserModel> dialogUserModels = dataManager.getDialogUsersRepository().page(new PageInput(query)).items;

        for (DialogUserModel model: dialogUserModels) {
            DialogModel dialog = dataManager.getDialogRepository().getByServerId(model.getDialogId());
            if(dialog.getType() == DialogModel.Type.PRIVATE){
                return createQBDialogFromLocalDialog(dataManager, dialog);
            }
        }

        return null;
    }

    public static List<Integer> getOccupantIdsWithUser(List<Integer> friendIdsList) {
        UserModel user = AppSession.getSession().getUser();
        ArrayList<Integer> occupantIdsList = new ArrayList<Integer>(friendIdsList);
        occupantIdsList.add(user.getQbId());
        return occupantIdsList;
    }

    public static String getOccupantsIdsStringFromList(Collection<Integer> occupantIdsList) {
        return TextUtils.join(OCCUPANT_IDS_DIVIDER, occupantIdsList);
    }

    public static String getAttachUrlIfExists(QBChatMessage chatMessage) {
        String attachURL = com.trutek.looped.msas.common.Utils.Constants.EMPTY_STRING;
        Collection<QBAttachment> attachmentCollection = chatMessage.getAttachments();
        if (attachmentCollection != null && attachmentCollection.size() > 0) {
            attachURL = getAttachUrlFromMessage(attachmentCollection);
        }
        return attachURL;
    }

    public static String getAttachUrlFromMessage(Collection<QBAttachment> attachmentsCollection) {
        if (attachmentsCollection != null) {
            ArrayList<QBAttachment> attachmentsList = new ArrayList<QBAttachment>(attachmentsCollection);
            if (!attachmentsList.isEmpty()) {
                return attachmentsList.get(0).getUrl();
            }
        }
        return com.trutek.looped.msas.common.Utils.Constants.EMPTY_STRING;
    }

    public static DialogNotificationModel convertMessageToDialogNotification(MessageModel message) {
        DialogNotificationModel dialogNotification = new DialogNotificationModel();
        dialogNotification.setNotificationId(message.getMessageId());
        dialogNotification.setDialogOccupant(message.getDialogUserModel());
        dialogNotification.setBody(message.getBody());
        dialogNotification.setCreatedDate(message.getDateSent());
        dialogNotification.setState(message.getState());
        return dialogNotification;
    }

    public static List<DialogUserModel> getUpdatedDialogOccupantsList(DataManager dataManager, String dialogId, List<Integer> dialogOccupantIdsList, DialogUserModel.Status status) {
        List<DialogUserModel> updatedDialogOccupantsList = new ArrayList<>(dialogOccupantIdsList.size());

        for (Integer userId : dialogOccupantIdsList) {
            ChatUserModel user = dataManager.getChatUserRepository().get(new PageQuery().add("userId", userId));
            if (user == null) {
                user = RestHelper.loadAndSaveUser(dataManager, userId);
            }

            DialogUserModel dialogOccupant = getUpdatedDialogOccupant(dataManager, dialogId, status, userId);
            if (dialogOccupant == null) {
                dialogOccupant = createDialogOccupant(dataManager, dialogId, user);
                dialogOccupant.setUserStatus(status);
            }

            updatedDialogOccupantsList.add(dialogOccupant);
        }

        return updatedDialogOccupantsList;
    }

    public static DialogUserModel getUpdatedDialogOccupant(DataManager dataManager, String dialogId,
                                                          DialogUserModel.Status status, Integer userId) {
        DialogUserModel dialogOccupant = dataManager.getDialogUsersRepository().get(new PageQuery().add("dialogId", dialogId).add("userId", userId));
        if (dialogOccupant != null) {
            dialogOccupant.setUserStatus(status);
        }
        return dialogOccupant;
    }

    public static QBChatDialog createQBDialogFromLocalDialogWithoutLeaved(DataManager dataManager, DialogModel dialog) {
        PageInput input = new PageInput();
        input.query.add("dialogId", dialog.getDialogId());
//        input.query.add("userStatus", DialogUserModel.Status.ACTUAL.name());

        List<DialogUserModel> dialogOccupantsList = dataManager.getDialogUsersRepository()
                .page(input).items;
        QBChatDialog qbDialog = createQBDialogFromLocalDialog(dialog, dialogOccupantsList);
        return qbDialog;
    }

    public static ArrayList<Integer> createOccupantsIdsFromUsersList(
            List<ChatUserModel> usersList) {
        ArrayList<Integer> occupantsIdsList = new ArrayList<>(usersList.size());
        for (ChatUserModel user : usersList) {
            occupantsIdsList.add(user.getUserId());
        }
        return occupantsIdsList;
    }
}
