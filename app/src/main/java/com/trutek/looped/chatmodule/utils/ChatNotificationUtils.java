package com.trutek.looped.chatmodule.utils;

import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.ChatNotificationType;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.NotificationType;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ChatNotificationUtils {

    public static final String PROPERTY_DIALOG_ID = "dialog_id";
    public static final String PROPERTY_DATE_SENT = "date_sent";
    public static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    public static final String PROPERTY_SENDER_NAME = "senderName";

    public static final String VALUE_SAVE_TO_HISTORY = "1";

    public static final String PROPERTY_ROOM_NAME = "room_name";
    public static final String PROPERTY_ROOM_PHOTO = "room_photo";
    public static final String PROPERTY_ROOM_CURRENT_OCCUPANTS_IDS = "current_occupant_ids";
    public static final String PROPERTY_ROOM_ADDED_OCCUPANTS_IDS = "added_occupant_ids";
    public static final String PROPERTY_ROOM_UPDATED_AT = "room_updated_date";
    public static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    public static final String PROPERTY_ROOM_UPDATE_INFO = "dialog_update_info";
    public static final String PROPERTY_ROOM_DELETED_OCCUPANTS_IDS = "deleted_occupant_ids";
    public static final String PROPERTY_CHAT_TYPE = "type";

    public static final String PROPERTY_MODULE_IDENTIFIER = "moduleIdentifier";
    public static final String VALUE_MODULE_IDENTIFIER = "SystemNotifications";
    public static final String VALUE_GROUP_CHAT_TYPE = "2";

    public static QBChatDialog parseDialogFromQBMessage(Context context, QBChatMessage qbChatMessage, QBDialogType qbDialogType) {
        String dialogId = (String) qbChatMessage.getProperty(PROPERTY_DIALOG_ID);
        String currentOccupantsIdsString = (String) qbChatMessage.getProperty(PROPERTY_ROOM_CURRENT_OCCUPANTS_IDS);
        String addedOccupantsIdsString = (String) qbChatMessage.getProperty(PROPERTY_ROOM_ADDED_OCCUPANTS_IDS);
        String dialogName = (String) qbChatMessage.getProperty(PROPERTY_ROOM_NAME);
        String photoUrl = (String) qbChatMessage.getProperty(PROPERTY_ROOM_PHOTO);
        long dateSent = ChatUtils.getMessageDateSent(qbChatMessage);
        String updatedAtString = (String) qbChatMessage.getProperty(PROPERTY_ROOM_UPDATED_AT);
        String roomJid = ChatUtils.getRoomJid(dialogId);

        QBChatDialog qbDialog = new QBChatDialog(dialogId);
        qbDialog.setRoomJid(roomJid);
        qbDialog.setPhoto(photoUrl);
        qbDialog.setType(qbDialogType);

        qbDialog.setName(dialogName);

        if (!TextUtils.isEmpty(currentOccupantsIdsString)) {
            qbDialog.setOccupantsIds((ArrayList<Integer>) ChatUtils.getOccupantsIdsListFromString(currentOccupantsIdsString));
        } else if (!TextUtils.isEmpty(addedOccupantsIdsString)) {
            qbDialog.setOccupantsIds((ArrayList<Integer>) ChatUtils.getOccupantsIdsListFromString(addedOccupantsIdsString));
        }

        if (!qbChatMessage.getAttachments().isEmpty()) {
            qbDialog.setLastMessage(context.getString(R.string.dlg_attached_last_message));
        } else if (!TextUtils.isEmpty(qbChatMessage.getBody())) {
            qbDialog.setLastMessage(qbChatMessage.getBody());
        }

        qbDialog.setLastMessageDateSent(dateSent);
        qbDialog.setUnreadMessageCount(com.trutek.looped.msas.common.Utils.Constants.ZERO_INT_VALUE);

        if (!TextUtils.isEmpty(updatedAtString)) {
            qbDialog.setUpdatedAt(new Date(Long.parseLong(updatedAtString)));
        }

        return qbDialog;
    }

    public static boolean isNotificationMessage(QBChatMessage qbChatMessage) {
        return qbChatMessage.getProperty(PROPERTY_NOTIFICATION_TYPE) != null;
    }

    public static QBChatMessage createGroupMessageAboutCreateGroupChat(Context context, DataManager dataManager, QBChatDialog qbDialog, String photoUrl) {
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, VALUE_SAVE_TO_HISTORY);
        qbChatMessage.setProperty(PROPERTY_NOTIFICATION_TYPE,
                String.valueOf(NotificationType.GROUP_CHAT_UPDATE.getValue()));
        qbChatMessage.setProperty(PROPERTY_ROOM_CURRENT_OCCUPANTS_IDS,
                ChatUtils.getOccupantsIdsStringFromList(qbDialog.getOccupants()));
        qbChatMessage.setProperty(PROPERTY_ROOM_ADDED_OCCUPANTS_IDS,
                ChatUtils.getOccupantsIdsStringFromList(qbDialog.getOccupants()));
        qbChatMessage.setProperty(PROPERTY_ROOM_UPDATED_AT, String.valueOf(qbDialog.getUpdatedAt().getTime()));
        qbChatMessage.setProperty(PROPERTY_ROOM_UPDATE_INFO, String.valueOf(
                ChatNotificationType.CHAT_OCCUPANTS.getValue()));
        if (photoUrl != null) {
            qbChatMessage.setProperty(PROPERTY_ROOM_PHOTO, photoUrl);
        }
        qbChatMessage.setBody(getBodyForUpdateChatNotificationMessage(context, dataManager, qbChatMessage, true));
        return qbChatMessage;
    }

    public static DialogNotificationModel.Type getUpdateChatLocalNotificationType(QBChatMessage qbChatMessage) {
        String notificationTypeString = (String) qbChatMessage.getProperty(PROPERTY_NOTIFICATION_TYPE);
        String updatedInfo = (String) qbChatMessage.getProperty(PROPERTY_ROOM_UPDATE_INFO);

        NotificationType notificationType = null;
        ChatNotificationType chatNotificationType = null;

        if (notificationTypeString != null) {
            notificationType = NotificationType.parseByValue(Integer.parseInt(notificationTypeString));
        }

        if (updatedInfo != null) {
            chatNotificationType = ChatNotificationType.parseByValue(Integer.parseInt(updatedInfo));
        }

        DialogNotificationModel.Type dialogNotificationTypeLocal = null;

        if (chatNotificationType != null) {
            switch (chatNotificationType) {
                case CHAT_PHOTO:
                    dialogNotificationTypeLocal = DialogNotificationModel.Type.PHOTO_DIALOG;
                    break;
                case CHAT_NAME:
                    dialogNotificationTypeLocal = DialogNotificationModel.Type.NAME_DIALOG;
                    break;
                case CHAT_OCCUPANTS:
                    dialogNotificationTypeLocal = DialogNotificationModel.Type.OCCUPANTS_DIALOG;
                    break;
            }
        }

        if (notificationType != null && chatNotificationType == null
                && notificationType.equals(NotificationType.GROUP_CHAT_CREATE)) {
            dialogNotificationTypeLocal = DialogNotificationModel.Type.CREATE_DIALOG;
        }

        if (notificationType != null && notificationType.equals(NotificationType.GROUP_CHAT_UPDATE)) {
            dialogNotificationTypeLocal = DialogNotificationModel.Type.ADDED_DIALOG;
        }

        return dialogNotificationTypeLocal;
    }

    public static String getBodyForUpdateChatNotificationMessage(Context context, DataManager dataManager,
                                                                 QBChatMessage qbChatMessage, boolean sendFromMe) {
        String notificationTypeString = (String) qbChatMessage.getProperty(PROPERTY_NOTIFICATION_TYPE);
        String updatedInfo = (String) qbChatMessage.getProperty(PROPERTY_ROOM_UPDATE_INFO);
        String addedOccupantsIdsString = (String) qbChatMessage.getProperty(PROPERTY_ROOM_ADDED_OCCUPANTS_IDS);
        String deletedOccupantsIdsString = (String) qbChatMessage.getProperty(PROPERTY_ROOM_DELETED_OCCUPANTS_IDS);
        String dialogName = (String) qbChatMessage.getProperty(PROPERTY_ROOM_NAME);

        NotificationType notificationType = null;
        ChatNotificationType chatNotificationType = null;

        if (notificationTypeString != null) {
            notificationType = NotificationType.parseByValue(Integer.parseInt(notificationTypeString));
        }

        if (updatedInfo != null) {
            chatNotificationType = ChatNotificationType.parseByValue(Integer.parseInt(updatedInfo));
        }

        Resources resources = context.getResources();
        String resultMessage = resources.getString(R.string.cht_notification_message);
        QBUser qbUser = AppSession.getSession().getQbUser();
        boolean ownMessage;
        if(sendFromMe){
            ownMessage = true;
        } else {
            ownMessage = qbUser.getId().equals(qbChatMessage.getSenderId());
        }
        qbUser.setFullName(PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.FULL_NAME, ""));

        if (notificationType != null && notificationType.equals(NotificationType.GROUP_CHAT_CREATE)) {
            String fullNames;

            if (ownMessage) {
                fullNames = ChatUtils.getFullNamesFromOpponentId(dataManager, qbUser.getId(), addedOccupantsIdsString);
                resultMessage = resources.getString(R.string.cht_update_group_added_message, qbUser.getFullName(), fullNames);
            } else {
                fullNames = ChatUtils.getFullNamesFromOpponentId(dataManager, qbChatMessage.getSenderId(), addedOccupantsIdsString);
                resultMessage = resources.getString(R.string.cht_update_group_added_message, ChatUtils.getFullNameById(dataManager,
                        qbChatMessage.getSenderId()), fullNames);
            }

            return resultMessage;
        }

        if (chatNotificationType != null) {
            switch (chatNotificationType) {
                case CHAT_PHOTO:
                    resultMessage = ownMessage ? resources.getString(R.string.cht_update_group_photo_message,
                            qbUser.getFullName()) : resources.getString(R.string.cht_update_group_photo_message,
                            ChatUtils.getFullNameById(dataManager, qbChatMessage.getSenderId()));
                    break;
                case CHAT_NAME:
                    resultMessage = ownMessage ? resources.getString(R.string.cht_update_group_name_message,
                            qbUser.getFullName(), dialogName) : resources.getString(
                            R.string.cht_update_group_name_message, ChatUtils.getFullNameById(dataManager,
                                    qbChatMessage.getSenderId()), dialogName);
                    break;
                case CHAT_OCCUPANTS:
                    String fullNames,myId,other_addedOccupantsIdsString;

                    if (!TextUtils.isEmpty(addedOccupantsIdsString)) {
                        myId = String.valueOf(qbUser.getId());
                        myId +=",";
                        String[] parts = addedOccupantsIdsString.split(myId);
                        other_addedOccupantsIdsString = parts[0];
                        fullNames = ChatUtils.getFullNamesFromOpponentIds(dataManager, other_addedOccupantsIdsString);
                        resultMessage = ownMessage ?
                                resources.getString(R.string.cht_update_group_added_message, qbUser.getFullName(), fullNames)
                                : resources.getString(R.string.cht_update_group_added_message, ChatUtils.getFullNameById(dataManager, qbChatMessage.getSenderId()),
                                fullNames);
                    }

                    if (!TextUtils.isEmpty(deletedOccupantsIdsString)) {
                        fullNames = ChatUtils.getFullNamesFromOpponentIds(dataManager, deletedOccupantsIdsString);
                        resultMessage = ownMessage ?
                                resources.getString(R.string.cht_update_group_leave_message, qbUser.getFullName())
                                : resources.getString(R.string.cht_update_group_leave_message, fullNames);
                    }

                    break;
            }
        }

        return resultMessage;
    }

    public static void updateDialogFromQBMessage(Context context, DataManager dataManager, QBChatMessage qbChatMessage, QBChatDialog qbDialog) {
        String lastMessage = getBodyForUpdateChatNotificationMessage(context, dataManager, qbChatMessage, false);
        String dialogName = (String) qbChatMessage.getProperty(PROPERTY_ROOM_NAME);
        String photoUrl = (String) qbChatMessage.getProperty(PROPERTY_ROOM_PHOTO);
        String updatedInfo = (String) qbChatMessage.getProperty(PROPERTY_ROOM_UPDATE_INFO);
        String currentOccupantsIdsString = (String) qbChatMessage.getProperty(PROPERTY_ROOM_CURRENT_OCCUPANTS_IDS);
        String deletedOccupantsIdsString = (String) qbChatMessage.getProperty(PROPERTY_ROOM_DELETED_OCCUPANTS_IDS);

        ChatNotificationType chatNotificationType = null;

        if (updatedInfo != null) {
            chatNotificationType = ChatNotificationType.parseByValue(Integer.parseInt(updatedInfo));
        }

        if (chatNotificationType != null) {
            switch (chatNotificationType) {
                case CHAT_PHOTO:
                    setDialogPhoto(qbDialog, photoUrl);
                    break;
                case CHAT_NAME:
                    setDialogName(qbDialog, dialogName);
                    break;
                case CHAT_OCCUPANTS:
                    setDialogOccupants(dataManager, qbDialog.getDialogId(), currentOccupantsIdsString, deletedOccupantsIdsString);
                    break;
            }
        }

        qbDialog.setLastMessage(lastMessage);
    }

    private static void setDialogPhoto(QBChatDialog qbDialog, String photoUrl) {
        if (!TextUtils.isEmpty(photoUrl)) {
            qbDialog.setPhoto(photoUrl);
        }
    }

    private static void setDialogName(QBChatDialog qbDialog, String dialogName) {
        if (!TextUtils.isEmpty(dialogName)) {
            qbDialog.setName(dialogName);
        }
    }

    private static void setDialogOccupants(DataManager dataManager, String dialogId,
                                           String currentOccupantsIdsString, String deletedOccupantsIdsString) {
        if (!TextUtils.isEmpty(currentOccupantsIdsString)) {
            setDialogOccupant(dataManager, dialogId, currentOccupantsIdsString, DialogUserModel.Status.ACTUAL);
        }

        if (!TextUtils.isEmpty(deletedOccupantsIdsString)) {
            setDialogOccupant(dataManager, dialogId, deletedOccupantsIdsString, DialogUserModel.Status.DELETED);
        }
    }

    private static void setDialogOccupant(DataManager dataManager, String dialogId, String occupantsIdsString,
                                          DialogUserModel.Status status) {
        List<Integer> occupantsIdsList = ChatUtils.getOccupantsIdsListFromString(occupantsIdsString);
        DbUtils.updateDialogOccupants(dataManager, dialogId, occupantsIdsList, status);
    }

    public static QBChatMessage createGroupMessageAboutUpdateChat(Context context, QBChatDialog qbDialog,
                                                                  DialogNotificationModel.Type notificationType,
                                                                  Collection<Integer> occupantsIdsList, boolean leavedFromChat,
                                                                  DataManager dataManager) {
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, VALUE_SAVE_TO_HISTORY);
        qbChatMessage.setProperty(PROPERTY_NOTIFICATION_TYPE,
                String.valueOf(NotificationType.GROUP_CHAT_UPDATE.getValue()));
        qbChatMessage.setProperty(PROPERTY_ROOM_UPDATED_AT, String.valueOf(qbDialog.getUpdatedAt().getTime()));

        ChatNotificationType chatNotificationType = null;

        switch (notificationType) {
            case CREATE_DIALOG: {
                qbChatMessage.setProperty(PROPERTY_ROOM_CURRENT_OCCUPANTS_IDS,
                        ChatUtils.getOccupantsIdsStringFromList(qbDialog.getOccupants()));
                qbChatMessage.setProperty(PROPERTY_ROOM_ADDED_OCCUPANTS_IDS,
                        ChatUtils.getOccupantsIdsStringFromList(qbDialog.getOccupants()));
                break;
            }
            case ADDED_DIALOG: {
                qbChatMessage.setProperty(PROPERTY_ROOM_CURRENT_OCCUPANTS_IDS,
                        ChatUtils.getOccupantsIdsStringFromList(qbDialog.getOccupants()));
                qbChatMessage.setProperty(PROPERTY_ROOM_ADDED_OCCUPANTS_IDS,
                        ChatUtils.getOccupantsIdsStringFromList(occupantsIdsList));
                chatNotificationType = ChatNotificationType.CHAT_OCCUPANTS;
                break;
            }
            case NAME_DIALOG: {
                qbChatMessage.setProperty(PROPERTY_ROOM_NAME, qbDialog.getName());
                chatNotificationType = ChatNotificationType.CHAT_NAME;
                break;
            }
            case PHOTO_DIALOG: {
                qbChatMessage.setProperty(PROPERTY_ROOM_PHOTO, qbDialog.getPhoto());
                chatNotificationType = ChatNotificationType.CHAT_PHOTO;
                break;
            }
            case OCCUPANTS_DIALOG: {
                if (leavedFromChat) {
                    qbChatMessage.setProperty(PROPERTY_ROOM_DELETED_OCCUPANTS_IDS,
                            ChatUtils.getOccupantsIdsStringFromList(occupantsIdsList));
                } else {
                    qbChatMessage.setProperty(PROPERTY_ROOM_ADDED_OCCUPANTS_IDS,
                            ChatUtils.getOccupantsIdsStringFromList(occupantsIdsList));
                }
                List<Integer> occupantsList = qbDialog.getOccupants();
                occupantsList.remove(AppSession.getSession().getQbUser().getId());
                qbChatMessage.setProperty(PROPERTY_ROOM_CURRENT_OCCUPANTS_IDS,
                        ChatUtils.getOccupantsIdsStringFromList(occupantsList));
                chatNotificationType = ChatNotificationType.CHAT_OCCUPANTS;
                break;
            }
        }

        if (chatNotificationType != null) {
            qbChatMessage.setProperty(PROPERTY_ROOM_UPDATE_INFO, String.valueOf(chatNotificationType.getValue()));
        }

        qbChatMessage.setBody(ChatNotificationUtils.getBodyForUpdateChatNotificationMessage(context, dataManager, qbChatMessage, true));
        return qbChatMessage;
    }

    public static QBChatMessage createSystemMessageAboutCreatingGroupChat(Context context, QBChatDialog qbDialog) {
        QBChatMessage qbChatMessage = new QBChatMessage();
        addNecessaryPropertyForCreatingSystemMessage(context, qbChatMessage, qbDialog);

        if (qbDialog.getPhoto() != null) {
            qbChatMessage.setProperty(PROPERTY_ROOM_PHOTO, qbDialog.getPhoto());
        }

        return qbChatMessage;
    }

    public static QBChatDialog parseDialogFromQBMessage(Context context, QBChatMessage qbChatMessage,
                                                    String lastMessage, QBDialogType qbDialogType) {
        QBChatDialog qbDialog = parseDialogFromQBMessage(context, qbChatMessage, qbDialogType);

        if (!qbChatMessage.getAttachments().isEmpty()) {
            qbDialog.setLastMessage(context.getString(R.string.dlg_attached_last_message));
        } else if (!TextUtils.isEmpty(lastMessage)) {
            qbDialog.setLastMessage(lastMessage);
        }

        return qbDialog;
    }

    private static void addNecessaryPropertyForCreatingSystemMessage(Context context,
                                                                     QBChatMessage qbChatMessage, QBChatDialog qbDialog) {
        qbChatMessage.setBody(context.getResources().getString(R.string.cht_notification_message));
        qbChatMessage.setProperty(PROPERTY_MODULE_IDENTIFIER, VALUE_MODULE_IDENTIFIER);
        qbChatMessage.setProperty(PROPERTY_NOTIFICATION_TYPE,
                String.valueOf(NotificationType.GROUP_CHAT_CREATE.getValue()));
        qbChatMessage.setProperty(PROPERTY_CHAT_TYPE, VALUE_GROUP_CHAT_TYPE);

        qbChatMessage.setProperty(PROPERTY_ROOM_NAME, qbDialog.getName());
        qbChatMessage.setProperty(PROPERTY_ROOM_UPDATED_AT, String.valueOf(qbDialog.getUpdatedAt().getTime()));
        qbChatMessage.setProperty(PROPERTY_ROOM_CURRENT_OCCUPANTS_IDS,
                ChatUtils.getOccupantsIdsStringFromList(qbDialog.getOccupants()));
    }


}
