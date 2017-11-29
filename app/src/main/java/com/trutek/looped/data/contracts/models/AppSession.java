package com.trutek.looped.data.contracts.models;

import android.text.TextUtils;

import com.quickblox.users.model.QBUser;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.*;

import java.io.Serializable;

public class AppSession implements Serializable {

    private static final Object lock = new Object();
    private static AppSession activeSession;
    private final LoginType loginType;
    private com.trutek.looped.msas.common.models.UserModel user;
    private QBUser qbUser;
    private String sessionToken;

    private AppSession(LoginType loginType, com.trutek.looped.msas.common.models.UserModel user, String sessionToken) {
        this.loginType = loginType;
        this.user = user;
        this.sessionToken = sessionToken;
        save();
    }

    public static void startSession(LoginType loginType, com.trutek.looped.msas.common.models.UserModel user, String sessionToken) {
        activeSession = new AppSession(loginType, user, sessionToken);
    }

    private static AppSession getActiveSession() {
        synchronized (lock) {
            return activeSession;
        }
    }

    public static AppSession getSession() {
        AppSession activeSession = AppSession.getActiveSession();
        if (activeSession == null) {
            activeSession = AppSession.load();
        }
        return activeSession;
    }

    public static AppSession load() {
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        int loginTypeRaw = helper.getPreference(PreferenceHelper.USER_LOGIN_TYPE, LoginType.MOBILE.getValue());
        int userId = helper.getPreference(PreferenceHelper.USER_ID, Constants.NOT_INITIALIZED_VALUE);
        String userFullName = helper.getPreference(PreferenceHelper.FULL_NAME, Constants.EMPTY_STRING);
        String sessionToken = helper.getPreference(PreferenceHelper.SESSION_TOKEN, Constants.EMPTY_STRING);
        String mobile = helper.getPreference(PreferenceHelper.USER_MOBILE, Constants.EMPTY_STRING);
        String serverId = helper.getPreference(PreferenceHelper.USER_SERVER_ID, Constants.EMPTY_STRING);
        int qbId = helper.getPreference(PreferenceHelper.USER_QB_ID, Constants.ZERO_INT);
        String qbPassword = helper.getPreference(PreferenceHelper.USER_QB_PASSWORD, Constants.EMPTY_STRING);
        String isComplete = helper.getPreference(PreferenceHelper.USER_IS_PROFILE_COMPLETE, Constants.INACTIVE);
        String facebookId = helper.getPreference(PreferenceHelper.USER_FACEBOOK_ID, Constants.EMPTY_STRING);
        String facebookPic = helper.getPreference(PreferenceHelper.USER_FACEBOOK_PIC_URL, Constants.EMPTY_STRING);
        String userPic = helper.getPreference(PreferenceHelper.USER_PIC_URL, Constants.EMPTY_STRING);


        com.trutek.looped.msas.common.models.UserModel user = new com.trutek.looped.msas.common.models.UserModel();
        user.setId(userId);
        user.setFullName(userFullName);
        user.setPhone(mobile);
        user.setServerId(serverId);
        user.setQbId(qbId);
        user.setQbPassword(qbPassword);
        user.setProfileComplete(isComplete);
        user.setFacebookId(facebookId);
        user.setFacebookPic(facebookPic);
        user.setUserPicUrl(userPic);

        LoginType loginType = LoginType.fromInt(loginTypeRaw);
        return new AppSession(loginType, user, sessionToken);
    }

    public void closeAndClear() {
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        helper.delete(PreferenceHelper.USER_EMAIL);
        helper.delete(PreferenceHelper.USER_LOGIN_TYPE);
     //   helper.delete(PreferenceHelper.SESSION_TOKEN);
        helper.delete(PreferenceHelper.USER_ID);

        helper.delete(PreferenceHelper.FULL_NAME);
        helper.delete(PreferenceHelper.USER_MOBILE);
        helper.delete(PreferenceHelper.USER_SERVER_ID);
        helper.delete(PreferenceHelper.USER_QB_ID);
        helper.delete(PreferenceHelper.USER_QB_PASSWORD);
        helper.delete(PreferenceHelper.USER_IS_PROFILE_COMPLETE);
        helper.delete(PreferenceHelper.USER_FACEBOOK_ID);
        helper.delete(PreferenceHelper.USER_PIC_URL);
        activeSession = null;
    }

    public com.trutek.looped.msas.common.models.UserModel getUser() {
        return user;
    }

    public void save() {
        PreferenceHelper prefsHelper = PreferenceHelper.getPrefsHelper();
        prefsHelper.savePreference(PreferenceHelper.USER_LOGIN_TYPE, loginType.getValue());
        prefsHelper.savePreference(PreferenceHelper.SESSION_TOKEN, sessionToken);
        saveUser(user, prefsHelper);
    }

    public void updateUser(com.trutek.looped.msas.common.models.UserModel user) {
        this.user = user;
        saveUser(this.user, PreferenceHelper.getPrefsHelper());
    }

    public void updateUserName(String name) {
        this.user.setFullName(name);
        updateUserName(name, PreferenceHelper.getPrefsHelper());
    }

    public QBUser getQbUser() {
        QBUser qbUser = new QBUser();
        qbUser.setId(user.getQbId());
        qbUser.setEmail(user.getEmail());
        qbUser.setPassword(user.getQbPassword());
        qbUser.setFullName(user.getName());
        return qbUser;
    }

    private void saveUser(com.trutek.looped.msas.common.models.UserModel user, PreferenceHelper helper) {
        helper.savePreference(PreferenceHelper.USER_ID, user.getId());
        helper.savePreference(PreferenceHelper.USER_EMAIL, user.getEmail());
        helper.savePreference(PreferenceHelper.FULL_NAME, user.getName());
        helper.savePreference(PreferenceHelper.GENDER, user.getGender());
        helper.savePreference(PreferenceHelper.USER_PASSWORD, user.getPassword());
        helper.savePreference(PreferenceHelper.USER_MOBILE, user.getPhone());
        helper.savePreference(PreferenceHelper.USER_QB_ID, user.getQbId());
        helper.savePreference(PreferenceHelper.USER_QB_PASSWORD, user.getQbPassword());
        helper.savePreference(PreferenceHelper.USER_SERVER_ID, user.getServerId());
        helper.savePreference(PreferenceHelper.USER_FACEBOOK_ID, user.getFacebookId());
        helper.savePreference(PreferenceHelper.USER_PIC_URL, user.getUserPicUrl());
        helper.savePreference(PreferenceHelper.USER_PROFILE_ID, user.getUserProfileId());
    }

    private void updateUserName(String name, PreferenceHelper helper) {
        helper.savePreference(PreferenceHelper.FULL_NAME, name);
    }

    public boolean isSessionExist() {
        return loginType != null && !TextUtils.isEmpty(sessionToken);
    }

    public LoginType getLoginType() {
        return loginType;
    }

}
