package com.trutek.looped.utils.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.utils.SystemUtils;
import com.trutek.looped.utils.helpers.notification.ChatNotificationHelper;

public class ChatMessageReceiver extends BroadcastReceiver {

    private static final String TAG = ChatMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "--- onReceive() ---");

        if (!SystemUtils.isAppRunningNow()) {
            ChatNotificationHelper chatNotificationHelper = new ChatNotificationHelper(context);

            String message = intent.getStringExtra(QuickBloxServiceConsts.EXTRA_CHAT_MESSAGE);
            ChatUserModel user = (ChatUserModel) intent.getSerializableExtra(QuickBloxServiceConsts.EXTRA_USER);
            String dialogId = intent.getStringExtra(QuickBloxServiceConsts.EXTRA_DIALOG_ID);

            chatNotificationHelper.saveOpeningDialogData(user.getUserId(), dialogId);
            chatNotificationHelper.saveOpeningDialog(true);
            chatNotificationHelper.sendNotification(message, null);
        }
    }
}