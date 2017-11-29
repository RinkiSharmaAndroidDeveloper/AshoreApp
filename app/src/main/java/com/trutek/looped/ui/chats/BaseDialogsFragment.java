package com.trutek.looped.ui.chats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.quickblox.chat.model.QBChatDialog;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.ui.base.BaseChatActivity;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.base.BaseLoaderFragmentV4;
import com.trutek.looped.ui.dialogs.ProgressDialogFragment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;

/**
 * Created by msas on 9/16/2016.
 */
public abstract class BaseDialogsFragment<TData> extends BaseLoaderFragmentV4<TData>{

    protected BaseChatActivity baseActivity;
    private Handler handler;
    private Map<String, Set<Command>> broadcastCommandMap;
    protected LocalBroadcastManager localBroadcastManager;
    private BaseBroadcastReceiver broadcastReceiver;
    protected PrivateChatHelper privateChatHelper;
    protected GroupChatHelper groupChatHelper;
    protected QuickBloxService service;
    protected BaseAppCompatActivity.FailAction failAction;

    protected abstract void startPrivateChat(QBChatDialog dialog);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
    }

    @Override
    public void onResume() {
        super.onResume();

        addActions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeActions();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BaseChatActivity) {
            baseActivity = (BaseChatActivity) context;
            service = baseActivity.getService();
            privateChatHelper = baseActivity.getPrivateChatHelper();
            groupChatHelper = baseActivity.getGroupChatHelper();
            failAction = baseActivity.getFailAction();
        }

    }

    private void initFields() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastCommandMap = new HashMap<>();
        broadcastReceiver = new BaseBroadcastReceiver();

        groupChatHelper = baseActivity.getGroupChatHelper();
    }

    protected void activateButterKnife(View view) {
        ButterKnife.bind(this, view);
    }

    private void addActions() {
        addAction(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, new LoadChatsSuccessAction());
//        addAction(QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());

        addAction(QuickBloxServiceConsts.LEAVE_GROUP_DIALOG_SUCCESS_ACTION, new LeaveGroupDialogSuccessAction());
        addAction(QuickBloxServiceConsts.LEAVE_GROUP_DIALOG_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }


    private void removeActions() {
        removeAction(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
//        removeAction(QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.LEAVE_GROUP_DIALOG_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.LEAVE_GROUP_DIALOG_FAIL_ACTION);

        updateBroadcastActionList();
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

    public void updateBroadcastActionList() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        IntentFilter intentFilter = new IntentFilter();
        for (String commandName : broadcastCommandMap.keySet()) {
            intentFilter.addAction(commandName);
        }
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void showProgress() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            ProgressDialogFragment.show(fragmentManager);
        }
    }

    @Override
    public void hideProgress() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            ProgressDialogFragment.hide(fragmentManager);
        }
    }

    public class LoadChatsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {

        }
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
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

//    private class CreatePrivateChatSuccessAction implements Command {
//
//        @Override
//        public void execute(Bundle bundle) throws Exception {
//            hideProgress();
//            QBDialog qbDialog = (QBDialog) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);
//            startPrivateChat(qbDialog);
//        }
//    }


    private class LeaveGroupDialogSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
        }
    }

}
