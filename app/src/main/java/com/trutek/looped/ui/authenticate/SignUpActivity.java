package com.trutek.looped.ui.authenticate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onesignal.OneSignal;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBSubscription;
import com.trutek.looped.chatmodule.data.contracts.services.IDialogService;
import com.trutek.looped.data.contracts.models.DeviceModel;
import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.IDiseaseService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.services.IProviderService;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.gcm.GCMHelper;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.ui.profile.create.CreateProfileActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.models.LoginType;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.LoginModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.contracts.services.IUserService;
import com.trutek.looped.utils.helpers.FacebookHelper;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends BaseAuthActivity implements SignUpFragment.OnFragmentInteractionListener,
        ActvationCodeFragment.OnFragmentInteractionListener, AsyncResult<UserModel> {
    public static final String TAG = SignUpActivity.class.getSimpleName();
    @Inject
    IUserService userService;
    @Inject
    IProfileService _ProfileService;
    @Inject
    IRecipientService recipientService;
    @Inject
    IDiseaseService diseaseService;
    @Inject
    IProviderService _ProviderService;
    @BindView(R.id.asu_text_signup)
    TextView textView_signup;
    @BindView(R.id.frame_signup)
    FrameLayout signUpFrame;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private LoginModel loginModel;
    public static FacebookHelper facebookHelper;
    GCMHelper gcmHelper;
    public static void start(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected int getContentResId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookHelper = new FacebookHelper ();
        facebookHelper.initializeFacebook(getApplicationContext());
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.splash_color));
        }
        gcmHelper = new GCMHelper(this);
        ButterKnife.bind(this);
        setFonts();
        loginModel = new LoginModel();
        addFragmentWithoutStackEntry(R.id.frame_signup, SignUpFragment.newInstance(loginModel), "SignUpFragment");
    }

    public void facebookLogin() {
        showProgress();
        AppSession.getSession().closeAndClear();
        facebookHelper.login(this, this, userService);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(SignUpActivity.this);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void signUpWithMobile(LoginModel model) {
        showProgress();
        AppSession.getSession().closeAndClear();

        DeviceModel deviceModel = new DeviceModel();
        deviceModel.id = getOneSignalPlayerId();

        model.type = LoginType.MOBILE;
        model.device= deviceModel;
        userService.signUp(model, new AsyncResult<UserModel>() {
            @Override
            public void success(UserModel userModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        openActivationCode();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    public void validatePin(LoginModel model) {
        showProgress();
        userService.validatePin(model, new AsyncResult<UserModel>() {
            @Override
            public void success(final UserModel userModel) {
                userModel.profile.isMine = true;
                _ProfileService.deleteAll();
                _ProfileService.saveProfileToDatabase(userModel.profile);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (userModel.profile.status.equalsIgnoreCase(ProfileModel.Status.Active.name())) {
                            getProfile();
                        } else {
                            hideProgress();
                            startProfileActivity();
                        }
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    private void getProfile() {
        ProfileModel profileModel = _ProfileService.getMyProfile(null);
        AppSession.getSession().updateUserName(profileModel.name);
        saveGeneralDataIntoPreference(profileModel);
        hideProgress();
        startHomeActivity();
    }

    private void saveGeneralDataIntoPreference(ProfileModel profile) {
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        helper.savePreference(PreferenceHelper.FULL_NAME, profile.name);
        helper.savePreference(PreferenceHelper.GENDER, profile.gender);
        helper.savePreference(PreferenceHelper.USER_PIC_URL, profile.picUrl);
        helper.savePreference(PreferenceHelper.USER_PROFILE_ID, profile.id);
        if (profile.location != null) {
            helper.savePreference(PreferenceHelper.LOCATION_NAME, profile.location.name);
            helper.savePreference(PreferenceHelper.LOCATION_DESCRIPTION, profile.location.description);
            if (profile.location.coordinates != null) {
                helper.savePreference(PreferenceHelper.LOCATION_LONG, profile.location.coordinates.get(0));
                helper.savePreference(PreferenceHelper.LOCATION_LAT, profile.location.coordinates.get(1));
            }
        }
        helper.savePreference(PreferenceHelper.ABOUT_US, profile.about);
        if (profile.dateOfBirth != null)
            helper.savePreference(PreferenceHelper.B_DAY, DateHelper.stringify(profile.dateOfBirth));
    }

    /*recipientService.getRecipientAndSave(model.getServerId(), new AsyncResult<RecipientModel>() {
        @Override
        public void success(RecipientModel recipientModel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    startHomeActivity();
                }
            });
        }

        @Override
        public void error(final String error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.longToast(error);
                }
            });
        }
    });
} else {
    hideProgress();
    startHomeActivity();
}
}
*/
    private void openActivationCode() {
        addFragmentWithoutStackEntry(R.id.frame_signup, ActvationCodeFragment.newInstance(loginModel), "ActivationCode");
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        signUpFrame.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
     //  checkGCMRegistration();
    }

    private void checkGCMRegistration() {
        if (gcmHelper.checkPlayServices()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                return;
            }
            gcmHelper.registerInBackground();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gcmHelper.registerInBackground();
        } else {
            ToastUtils.longToast("Required permissions are not granted");
        }
    }*/

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        signUpFrame.setVisibility(View.VISIBLE);
    }

    private void setFonts() {
        Typeface avenirNextBold = Typeface.createFromAsset(getAssets(), Constants.AvenirNextBold);
        textView_signup.setTypeface(avenirNextBold);
    }

    @Override
    public void success(UserModel userModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                CreateProfileActivity.start(SignUpActivity.this);
            }
        });
    }

    @Override
    public void error(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                ToastUtils.longToast(error);
            }
        });
    }

    // hide keypad and cursor from edittext phone no.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return true;
    }

    public String getOneSignalPlayerId() {
        final String[] mUserId = {null};
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                mUserId[0] = userId;
            }
        });
        if (mUserId[0] != null) {
            Log.d("PlayerId", mUserId[0]);
        } else {
            Log.d("PlayerId", "null");
        }
        return mUserId[0];
    }

}