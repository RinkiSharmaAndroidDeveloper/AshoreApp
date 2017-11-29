package com.trutek.looped.utils.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.commands.chat.QBLoadDialogsCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLoginChatCompositeCommand;
import com.trutek.looped.chatmodule.commands.rest.QBLoginCompositeCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.SessionUtils;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.models.LoginType;
import com.trutek.looped.utils.listeners.ExistingSessionListener;
import com.trutek.looped.utils.listeners.GlobalLoginListener;

import java.util.concurrent.TimeUnit;

public class LoginHelper {

    private Context context;
    private boolean isSessionExist;
    private GlobalLoginListener globalLoginListener;
    private ExistingSessionListener existingSessionListener;
    private CommandBroadcastReceiver commandBroadcastReceiver;

    private String userMobile;
    private String userPassword;

    public LoginHelper(Context context) {
        this.context = context;
        isSessionExist = AppSession.getSession().isSessionExist();

        userMobile = AppSession.getSession().getUser().getPhone();
        userPassword = AppSession.getSession().getUser().getQbPassword();
    }

    public LoginHelper(Context context, ExistingSessionListener existingSessionListener) {
        this(context);
        this.existingSessionListener = existingSessionListener;
    }

    public void checkStartExistSession() {
        if (needToClearAllData()) {
            existingSessionListener.onSessionFail();
            return;
        }

        startExistSession();
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(commandBroadcastReceiver);
    }

    public void startExistSession() {

        boolean isEmailEntered = !TextUtils.isEmpty(userMobile);
        boolean isPasswordEntered = !TextUtils.isEmpty(userPassword);
        if ((isEmailEntered && isPasswordEntered) || (isLoggedViaFB(isPasswordEntered))) {
            runExistSession();
        } else {
            existingSessionListener.onSessionFail();
        }
    }

    public void runExistSession() {
        //check is token valid for about 1 minute
        if (SessionUtils.isSessionExistOrNotExpired(TimeUnit.MINUTES.toMillis(
                Constants.TOKEN_VALID_TIME_IN_MINUTES))) {
            existingSessionListener.onSessionSuccess();
        } else {
            login();
        }
    }

    public void login() {
        if (LoginType.MOBILE.equals(getCurrentLoginType())) {
            loginQB();
        } else if (LoginType.FACEBOOK.equals(getCurrentLoginType())) {
//            loginFB();
        }
    }

//    public void loginFB() {
//        String fbToken = appSharedHelper.getFBToken();
//        AppSession.getSession().closeAndClear();
//        QBSocialLoginCommand.start(context, QBProvider.FACEBOOK, fbToken, null);
//    }

    public void makeGeneralLogin(GlobalLoginListener globalLoginListener) {
        this.globalLoginListener = globalLoginListener;
        commandBroadcastReceiver = new CommandBroadcastReceiver();
        registerCommandBroadcastReceiver();
        login();
    }

    private void loadDialogs() {
        QBLoadDialogsCommand.start(context);
    }

    public void loginQB() {
        QBUser qbUser = new QBUser(userMobile, userPassword, null);
        QBLoginCompositeCommand.start(context, qbUser);
    }

    public void loginChat() {
        QBLoginChatCompositeCommand.start(context);
    }

    public static boolean isCorrectOldAppSession() {
        AppSession.load();
        return AppSession.getSession().getLoginType() != LoginType.FACEBOOK
                && AppSession.getSession().isSessionExist();
    }

    private void registerCommandBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(QuickBloxServiceConsts.LOGIN_SUCCESS_ACTION);
        intentFilter.addAction(QuickBloxServiceConsts.LOGIN_FAIL_ACTION);

        intentFilter.addAction(QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);
        intentFilter.addAction(QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_FAIL_ACTION);

        intentFilter.addAction(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
        intentFilter.addAction(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION);

        LocalBroadcastManager.getInstance(context).registerReceiver(commandBroadcastReceiver, intentFilter);
    }

    public static boolean isProfileComplete() {
        return AppSession.getSession().getUser().getProfileComplete().equalsIgnoreCase(ProfileModel.Status.Active.name());
    }

    public boolean isLoggedViaFB(boolean isPasswordEntered) {
        return isPasswordEntered && LoginType.FACEBOOK.equals(getCurrentLoginType());
    }

    public LoginType getCurrentLoginType() {
        return AppSession.getSession().getLoginType();
    }

    private boolean needToClearAllData() {
        return false;
    }

    private class CommandBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals(QuickBloxServiceConsts.LOGIN_SUCCESS_ACTION)) {
                QBUser qbUser = (QBUser) intent.getExtras().getSerializable(QuickBloxServiceConsts.EXTRA_USER);
//                AppSession.getSession().updateUser(qbUser);
                loginChat();
            } else if (intent.getAction().equals(QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION)) {
                loadDialogs();
            } else if (intent.getAction().equals(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION)) {
                unregisterBroadcastReceiver();
                if (globalLoginListener != null) {
                    globalLoginListener.onCompleteQbChatLogin();
                }
            } else if (intent.getAction().equals(QuickBloxServiceConsts.LOGIN_FAIL_ACTION)
                    || intent.getAction().equals(QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_FAIL_ACTION)
                    || intent.getAction().equals(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION)) {
                unregisterBroadcastReceiver();
                if (globalLoginListener != null) {
                    globalLoginListener.onCompleteWithError("Login was finished with error!");
                }
            }
        }
    }

}
