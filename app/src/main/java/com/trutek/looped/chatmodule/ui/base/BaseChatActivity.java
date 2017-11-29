package com.trutek.looped.chatmodule.ui.base;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.quickblox.chat.QBChatService;
import com.trutek.looped.chatmodule.commands.chat.QBLoadDialogsCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLoginChatCompositeCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.chats.GroupDialogActivity;
import com.trutek.looped.ui.chats.PrivateDialogActivity;
import com.trutek.looped.utils.ToastUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BaseChatActivity extends BaseAppCompatActivity {

    protected String title;

    protected QuickBloxService service;
    protected PrivateChatHelper privateChatHelper;
    protected GroupChatHelper groupChatHelper;
    private ServiceConnection serviceConnection;
    private boolean bounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
    }

    private void initFields() {
        serviceConnection = new QBChatServiceConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectToService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService();
    }

    protected boolean isChatInitialized() {
        return QBChatService.getInstance().isLoggedIn() && AppSession.getSession().isSessionExist();
    }

    protected boolean isChatInitializedAndUserLoggedIn() {
        return isChatInitialized() && QBChatService.getInstance().isLoggedIn();
    }

    protected void loginChat() {
        QBLoginChatCompositeCommand.start(this);
    }

    public QuickBloxService getService() {
        return service;
    }

    public PrivateChatHelper getPrivateChatHelper() {
        return privateChatHelper;
    }

    public GroupChatHelper getGroupChatHelper() {
        return groupChatHelper;
    }

    public FailAction getFailAction() {
        return failAction;
    }

    public void onConnectedToService(QuickBloxService service) {
        if (privateChatHelper == null) {
            privateChatHelper = (PrivateChatHelper) service.getHelper(QuickBloxService.PRIVATE_CHAT_HELPER);
        }

        if (groupChatHelper == null) {
            groupChatHelper = (GroupChatHelper) service.getHelper(QuickBloxService.GROUP_CHAT_HELPER);
        }

    }

    private void unbindService() {
        if (bounded) {
            unbindService(serviceConnection);
        }
    }

    private void connectToService() {
        Intent intent = new Intent(this, QuickBloxService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private class QBChatServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            bounded = true;
            service = ((QuickBloxService.QuickBloxServiceBinder) binder).getService();
            onConnectedToService(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}
