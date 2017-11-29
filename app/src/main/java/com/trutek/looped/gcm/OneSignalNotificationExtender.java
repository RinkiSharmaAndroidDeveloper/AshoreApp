package com.trutek.looped.gcm;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

/**
 * Created by Amrit on 20/12/16.
 */
public class OneSignalNotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        return true;
    }
}
