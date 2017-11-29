package com.trutek.looped.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.trutek.looped.R;
import com.trutek.looped.ui.home.NotificationActivity;
import com.trutek.looped.utils.helpers.notification.ChatNotificationHelper;

public class GcmIntentService extends IntentService {

    private static String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        String messageType = googleCloudMessaging.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                new ChatNotificationHelper(this).parseChatMessage(extras);
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
