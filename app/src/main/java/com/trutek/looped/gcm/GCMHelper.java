package com.trutek.looped.gcm;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.trutek.looped.task.GCMRegistrationTask;

public class GCMHelper {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 2404;
    private final static String TAG = GCMHelper.class.getSimpleName();

    private Activity activity;
    private GoogleCloudMessaging googleCloudMessaging;

    public GCMHelper(Activity activity) {
        this.activity = activity;
        googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
    }

    public boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public void registerInBackground() {
        new GCMRegistrationTask(activity).execute(googleCloudMessaging);
    }

}
