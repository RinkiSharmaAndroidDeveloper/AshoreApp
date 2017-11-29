package com.trutek.looped.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.trutek.looped.data.contracts.models.DeviceModel;
import com.trutek.looped.data.contracts.models.FacebookModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.contracts.services.IUserService;
import com.trutek.looped.data.impl.entities.User;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.LoginType;
import com.trutek.looped.ui.authenticate.SignUpActivity;
import com.trutek.looped.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookHelper {

    private CallbackManager callbackManager;
    private AsyncResult<UserModel> result;
    private IUserService userService;

    public void initializeFacebook(final Context context) {
        FacebookSdk.sdkInitialize(context);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            button_facebook.setReadPermissions("public_profile","email", "user_friends","user_birthday");
            @Override
            public void onSuccess(final LoginResult result) {

                Log.d("fb data", "Success");
                Bundle param = new Bundle();
                param.putString("fields", "id, email, name, first_name, last_name, gender, birthday");
                new GraphRequest(result.getAccessToken(), "/me", param, HttpMethod.GET, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        signUpWithFacebook(response.getJSONObject());
                    }
                }).executeAsync();

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("", "error: " + error.getMessage());
                if (error.getMessage() != null) {
                    result.error(error.getMessage());

                } else {
                    //SnackBar.serverError(LogInActivity.this);
                }
            }

            @Override
            public void onCancel() {
                result.error("Cancel");
                Log.e("Result", "Cancel");
                // Toast.makeText(LogInActivity.this, Utility.getStrings(LogInActivity.this, R.string.fb_login_cancel), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(Activity mainActivity, AsyncResult<UserModel> result, IUserService userService) {
        this.result = result;
        this.userService = userService;
        LoginManager.getInstance().logInWithReadPermissions(mainActivity, Arrays.asList("public_profile", "user_friends", "email", "user_photos"));
    }

    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    private void signUpWithFacebook(JSONObject object){
        FacebookModel facebook = new FacebookModel();
        setFacebookDataInPreference(object, facebook);
        facebook.type = LoginType.FACEBOOK;
        DeviceModel deviceModel = new DeviceModel();
        SignUpActivity signUpActivity =new SignUpActivity();
        deviceModel.id = signUpActivity.getOneSignalPlayerId();
        facebook.device= deviceModel;

        userService.signUpWithFacebook(facebook, new AsyncResult<UserModel>() {
            @Override
            public void success(UserModel userModel) {
               result.success(userModel);
            }

            @Override
            public void error(final String error) {
                logOut();
                result.error(error);
            }
        });
    }

    private void setFacebookDataInPreference(JSONObject jsonObject, FacebookModel model){
        try {
            model.userName = jsonObject.getString("id") +"@facebook.com";
            model.first_name = jsonObject.getString("name");
            model.last_name = jsonObject.getString("last_name");
            model.gender = jsonObject.getString("gender");
            model.facebookId = jsonObject.getString("id");
            model.url = "https://graph.facebook.com/" +jsonObject.getString("id") + "/picture?width=720";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logOut(){
        LoginManager.getInstance().logOut();
    }
}
