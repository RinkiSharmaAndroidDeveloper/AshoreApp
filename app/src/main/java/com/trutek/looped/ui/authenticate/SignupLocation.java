package com.trutek.looped.ui.authenticate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.data.contracts.models.DeviceModel;
import com.trutek.looped.data.contracts.models.LoginModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.services.IUserService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.location.LocationActivity;
import com.trutek.looped.ui.location.SearchLocationActivity;

import javax.inject.Inject;

/**
 * Created by Rinki on 1/18/2017.
 */
public class SignupLocation extends BaseAppCompatActivity implements View.OnClickListener {

    @Inject
    IUserService _UserService;

    @Inject
    IProfileService _ProfileService;

    Button signupButton;
    TextView loggedIn;

    public static void start(Context context) {
        Intent intent = new Intent(context, SignupLocation.class);
        context.startActivity(intent);

    }
    @Override
    protected int getContentResId() {
        return R.layout.activity_signup_with_location;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupButton =(Button)findViewById(R.id.signup_location_button);
        loggedIn =(TextView) findViewById(R.id.signup_location_login_already);
        signupButton.setOnClickListener(this);
        loggedIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.signup_location_button):
                showProgress();
                registerUserWithPlayerId();
                break;
            case (R.id.signup_location_login_already):
                Intent intent = new Intent(this,SignUpActivity.class);
                PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
                helper.delete(PreferenceHelper.SESSION_TOKEN);
                startActivity(intent);
                break;
        }

    }

    void registerUserWithPlayerId(){
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                DeviceModel deviceModel = new DeviceModel();
                deviceModel.id = userId;
                LoginModel loginModel = new LoginModel("","");
                loginModel.device = deviceModel;
                _UserService.register(loginModel,asyncResult_signUp);
            }
        });
    }

    AsyncResult<UserModel> asyncResult_signUp = new AsyncResult<UserModel>() {
        @Override
        public void success(final UserModel userModel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _ProfileService.deleteAll();
                    _ProfileService.saveProfileToDatabase(userModel.profile);

                    PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
                    helper.savePreference(PreferenceHelper.SESSION_TOKEN, userModel.token);
                    Intent i = new Intent(SignupLocation.this,SearchLocationActivity.class);
                    startActivity(i);
                    hideProgress();
                }
            });
        }

        @Override
        public void error(final String error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
                    hideProgress();
                }
            });
        }
    };



}
