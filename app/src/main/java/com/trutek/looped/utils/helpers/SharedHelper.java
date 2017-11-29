package com.trutek.looped.utils.helpers;


import android.content.Context;

import com.trutek.looped.msas.common.helpers.PreferenceHelper;

public class SharedHelper extends PreferenceHelper {

    public class Constants {

        public static final String ENABLING_PUSH_NOTIFICATIONS = "enabling_push_notifications";
    }

    public SharedHelper(Context context) {
        super(context);
    }

    public boolean isEnablePushNotifications() {
        return getPreference(Constants.ENABLING_PUSH_NOTIFICATIONS, true);
    }

    public void saveEnablePushNotifications(boolean enable) {
        savePreference(Constants.ENABLING_PUSH_NOTIFICATIONS, enable);
    }
}
