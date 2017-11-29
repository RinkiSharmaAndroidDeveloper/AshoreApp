package com.trutek.looped.ui.base;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.quickblox.auth.model.QBProvider;
import com.quickblox.chat.QBChatService;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.chat.QBLoadDialogsCommand;
import com.trutek.looped.chatmodule.commands.rest.QBLoginRestCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.User;
import com.trutek.looped.data.impl.repositories.ProfileRepository;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.Utils.bridges.ConnectionBridge;
import com.trutek.looped.msas.common.Utils.bridges.LoadingBridge;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.LoginType;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.ui.authenticate.SignUpFragment;
import com.trutek.looped.ui.authenticate.SplashActivity;
import com.trutek.looped.ui.chats.GroupDialogActivity;
import com.trutek.looped.ui.chats.InviteToGroupActivity;
import com.trutek.looped.ui.chats.PrivateDialogActivity;
import com.trutek.looped.ui.dialogs.ProgressDialogFragment;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.broadcasts.NetworkChangeReceiver;
import com.trutek.looped.utils.helpers.ActivityUIHelper;
import com.trutek.looped.utils.helpers.LoginHelper;
import com.trutek.looped.utils.helpers.notification.NotificationManagerHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseAppCompatActivity extends AppCompatActivity implements LoadingBridge, ConnectionBridge {

    protected abstract int getContentResId();
    protected LocalBroadcastManager localBroadcastManager;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private Map<String, Set<Command>> broadcastCommandMap;
    private Handler handler;
    private BaseBroadcastReceiver broadcastReceiver;
    private GlobalBroadcastReceiver globalBroadcastReceiver;
    protected FailAction failAction;
    private ActivityUIHelper activityUIHelper;
    protected Unbinder unbinder;

    protected Typeface avenirNextRegular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        if(getContentResId() != 0){
            setContentView(getContentResId());
            activateButterKnife();
        }

        setupActivityComponent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

    }

    private void initFields() {
        activityUIHelper = new ActivityUIHelper(this);

        globalBroadcastReceiver = new GlobalBroadcastReceiver();
        networkBroadcastReceiver = new NetworkBroadcastReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastCommandMap = new HashMap<>();
        broadcastReceiver = new BaseBroadcastReceiver();
        failAction = new FailAction();
    }

    private void activateButterKnife() {
        unbinder = ButterKnife.bind(this);
    }

    protected abstract void setupActivityComponent();

    @Override
    public boolean isNetworkAvailable() {
        return NetworkDetector.isNetworkAvailable(this);
    }

    @Override
    public void showProgress() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            ProgressDialogFragment.show(fragmentManager);
        }
    }

    @Override
    public void hideProgress() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            ProgressDialogFragment.hide(fragmentManager);
        }
    }

    public void setProfileStatusDone(){
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        helper.savePreference(PreferenceHelper.USER_IS_PROFILE_COMPLETE, ProfileModel.Status.Active.name());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceivers();

        addActions();

        NotificationManagerHelper.clearNotificationEvent(this);

        checkOpeningDialog();

    }

    private void checkOpeningDialog() {
        PreferenceHelper preferenceHelper = PreferenceHelper.getPrefsHelper();
        if (preferenceHelper.getPreference(PreferenceHelper.PUSH_NEED_TO_OPEN_DIALOG, false) && isChatInitialized()) {
            DialogModel dialog = DataManager.getInstance().getDialogRepository().getByServerId(preferenceHelper.getPreference(PreferenceHelper.PUSH_DIALOG_ID, ""));
            ChatUserModel user = DataManager.getInstance().getChatUserRepository().get(new PageQuery().add("userId", preferenceHelper.getPreference(PreferenceHelper.PUSH_USER_ID, 0)));

            if (dialog != null && user != null) {
                if (DialogModel.Type.PRIVATE.equals(dialog.getType())) {
                    startPrivateChatActivity(user, dialog);
                } else {
                    startGroupChatActivity(dialog);
                }

                preferenceHelper.savePreference(PreferenceHelper.PUSH_NEED_TO_OPEN_DIALOG, false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterBroadcastReceivers();
        removeActions();
    }

    private void addActions() {
        addAction(QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION, new LoginChatCompositeSuccessAction());
        addAction(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, new LoadChatsSuccessAction());
        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);

        updateBroadcastActionList();
    }

    private void registerBroadcastReceivers() {
        IntentFilter globalActionsIntentFilter = new IntentFilter();
        globalActionsIntentFilter.addAction(QuickBloxServiceConsts.GOT_CHAT_MESSAGE_LOCAL);
        globalActionsIntentFilter.addAction(QuickBloxServiceConsts.FORCE_RELOGIN);
        globalActionsIntentFilter.addAction(QuickBloxServiceConsts.REFRESH_SESSION);
        IntentFilter networkIntentFilter = new IntentFilter(NetworkChangeReceiver.ACTION_LOCAL_CONNECTIVITY);

        localBroadcastManager.registerReceiver(globalBroadcastReceiver, globalActionsIntentFilter);
        localBroadcastManager.registerReceiver(networkBroadcastReceiver, networkIntentFilter);
    }

    private void unregisterBroadcastReceivers() {
        localBroadcastManager.unregisterReceiver(globalBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        localBroadcastManager.unregisterReceiver(networkBroadcastReceiver);
    }

    public void updateBroadcastActionList() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        IntentFilter intentFilter = new IntentFilter();
        for (String commandName : broadcastCommandMap.keySet()) {
            intentFilter.addAction(commandName);
        }
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void addAction(String action, Command command) {
        Set<Command> commandSet = broadcastCommandMap.get(action);
        if (commandSet == null) {
            commandSet = new HashSet<Command>();
            broadcastCommandMap.put(action, commandSet);
        }
        commandSet.add(command);
    }

    public void removeAction(String action) {
        broadcastCommandMap.remove(action);
    }

    public boolean isFragmentExistInStack(String Tag){
        return getSupportFragmentManager().findFragmentByTag(Tag) != null;
    }

    public void popFragmentUpToTag(String Tag){
        getSupportFragmentManager().popBackStack(Tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void clearFragmentStack(){
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void popFragmentWithName(String Tag){
        getSupportFragmentManager().popBackStack(Tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void addFragmentWithoutStackEntry(int containerViewId, Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, TAG);
        fragmentTransaction.commit();
    }

    public void addFragmentWithStackEntry(int containerViewId, Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    public void replaceFragment(int containerViewId, Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    public void replaceFragmentWithAnimation(int containerViewId, Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    protected boolean isChatInitializedAndUserLoggedIn() {
        return isChatInitialized() && QBChatService.getInstance().isLoggedIn();
    }

    protected boolean isChatInitialized() {
        return QBChatService.getInstance().isLoggedIn() && AppSession.getSession().isSessionExist();
    }

    public void showSnackbar(String title, int duration, int buttonTitleResId, View.OnClickListener onClickListener) {
//        if (snackBarView != null) {
//            snackbar = Snackbar.make(snackBarView, title, duration);
//            snackbar.setAction(buttonTitleResId, onClickListener);
//            snackbar.show();
//        }
        ToastUtils.longToast(title);
    }

    public void hideSnackBar() {
//        if (snackbar != null) {
//            snackbar.dismiss();
//        }
    }

    public void startPrivateChatActivity(ChatUserModel user, DialogModel dialog) {
        PrivateDialogActivity.start(this, user, dialog);
    }

    public void startGroupChatActivity(DialogModel dialog) {
        GroupDialogActivity.start(this, dialog);
    }

    private class BaseBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                Log.d("STEPS", "executing " + action);
                final Set<Command> commandSet = broadcastCommandMap.get(action);

                if (commandSet != null && !commandSet.isEmpty()) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            for (Command command : commandSet) {
                                try {
                                    command.execute(intent.getExtras());
                                } catch (Exception e) {
                                    ErrorUtils.logError(e);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public class FailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Exception e = (Exception) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_ERROR);
            hideProgress();
            ErrorUtils.showError(BaseAppCompatActivity.this, e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onReceiveChatMessageAction(Bundle extras) {
        if (needShowReceivedNotification()) {
            onReceivedChatMessageNotification(extras);
        }
    }

    protected void onReceivedChatMessageNotification(Bundle extras) {
        activityUIHelper.showChatMessageNotification(extras);
    }

    private boolean needShowReceivedNotification() {
        boolean isSplashActivity = this instanceof SplashActivity;
        return !isSplashActivity;
    }

    public void onReceiveRefreshSessionAction(Bundle extras) {
        ToastUtils.longToast(R.string.dlg_refresh_session);
        refreshSession();
    }

    public void refreshSession() {
        if (LoginType.EMAIL.equals(AppSession.getSession().getLoginType())) {
            QBLoginRestCommand.start(this, AppSession.getSession().getQbUser());
        }
    }

    private void performLoadChatsSuccessAction(Bundle bundle) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //  ToastUtils.longToast("login chat successfully");
            }
        });
    }

    protected void loadDialogs() {
        QBLoadDialogsCommand.start(this);
    }

    protected void performLoginChatSuccessAction(Bundle bundle) {
        loadDialogs();
        hideProgress();
    }

    protected void onFailAction(String action) {
    }

    public class LoginChatCompositeSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performLoginChatSuccessAction(bundle);
        }
    }

    public class LoadChatsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performLoadChatsSuccessAction(bundle);
        }
    }

    private class GlobalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (intent == null) {
                        return;
                    }

                    if (QuickBloxServiceConsts.GOT_CHAT_MESSAGE_LOCAL.equals(intent.getAction())) {
                        onReceiveChatMessageAction(intent.getExtras());

                        // TODO not sure rigth place to update dialogs
                        QBLoadDialogsCommand.start(context);

                    } else if (QuickBloxServiceConsts.FORCE_RELOGIN.equals(intent.getAction())) {
                        //                        onReceiveForceReloginAction(intent.getExtras());
                    } else if (QuickBloxServiceConsts.REFRESH_SESSION.equals(intent.getAction())) {
                        onReceiveRefreshSessionAction(intent.getExtras());
                    }
                }
            });
        }
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {

        private boolean loggedIn = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean activeConnection = intent
                    .getBooleanExtra(NetworkChangeReceiver.EXTRA_IS_ACTIVE_CONNECTION, false);

            if (activeConnection) {

                if (!loggedIn && LoginHelper.isCorrectOldAppSession()) {
                    loggedIn = true;

                    LoginHelper loginHelper = new LoginHelper(BaseAppCompatActivity.this);
                    loginHelper.makeGeneralLogin(null);
                }
            }
        }
    }
}
