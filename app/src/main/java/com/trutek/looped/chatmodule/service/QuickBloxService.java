package com.trutek.looped.chatmodule.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.exception.QBResponseException;
import com.trutek.looped.App;
import com.trutek.looped.chatmodule.commands.QBLoadAttachFileCommand;
import com.trutek.looped.chatmodule.commands.chat.QBAddFriendsToGroupCommand;
import com.trutek.looped.chatmodule.commands.chat.QBCreateGroupDialogCommand;
import com.trutek.looped.chatmodule.commands.chat.QBCreatePrivateChatCommand;
import com.trutek.looped.chatmodule.commands.chat.QBDeleteChatCommand;
import com.trutek.looped.chatmodule.commands.chat.QBInitChatServiceCommand;
import com.trutek.looped.chatmodule.commands.chat.QBInitChatsCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLeaveGroupDialogCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLoadDialogMessagesCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLoadDialogsCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLoginChatCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLoginChatCompositeCommand;
import com.trutek.looped.chatmodule.commands.chat.QBUpdateGroupDialogCommand;
import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.commands.rest.QBLoginCompositeCommand;
import com.trutek.looped.chatmodule.commands.rest.QBLoginRestCommand;
import com.trutek.looped.chatmodule.helpers.BaseChatHelper;
import com.trutek.looped.chatmodule.helpers.BaseHelper;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.helpers.QBAuthHelper;
import com.trutek.looped.chatmodule.helpers.QBChatRestHelper;
import com.trutek.looped.chatmodule.utils.Utils;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.CompositeServiceCommand;
import com.trutek.looped.msas.common.commands.ServiceCommand;
import com.trutek.looped.utils.cloudinary.CloudinaryHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QuickBloxService extends Service {

    public static final int AUTH_HELPER = 0;
    public static final int CHAT_REST_HELPER = 1;
    public static final int PRIVATE_CHAT_HELPER = 2;
    public static final int CLOUDINARY_HELPER = 3;
    public static final int GROUP_CHAT_HELPER = 4;

    private static final String TAG = QuickBloxService.class.getSimpleName();

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private final BlockingQueue<Runnable> threadQueue;
    private ThreadPoolExecutor threadPool;

    private IBinder binder = new QuickBloxServiceBinder();
    private Map<String, ServiceCommand> serviceCommandMap = new HashMap<>();
    private BroadcastReceiver broadcastReceiver;
    private Map<Integer, BaseHelper> helpersMap;

    public QuickBloxService() {
        threadQueue = new LinkedBlockingQueue<>();
        helpersMap = new HashMap<>();
        App.getInstance().component().inject(this);
        broadcastReceiver = new LoginBroadcastReceiver();

        initThreads();

        initHelpers();
        initCommands();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void initThreads() {
        threadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, threadQueue);
        threadPool.allowCoreThreadTimeOut(true);
    }

    private void initHelpers() {
        helpersMap.put(AUTH_HELPER, new QBAuthHelper(this));
        helpersMap.put(CHAT_REST_HELPER, new QBChatRestHelper(this));
        helpersMap.put(PRIVATE_CHAT_HELPER, new PrivateChatHelper(this));
        helpersMap.put(CLOUDINARY_HELPER, new CloudinaryHelper(this));
        helpersMap.put(GROUP_CHAT_HELPER, new GroupChatHelper(this));
    }

    private void initCommands() {

        registerLoginRestCommand();
        registerLoginCommand();
        registerLoginChatCommand();

        registerLoadChatsDialogsCommand();
        registerLoadDialogMessagesCommand();
        registerLoadAttachFileCommand();
        registerCreatePrivateChatCommand();
        registerDeleteChatCommand();
        registerLeaveGroupDialogCommand();

        registerCreateGroupChatCommand();
        registerUpdateGroupDialogCommand();
        registerAddFriendsToGroupCommand();
        //cloudinary
        registerCloudinaryImageUploadCommand();
    }

    private void registerLoginRestCommand() {
        QBAuthHelper authHelper = (QBAuthHelper) getHelper(AUTH_HELPER);

        QBLoginRestCommand loginRestCommand = new QBLoginRestCommand(this, authHelper,
                QuickBloxServiceConsts.LOGIN_REST_SUCCESS_ACTION,
                QuickBloxServiceConsts.LOGIN_REST_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.LOGIN_REST_ACTION, loginRestCommand);
    }

    private void registerLoginCommand() {
        QBLoginCompositeCommand loginCommand = new QBLoginCompositeCommand(this,
                QuickBloxServiceConsts.LOGIN_SUCCESS_ACTION, QuickBloxServiceConsts.LOGIN_FAIL_ACTION);
        QBLoginRestCommand loginRestCommand = (QBLoginRestCommand) serviceCommandMap.get(
                QuickBloxServiceConsts.LOGIN_REST_ACTION);

        loginCommand.addCommand(loginRestCommand);

        serviceCommandMap.put(QuickBloxServiceConsts.LOGIN_ACTION, loginCommand);
    }

    private void registerLoginChatCommand() {
        CompositeServiceCommand loginChatCommand = new QBLoginChatCompositeCommand(this,
                QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION,
                QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_FAIL_ACTION);

        addLoginChatAndInitCommands(loginChatCommand);

        serviceCommandMap.put(QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_ACTION, loginChatCommand);
    }

    private void registerCreatePrivateChatCommand() {
        PrivateChatHelper privateChatHelper = (PrivateChatHelper) getHelper(PRIVATE_CHAT_HELPER);

        QBCreatePrivateChatCommand createPrivateChatCommand = new QBCreatePrivateChatCommand(this,
                privateChatHelper,
                QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION,
                QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_ACTION, createPrivateChatCommand);
    }

    private void addLoginChatAndInitCommands(CompositeServiceCommand loginCommand) {
        QBChatRestHelper chatRestHelper = (QBChatRestHelper) getHelper(CHAT_REST_HELPER);
        PrivateChatHelper privateChatHelper = (PrivateChatHelper) getHelper(PRIVATE_CHAT_HELPER);
        GroupChatHelper groupChatHelper = (GroupChatHelper) getHelper(GROUP_CHAT_HELPER);

        QBLoginRestCommand loginRestCommand = (QBLoginRestCommand) serviceCommandMap.get(
                QuickBloxServiceConsts.LOGIN_REST_ACTION);

        QBInitChatServiceCommand initChatServiceCommand = new QBInitChatServiceCommand(this, chatRestHelper,
                QuickBloxServiceConsts.INIT_CHAT_SERVICE_SUCCESS_ACTION,
                QuickBloxServiceConsts.INIT_CHAT_SERVICE_FAIL_ACTION);

        QBLoginChatCommand loginChatCommand = new QBLoginChatCommand(this, chatRestHelper,
                QuickBloxServiceConsts.LOGIN_CHAT_SUCCESS_ACTION,
                QuickBloxServiceConsts.LOGIN_CHAT_FAIL_ACTION);

        QBInitChatsCommand initChatsCommand = new QBInitChatsCommand(this, privateChatHelper, groupChatHelper,
                QuickBloxServiceConsts.INIT_CHATS_SUCCESS_ACTION,
                QuickBloxServiceConsts.INIT_CHATS_FAIL_ACTION);

        loginCommand.addCommand(loginRestCommand);
        loginCommand.addCommand(initChatServiceCommand);
        loginCommand.addCommand(loginChatCommand);
        loginCommand.addCommand(initChatsCommand);
    }

    private void registerLoadChatsDialogsCommand() {
        GroupChatHelper groupChatHelper = (GroupChatHelper) getHelper(GROUP_CHAT_HELPER);
        PrivateChatHelper privateChatHelper = (PrivateChatHelper) getHelper(PRIVATE_CHAT_HELPER);

        QBLoadDialogsCommand chatsDialogsCommand = new QBLoadDialogsCommand(this, privateChatHelper, groupChatHelper,
                QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION,
                QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_ACTION, chatsDialogsCommand);
    }

    private void registerLoadDialogMessagesCommand() {
        GroupChatHelper groupChatHelper = (GroupChatHelper) getHelper(GROUP_CHAT_HELPER);
        QBLoadDialogMessagesCommand loadDialogMessagesCommand = new QBLoadDialogMessagesCommand(this,
                groupChatHelper,
                QuickBloxServiceConsts.LOAD_DIALOG_MESSAGES_SUCCESS_ACTION,
                QuickBloxServiceConsts.LOAD_DIALOG_MESSAGES_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.LOAD_DIALOG_MESSAGES_ACTION, loadDialogMessagesCommand);
    }

    private void registerLoadAttachFileCommand() {
        PrivateChatHelper privateChatHelper = (PrivateChatHelper) getHelper(PRIVATE_CHAT_HELPER);

        ServiceCommand loadAttachFileCommand = new QBLoadAttachFileCommand(this, privateChatHelper,
                QuickBloxServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION,
                QuickBloxServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.LOAD_ATTACH_FILE_ACTION, loadAttachFileCommand);
    }

    private void registerCreateGroupChatCommand() {
        GroupChatHelper groupChatHelper = (GroupChatHelper) getHelper(GROUP_CHAT_HELPER);

        QBCreateGroupDialogCommand createGroupChatCommand = new QBCreateGroupDialogCommand(this,
                groupChatHelper,
                QuickBloxServiceConsts.CREATE_GROUP_CHAT_SUCCESS_ACTION,
                QuickBloxServiceConsts.CREATE_GROUP_CHAT_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.CREATE_GROUP_CHAT_ACTION, createGroupChatCommand);
    }

    private void registerUpdateGroupDialogCommand() {
        GroupChatHelper groupChatHelper = (GroupChatHelper) getHelper(GROUP_CHAT_HELPER);
        CloudinaryHelper cloudinaryHelper = (CloudinaryHelper) getHelper(CLOUDINARY_HELPER);

        QBUpdateGroupDialogCommand updateGroupNameCommand = new QBUpdateGroupDialogCommand(this,
                groupChatHelper,
                cloudinaryHelper,
                QuickBloxServiceConsts.UPDATE_GROUP_DIALOG_SUCCESS_ACTION,
                QuickBloxServiceConsts.UPDATE_GROUP_DIALOG_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.UPDATE_GROUP_DIALOG_ACTION, updateGroupNameCommand);
    }

    private void registerDeleteChatCommand() {
        PrivateChatHelper privateChatHelper = (PrivateChatHelper) getHelper(PRIVATE_CHAT_HELPER);

        ServiceCommand deleteChatCommand = new QBDeleteChatCommand(this, privateChatHelper,
                QuickBloxServiceConsts.DELETE_DIALOG_SUCCESS_ACTION,
                QuickBloxServiceConsts.DELETE_DIALOG_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.DELETE_DIALOG_ACTION, deleteChatCommand);
    }

    private void registerLeaveGroupDialogCommand() {
        GroupChatHelper groupChatHelper = (GroupChatHelper) getHelper(GROUP_CHAT_HELPER);

        QBLeaveGroupDialogCommand leaveGroupDialogCommand = new QBLeaveGroupDialogCommand(this,
                groupChatHelper,
                QuickBloxServiceConsts.LEAVE_GROUP_DIALOG_SUCCESS_ACTION,
                QuickBloxServiceConsts.LEAVE_GROUP_DIALOG_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.LEAVE_GROUP_DIALOG_ACTION, leaveGroupDialogCommand);
    }

    private void registerAddFriendsToGroupCommand() {
        GroupChatHelper groupChatHelper = (GroupChatHelper) getHelper(GROUP_CHAT_HELPER);

        QBAddFriendsToGroupCommand addFriendsToGroupCommand = new QBAddFriendsToGroupCommand(this,
                groupChatHelper,
                QuickBloxServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION,
                QuickBloxServiceConsts.ADD_FRIENDS_TO_GROUP_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.ADD_FRIENDS_TO_GROUP_ACTION, addFriendsToGroupCommand);
    }

    private void registerCloudinaryImageUploadCommand() {
        CloudinaryHelper cloudinaryHelper = (CloudinaryHelper) getHelper(CLOUDINARY_HELPER);

        ServiceCommand cloudinaryImageUploadCommand = new CloudinaryImageUploadCommand(this, cloudinaryHelper,
                QuickBloxServiceConsts.CLOUDINARY_LOAD_SUCCESS_ACTION,
                QuickBloxServiceConsts.CLOUDINARY_LOAD_FAIL_ACTION);

        serviceCommandMap.put(QuickBloxServiceConsts.CLOUDINARY_LOAD_ACTION, cloudinaryImageUploadCommand);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action;
        if (intent != null && (action = intent.getAction()) != null) {
            Log.d(TAG, "service started with resultAction=" + action);
            ServiceCommand command = serviceCommandMap.get(action);
            if (command != null) {
                startAsync(command, intent);
            }
        }
        return START_NOT_STICKY;
    }

    private void startAsync(final ServiceCommand command, final Intent intent) {
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    command.execute(intent.getExtras());
                } catch (QBResponseException e) {
                    ErrorUtils.logError(e);
                    if (Utils.isExactError(e, Constants.SESSION_DOES_NOT_EXIST)) {
                        refreshSession();
                    } else if (Utils.isTokenDestroyedError(e)) {
                        forceReLogin();
                    }
                } catch (Exception e) {
                    ErrorUtils.logError(e);
                }
            }
        });
    }


    private void forceReLogin() {
        Intent intent = new Intent(QuickBloxServiceConsts.FORCE_RELOGIN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void refreshSession() {
        Intent intent = new Intent(QuickBloxServiceConsts.REFRESH_SESSION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public BaseHelper getHelper(int helperId) {
        return helpersMap.get(helperId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class QuickBloxServiceBinder extends Binder {

        public QuickBloxService getService() {
            return QuickBloxService.this;
        }

    }

    private class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive " + intent.getAction());
            String action = intent.getAction();
            if (action != null && QuickBloxServiceConsts.RE_LOGIN_IN_CHAT_SUCCESS_ACTION.equals(action)) {
                ((BaseChatHelper) getHelper(PRIVATE_CHAT_HELPER)).init(AppSession.getSession().getQbUser());
                ((BaseChatHelper) getHelper(GROUP_CHAT_HELPER)).init(AppSession.getSession().getQbUser());
            }
        }
    }
}
