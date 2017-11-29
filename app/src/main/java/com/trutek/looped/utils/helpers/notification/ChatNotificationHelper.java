package com.trutek.looped.utils.helpers.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.NotificationEvent;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.gcm.PushNotificationModel;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.utils.SystemUtils;
import com.trutek.looped.utils.helpers.LoginHelper;
import com.trutek.looped.utils.listeners.simple.SimpleGlobalLoginListener;


public class ChatNotificationHelper {

    public static final String ALERT = "alert";
    public static final String MESSAGE = "message";
    public static final String DIALOG_ID = "dialog_id";
    public static final String USER_ID = "user_id";
    private static final String TAG = ChatNotificationHelper.class.getSimpleName();

    private Context context;
    private String dialogId;
    private int userId;

    private boolean fromOneSignal;
    private static String message;
    private static boolean isLoginNow;
    private PushNotificationModel notificationModel;
    private Gson gson;

    public ChatNotificationHelper(Context context) {
        this.context = context;
        gson = new Gson();
    }

    public void parseChatMessage(Bundle extras) {
        Log.d(TAG,"Push Message: " + extras.toString());
        if (extras.getString(ChatNotificationHelper.MESSAGE) != null) {
            message = extras.getString(ChatNotificationHelper.MESSAGE);
            fromOneSignal = false;
        }

        if (extras.getString(ChatNotificationHelper.ALERT) != null) {
            message = extras.getString(ChatNotificationHelper.ALERT);
            String result = (String) extras.getSerializable("custom");
            notificationModel = gson.fromJson(result, PushNotificationModel.class);
            fromOneSignal = true;
        }

        if (extras.getString(ChatNotificationHelper.USER_ID) != null) {
            userId = Integer.parseInt(extras.getString(ChatNotificationHelper.USER_ID));
            fromOneSignal = false;
        }

        if (extras.getString(ChatNotificationHelper.DIALOG_ID) != null) {
            dialogId = extras.getString(ChatNotificationHelper.DIALOG_ID);
            fromOneSignal = false;
        }

//        if (SystemUtils.isAppRunningNow()) {
//            return;
//        }

        boolean chatPush = userId != 0 && !TextUtils.isEmpty(dialogId);

        if (chatPush) {
            saveOpeningDialogData(userId, dialogId);
            if (AppSession.getSession().getUser() != null && !isLoginNow) {
                isLoginNow = true;
                LoginHelper loginHelper = new LoginHelper(context);
                loginHelper.makeGeneralLogin(new GlobalLoginListener());
                return;
            }
        } else {
            // push about call
            // push from one signal
            sendNotification(message, notificationModel);
        }

        saveOpeningDialog(false);
    }

    public void sendNotification(String message, PushNotificationModel notificationModel) {
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setTitle(context.getString(R.string.app_name));
        notificationEvent.setSubject(message);
        notificationEvent.setBody(message);
        notificationEvent.setNotificationModel(notificationModel);

        if (fromOneSignal)
            NotificationManagerHelper.sendNotificationEventForOneSignal(context, notificationEvent);
        else
            NotificationManagerHelper.sendNotificationEvent(context, notificationEvent);
    }

    private boolean isPushForPrivateChat() {
        DialogModel dialog = DataManager.getInstance().getDialogRepository().get(new PageQuery().add("dialogId", dialogId));
        return dialog != null && dialog.getType().equals(DialogModel.Type.PRIVATE);
    }

    public void saveOpeningDialogData(int userId, String dialogId) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getPrefsHelper();
        preferenceHelper.savePreference(PreferenceHelper.PUSH_USER_ID, userId);
        preferenceHelper.savePreference(PreferenceHelper.PUSH_DIALOG_ID, dialogId);
    }

    public void saveOpeningDialog(boolean open) {
        PreferenceHelper.getPrefsHelper().savePreference(PreferenceHelper.PUSH_NEED_TO_OPEN_DIALOG, open);
    }

    private class GlobalLoginListener extends SimpleGlobalLoginListener {

        @Override
        public void onCompleteQbChatLogin() {
            isLoginNow = false;

            saveOpeningDialog(true);

            Intent intent = SystemUtils.getPreviousIntent(context);
            if (!isPushForPrivateChat() || intent == null) {
                sendNotification(message, null);
            }
        }

        @Override
        public void onCompleteWithError(String error) {
            isLoginNow = false;

            saveOpeningDialog(false);
        }
    }
}