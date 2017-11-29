package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trutek.looped.data.contracts.models.DeviceModel;
import com.trutek.looped.data.contracts.models.FacebookModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.DataModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;
import com.trutek.looped.data.contracts.apis.IUserApi;
import com.trutek.looped.data.contracts.models.LoginModel;
import com.trutek.looped.data.contracts.models.UserModel;

import java.lang.reflect.Type;
import java.util.Date;

public class UserApi<TModel> extends AsyncRemoteApi<UserModel> implements IUserApi<TModel> {

    private Gson _gson;
    private Type _dataType;


    public UserApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
        _dataType = dataType;

        _gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Date.class, new DateHelper())
                .registerTypeHierarchyAdapter(Date.class, new DateHelper())
                .create();
    }

    @Override
    public void signUp(LoginModel loginModel, final AsyncResult<UserModel> result) {
        final String body = _gson.toJson(loginModel);

        if (!_networkDetector.isNetworkAvailable()) {
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(
                new Runnable() {
            @Override
            public void run() {
                try {

                    String responseJson = _remoteRepository.rawPost("signUp", body);

                    DataModel<UserModel> responseData = _gson.fromJson(responseJson, _dataType);

                    if (responseData.isSuccess) {
                        result.success(responseData.data);
                    } else {
                        result.error(responseData.getError());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

    @Override
    public void register(LoginModel loginModel, final AsyncResult<UserModel> result) {
        final String body = _gson.toJson(loginModel);
        if (!_networkDetector.isNetworkAvailable()) {
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String responseJson = _remoteRepository.rawPost(body);

                    DataModel<UserModel> responseData = _gson.fromJson(responseJson, _dataType);

                    if (responseData.isSuccess) {
                        result.success(responseData.data);
                    } else {
                        result.error(responseData.getError());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }


    @Override
    public void validatePin(LoginModel loginModel, final AsyncResult<UserModel> result) {
        final String body = _gson.toJson(loginModel);
        final String action = PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.USER_SERVER_ID)+"/validatePin";
        if (!_networkDetector.isNetworkAvailable()) {
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String responseJson = _remoteRepository.rawPost(action, body);

                    DataModel<UserModel> responseData = _gson.fromJson(responseJson, _dataType);

                    if (responseData.isSuccess) {
                        result.success(responseData.data);
                    } else {
                        result.error(responseData.getError());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

    @Override
    public void signUpWithFacebook(FacebookModel model, final AsyncResult<UserModel> result) {
        final String body = _gson.toJson(model);
        if (!_networkDetector.isNetworkAvailable()) {
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String responseJson = _remoteRepository.rawPost("signupFacebook", body);

                    DataModel<UserModel> responseData = _gson.fromJson(responseJson, _dataType);

                    if (responseData.isSuccess) {
                        result.success(responseData.data);
                    } else {
                        result.error(responseData.getError());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

}
