package com.trutek.looped.ui.authenticate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.gcm.GCMHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.ui.location.LocationActivity;
import com.trutek.looped.ui.location.SearchLocationActivity;
import com.trutek.looped.ui.profile.create.CreateProfileActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.LoginHelper;
import com.trutek.looped.utils.listeners.ExistingSessionListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class SplashActivity extends BaseAuthActivity implements ExistingSessionListener {
    @Inject
    IProfileService _ProfileService;

    PreferenceHelper preferenceHelper;
    String userName, mobileNumber, profileStaus;
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int DELAY_FOR_OPENING_LANDING_ACTIVITY = 2000;

    @Override
    protected int getContentResId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.splash_color));
        }
        preferenceHelper = PreferenceHelper.getPrefsHelper();
        setSplash();
        printHashKey();
    }

    private void goNext() {
        userName = preferenceHelper.getPreference(PreferenceHelper.FULL_NAME);
        mobileNumber = preferenceHelper.getPreference(PreferenceHelper.USER_MOBILE);
        profileStaus = preferenceHelper.getPreference(PreferenceHelper.USER_IS_PROFILE_COMPLETE);
        if (LoginHelper.isCorrectOldAppSession()) {
            if (profileStaus != null && profileStaus.contains("Active")) {
                startHomeActivity();
                finish();
            } else if (mobileNumber != null && !mobileNumber.equals("") && userName.contains("")) {
                Intent intent = new Intent(this, CreateProfileActivity.class);
                startActivity(intent);
            } else {
                startSignupLocationActivity();
                finish();
            }
        } else {
            startSignupLocationActivity();
            finish();

        }
    }

    private void setSplash() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                goNext();
            }
        }, DELAY_FOR_OPENING_LANDING_ACTIVITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoggedInToServer()) {
            startHomeActivity();
        }
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onSessionSuccess() {
        startHomeActivity();
    }

    @Override
    public void onSessionFail() {
        startSignupLocationActivity();
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.trutek.looped",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("package_manager", e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            Log.i("NSAE", e.getMessage());
        }
    }

}
