package com.trutek.looped.utils.helpers.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.trutek.looped.R;
import com.trutek.looped.androidservices.NotificationBackGroundService;
import com.trutek.looped.chatmodule.data.contracts.models.NotificationEvent;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.impl.entities.Recipient;
import com.trutek.looped.gcm.PushNotificationModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.ui.authenticate.SplashActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.ui.home.NotificationActivity;
import com.trutek.looped.ui.recipient.recipient.display.DisplayRecipientActivity;
import com.trutek.looped.utils.SystemUtils;

public class NotificationManagerHelper {

    public final static int NOTIFICATION_ID = NotificationManagerHelper.class.hashCode();
    static final String NOTIFICATION_KEY_CONNECTION = "Connections Updated";

    public static void sendNotificationEvent(Context context, NotificationEvent notificationEvent) {
//        Intent intent = SystemUtils.getPreviousIntent(context);
//        if (intent == null) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("OPEN_FROM", 0);
//            intent.setAction(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        }
        sendNotificationEvent(context, intent, notificationEvent);
    }

    public static void sendNotificationEventForOneSignal(Context context, NotificationEvent notificationEvent) {
//        Intent intent = SystemUtils.getPreviousIntent(context);
//        if (intent == null) {
//           Intent intent = new Intent(context, NotificationActivity.class);
//            intent.setAction(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        }
        Intent intent;
        if(notificationEvent.getNotificationModel() != null
                && notificationEvent.getNotificationModel().a.action.equalsIgnoreCase("invited")
                && notificationEvent.getNotificationModel().a.api.equalsIgnoreCase("profile/recipient")){
            PushNotificationModel pushModel = notificationEvent.getNotificationModel();
            intent = new Intent(context, DisplayRecipientActivity.class);
            RecipientModel recipientModel = new RecipientModel();
            recipientModel.setServerId(pushModel.a.entity.id);
            intent.putExtra("requestForRecipient", true);
            intent.putExtra("notificationId", pushModel.a.id);
            intent.putExtra("recipient", recipientModel);
            sendNotificationEvent(context, intent, notificationEvent);
        } else if(notificationEvent.getNotificationModel() != null
                && notificationEvent.getNotificationModel().a.action.equalsIgnoreCase("updation")){
            intent = new Intent(context, NotificationBackGroundService.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            intent.setAction(getIntentAction(notificationEvent.getSubject()));
            context.startService(intent);
        }else {
            intent = new Intent(context, NotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            sendNotificationEvent(context, intent, notificationEvent);
        }
    }

    private static void sendNotificationEvent(Context context, Intent intent, NotificationEvent notificationEvent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder;
        if(PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.SOUND, false)){
            builder = new NotificationCompat.Builder(context)
                     .setColor(Color.parseColor("#0ccdaa"))
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(notificationEvent.getTitle())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationEvent.getSubject()))
                    .setContentText(notificationEvent.getBody())
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setSound(alarmSound);
        } else {
            builder = new NotificationCompat.Builder(context)
                    .setColor(Color.parseColor("#0ccdaa"))
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(notificationEvent.getTitle())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationEvent.getSubject()))
                    .setContentText(notificationEvent.getBody())
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);
        }

        Notification notification = builder.build();
    //    notification.flags |= Notification.FLAG_AUTO_CANCEL;
      //  notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
    //    notification.defaults = Notification.DEFAULT_ALL;
       // notificationManager.notify(NOTIFICATION_ID, notification);
        notificationManager.notify(1,notification);
    }


    private static int getNotificationIcon() {
        boolean whiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        // TODO need to add other icon
        return whiteIcon ? R.mipmap.notification_icon_display : R.mipmap.app_icon;
    }

    public static void clearNotificationEvent(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    static String getIntentAction(String notificationKey){
        switch (notificationKey){
            case NOTIFICATION_KEY_CONNECTION:
                return Constants.BROADCAST_MY_CONNECTIONS;
            default:
                return "";
        }
    }
}