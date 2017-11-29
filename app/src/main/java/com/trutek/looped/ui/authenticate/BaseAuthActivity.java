package com.trutek.looped.ui.authenticate;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;

import com.trutek.looped.msas.common.models.LoginType;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.ui.profile.create.CreateProfileActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.FacebookHelper;

public abstract class BaseAuthActivity extends BaseAppCompatActivity {

    protected static final String STARTED_LOGIN_TYPE = "started_login_type";

    private static final int PHONE_STATE = 1;

    protected FacebookHelper facebookHelper;
    protected LoginType loginType = LoginType.MOBILE;
    protected Resources resources;

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    protected void facebookConnect() {
        loginType = LoginType.FACEBOOK;

//        loginWithFacebook();
    }

//    private void loginWithFacebook() {
//        AppSession.getSession().closeAndClear();
//        /*TODO clear all tables pending*/
//        FacebookHelper.logout(); // clearing old data
//        facebookHelper.loginWithFacebook();
//    }

//    private void initFields(Bundle savedInstanceState) {
//        resources = getResources();
//        if (savedInstanceState != null && savedInstanceState.containsKey(STARTED_LOGIN_TYPE)) {
//            loginType = (LoginType) savedInstanceState.getSerializable(STARTED_LOGIN_TYPE);
//        }
//        facebookHelper = new FacebookHelper(this, savedInstanceState, new FacebookSessionStatusCallback());
//        loginSuccessAction = new LoginSuccessAction();
//        socialLoginSuccessAction = new SocialLoginSuccessAction();
//        failAction = new FailAction();
//    }

    protected boolean isLoggedInToServer() {
        return false;
//        return AppSession.getSession().isLoggedIn();
    }

    protected void startHomeActivity() {
        setProfileStatusDone();
        HomeActivity.start(BaseAuthActivity.this);
    }

    protected void startProfileActivity() {
        CreateProfileActivity.start(BaseAuthActivity.this);
    }

    protected void startSignupLocationActivity() {
       SignupLocation.start(BaseAuthActivity.this);
    }

}
