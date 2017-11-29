package com.trutek.looped.data.impl.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBSubscription;
import com.trutek.looped.data.contracts.models.DeviceModel;
import com.trutek.looped.data.contracts.models.FacebookModel;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.services.base.BaseService;
import com.trutek.looped.data.contracts.apis.IUserApi;
import com.trutek.looped.data.contracts.models.LoginModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.contracts.services.IUserService;

import java.util.ArrayList;

public class UserService extends BaseService<UserModel> implements IUserService {

    private IUserApi<UserModel> remote;
    private SQLiteDatabase database;

    public UserService(IRepository<UserModel> local, IUserApi<UserModel> remote, SQLiteDatabase database) {
        super(local);
        this.remote = remote;
        this.database = database;
    }

    @Override
    public void signUp(final LoginModel model,final AsyncResult<UserModel> result) {
        remote.signUp(model, new AsyncResult<UserModel>() {
            @Override
            public void success(UserModel userModel) {
                PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
                helper.savePreference(PreferenceHelper.USER_SERVER_ID, userModel.getServerId());
                result.success(userModel);
            }
            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    /*@Override
    public void signUp(LoginModel model, String token, AsyncResult<UserModel> result) {

    }*/

    @Override
    public void register(LoginModel model, final AsyncResult<UserModel> result) {

        remote.register(model, new AsyncResult<UserModel>() {
            @Override
            public void success(UserModel userModel) {

                result.success(userModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void validatePin(final LoginModel loginModel, final AsyncResult<UserModel> result) {
        remote.validatePin(loginModel, new AsyncResult<UserModel>() {
            @Override
            public void success(UserModel serverModel) {

                com.trutek.looped.msas.common.models.UserModel model = new com.trutek.looped.msas.common.models.UserModel();
                model.setPhone(serverModel.phone);
                model.setQbId(serverModel.chat.id);
                model.setToken(serverModel.token);
                model.setQbPassword(serverModel.chat.password);
                model.setServerId(serverModel.getServerId());
                model.setProfileComplete(serverModel.status);
                AppSession.startSession(loginModel.type, model, serverModel.token);

                result.success(serverModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void signUpWithFacebook(final FacebookModel facebookModel, final AsyncResult<UserModel> result) {
        remote.signUpWithFacebook(facebookModel, new AsyncResult<UserModel>() {
            @Override
            public void success(UserModel userModel) {

                com.trutek.looped.msas.common.models.UserModel model = new com.trutek.looped.msas.common.models.UserModel();

                model.setFullName(facebookModel.first_name);
                model.setGender(facebookModel.gender);
                model.setFacebookId(facebookModel.facebookId);
                model.setFacebookPic(facebookModel.url);

                model.setPhone(userModel.phone);
                model.setQbId(userModel.chat.id);
                model.setToken(userModel.token);
                model.setQbPassword(userModel.chat.password);
                model.setServerId(userModel.getServerId());
                model.setProfileComplete(userModel.status);

                AppSession.startSession(facebookModel.type, model, userModel.token);

                result.success(userModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void clearDatabase() {
        database.execSQL("DELETE FROM User");
        database.execSQL("DELETE FROM Location");
        database.execSQL("DELETE FROM Interest");
        database.execSQL("DELETE FROM Tag");
        database.execSQL("DELETE FROM Notification");
        database.execSQL("DELETE FROM Community");
        database.execSQL("DELETE FROM Profile");
        database.execSQL("DELETE FROM Connection");
        database.execSQL("DELETE FROM Activity");
        database.execSQL("DELETE FROM Recipient");
        database.execSQL("DELETE FROM Disease");
        database.execSQL("DELETE FROM Dialog");
        database.execSQL("DELETE FROM Message");
        database.execSQL("DELETE FROM Attachment");
        database.execSQL("DELETE FROM CHAT_USER");
        database.execSQL("DELETE FROM DIALOG_NOTIFICATION");
        database.execSQL("DELETE FROM DIALOG_USERS");
        database.execSQL("DELETE FROM LOOP");
    }

}
