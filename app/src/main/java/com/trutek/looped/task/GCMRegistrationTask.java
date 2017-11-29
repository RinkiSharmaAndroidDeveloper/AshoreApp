package com.trutek.looped.task;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.trutek.looped.gcm.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.concurrancy.BaseErrorAsyncTask;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;

import java.util.ArrayList;

public class GCMRegistrationTask extends BaseErrorAsyncTask<GoogleCloudMessaging, Void, Bundle> {

    private static final String REGISTRATION_ID = "registration_id";
    public static final String EXTRA_IS_PUSH_SUBSCRIBED_ON_SERVER = "is_push_subscribed_on_server";
    private static final String TAG = GCMRegistrationTask.class.getSimpleName();
    private Activity activity;

    public GCMRegistrationTask(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onResult(Bundle bundle) {
        if (!bundle.isEmpty()) {
            if (bundle.getBoolean(EXTRA_IS_PUSH_SUBSCRIBED_ON_SERVER)) {
                storeRegistrationId(bundle);
            }
        }
    }

    @Override
    public Bundle performInBackground(GoogleCloudMessaging... params) throws Exception {
        String registrationId;
        boolean subscribed = false;
        Bundle registration = new Bundle();
        registrationId = getRegistrationId();
        if(registrationId.isEmpty()) {
            InstanceID instanceID = InstanceID.getInstance(activity);
            registrationId = instanceID.getToken(Constants.PUSH_REGISTRATION_APP_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            subscribed = subscribeToPushNotifications(registrationId);
        }
        registration.putString(Constants.EXTRA_REGISTRATION_ID, registrationId);
        registration.putBoolean(EXTRA_IS_PUSH_SUBSCRIBED_ON_SERVER, subscribed);
        return registration;
    }

    private String getRegistrationId() {
        final PreferenceHelper prefs = getGCMPreferences();
        String registrationId = prefs.getPreference(REGISTRATION_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    private void storeRegistrationId(Bundle registration) {
        final PreferenceHelper prefs = getGCMPreferences();
        prefs.savePreference(REGISTRATION_ID, registration.getString(Constants.EXTRA_REGISTRATION_ID));
    }

    private PreferenceHelper getGCMPreferences() {
        return PreferenceHelper.getPrefsHelper();
    }

    private boolean subscribeToPushNotifications(String regId) {
        String deviceId = getDeviceIdForMobile(activity);
        if (deviceId == null) {
            deviceId = getDeviceIdForTablet(activity);
        }

        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
        subscription.setEnvironment(QBEnvironment.DEVELOPMENT);
        subscription.setDeviceUdid(deviceId);
        subscription.setRegistrationID(regId);

        ArrayList<QBSubscription> subscriptions = null;
        try {
            subscriptions = QBPushNotifications.createSubscription(subscription).perform();
        } catch (QBResponseException e) {
            ErrorUtils.logError(e);
        }
        return subscriptions != null;
    }

    private String getDeviceIdForMobile(Context context) {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return null;
        }
        return telephonyManager.getDeviceId();
    }

    private String getDeviceIdForTablet(Context context) {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID); //*** use for tablets
    }
}
