package com.trutek.looped.ui.chats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.QBLoadAttachFileCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLoadDialogMessagesCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.CombinationMessage;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.data.impl.repository.DialogNotificationRepository;
import com.trutek.looped.chatmodule.data.impl.repository.DialogRepository;
import com.trutek.looped.chatmodule.data.impl.repository.MessageRepository;
import com.trutek.looped.chatmodule.helpers.BaseChatHelper;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.ui.base.BaseLoggableActivity;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.chats.adapters.base.BaseRecyclerViewAdapter;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class BaseDialogActivity extends BaseLoggableActivity implements OnImagePickedListener {

    private static final int DELAY_SCROLLING_LIST = 300;

    @BindView(R.id.header_name) TextView headerName;
    @BindView(R.id.recycler_view_messages) RecyclerView messagesRecyclerView;
    @BindView(R.id.edit_txt_msg) EditText messageEditText;
    @BindView(R.id.iv_comera) ImageView camera;

    protected DataManager dataManager;
    protected ChatUserModel opponentUser;
    protected DialogModel dialog;
    protected BaseRecyclerViewAdapter messagesAdapter;
    protected Resources resources;
    protected int chatHelperIdentifier;
    protected List<CombinationMessage> combinationMessagesList;
    protected BaseChatHelper baseChatHelper;
    protected ImagePickHelper imagePickHelper;

    private Handler mainThreadHandler;
    private boolean loadMore;

    private Observer dialogObserver;
    private Observer messageObserver;
    private Observer dialogNotificationObserver;
    private BroadcastReceiver updatingDialogBroadcastReceiver;
    private LoadAttachFileSuccessAction loadAttachFileSuccessAction;
    private LoadDialogMessagesSuccessAction loadDialogMessagesSuccessAction;
    private LoadDialogMessagesFailAction loadDialogMessagesFailAction;
    private Tracker mTracker;

    @Override
    protected int getContentResId() {
        return R.layout.activity_private_dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();

        addActions();
        registerBroadcastReceivers();
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.PRIVATECHAT_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @OnClick(R.id.iv_comera)
    public void attachment(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Uploading the image")
                .setAction("Private chat screen")
                .build());
        imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addObservers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        createChatLocally();
    }

    private void initFields() {
        mainThreadHandler = new Handler(Looper.getMainLooper());
        resources = getResources();
        dataManager = DataManager.getInstance();

        dialogObserver = new DialogObserver();
        messageObserver = new MessageObserver();
        dialogNotificationObserver = new DialogNotificationObserver();
        loadDialogMessagesSuccessAction = new LoadDialogMessagesSuccessAction();
        loadDialogMessagesFailAction = new LoadDialogMessagesFailAction();
        loadAttachFileSuccessAction = new LoadAttachFileSuccessAction();
        updatingDialogBroadcastReceiver = new UpdatingDialogBroadcastReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        imagePickHelper = new ImagePickHelper();

        PreferenceHelper.getPrefsHelper().savePreference(PreferenceHelper.PUSH_NEED_TO_OPEN_DIALOG, false);
    }

    protected abstract void updateMessagesList();

    protected abstract Bundle generateBundleToInitDialog();

    protected abstract void onConnectServiceLocally(QuickBloxService service);

    protected abstract void onFileLoaded(QBFile file);

    protected void initMessagesRecyclerView() {
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    protected void scrollMessagesToBottom() {
        if (!loadMore) {
            scrollMessagesWithDelay();
        }
    }

    private void scrollMessagesWithDelay() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }
        }, DELAY_SCROLLING_LIST);
    }

    protected void checkForScrolling(int oldMessagesCount) {
        if (oldMessagesCount != messagesAdapter.getAllItems().size()) {
            scrollMessagesToBottom();
        }
    }

    protected void updateData() {
        dialog =  dataManager.getDialogRepository().getByServerId(dialog.getServerId());
        updateMessagesList();

        headerName.setText(dialog.getName());
    }

    @Override
    public void onConnectedToService(QuickBloxService service) {
        super.onConnectedToService(service);
        onConnectServiceLocally(service);
    }

    protected void onConnectServiceLocally() {
        createChatLocally();
    }

    private void createChatLocally() {
        if (isNetworkAvailable()) {
            if (service != null) {
                baseChatHelper = (BaseChatHelper) service.getHelper(chatHelperIdentifier);
                Log.d("Fix double message", "baseChatHelper = " + baseChatHelper + "\n dialog = " + dialog);
                if (baseChatHelper != null && dialog != null) {
                    try {
                        baseChatHelper.createChatLocally(ChatUtils.createQBDialogFromLocalDialog(dataManager, dialog),
                                generateBundleToInitDialog());
                    } catch (QBResponseException e) {
                        ErrorUtils.showError(this, e.getMessage());
                        finish();
                    }
                }
            }
        }
    }

    private void closeChatLocally() {
        if (baseChatHelper != null && dialog != null) {
            baseChatHelper.closeChat(ChatUtils.createQBDialogFromLocalDialog(dataManager, dialog),
                    generateBundleToInitDialog());
        }
        dialog = null;
    }

    protected void sendMessage(boolean privateMessage) {
        if(messageEditText.getText().toString().isEmpty()){
            return;
        }

        boolean error = false;
        try {
            if (privateMessage) {
                ((PrivateChatHelper) baseChatHelper).sendPrivateMessage(
                        messageEditText.getText().toString(), opponentUser.getUserId());
            }
            else {
                ((GroupChatHelper) baseChatHelper).sendGroupMessage(dialog.getXmppRoomJid(),
                        messageEditText.getText().toString());
            }
        } catch (QBResponseException e) {
            ErrorUtils.showError(this, e);
            error = true;
        } catch (IllegalStateException e) {
            ErrorUtils.showError(this, this.getString(R.string.dlg_not_joined_room));
            error = true;
        } catch (Exception e) {
            ErrorUtils.showError(this, e);
            error = true;
        }

        if (!error) {
            messageEditText.setText(Constants.EMPTY_STRING);
            scrollMessagesToBottom();
        }
    }


    protected List<CombinationMessage> createCombinationMessagesList() {
        if (dialog == null) {
            Log.d("PrivateDialogActivity", "dialog = " + dialog);
            return null;
        }

        PageInput input = new PageInput();
        input.query.add("dialogId", dialog.getServerId());

        List<MessageModel> messagesList = dataManager.getMessageRepository().page(input).items;
        List<DialogNotificationModel> dialogNotificationsList = dataManager.getDialogNotificationRepository().page(input).items;

        return ChatUtils.createCombinationMessagesList(messagesList, dialogNotificationsList);
    }

    protected void startLoadDialogMessages() {
        if (dialog == null) {
            return;
        }

        List<DialogUserModel> dialogUsers = dataManager.getDialogUsersRepository().page(new PageInput(new PageQuery().add("dialogId", dialog.getDialogId()))).items;
        List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogUsers);

        MessageModel message;
        DialogNotificationModel dialogNotification;

        long messageDateSent = 0;

        if (loadMore) {
            message = dataManager.getMessageRepository().get(new PageQuery().add("lastMessage", true).add("dialogId", dialog.getDialogId()));
            dialogNotification = dataManager.getDialogNotificationRepository().get(new PageQuery().add("lastNotification", true).add("dialogId", dialog.getDialogId()));
            messageDateSent = ChatUtils.getDialogMessageCreatedDate(false, message, dialogNotification);
        } else {
            message = dataManager.getMessageRepository().get(new PageQuery().add("lastMessage", true).add("dialogId", dialog.getDialogId()));
            dialogNotification = dataManager.getDialogNotificationRepository().get(new PageQuery().add("lastNotification", true).add("dialogId", dialog.getDialogId()));
            messageDateSent = ChatUtils.getDialogMessageCreatedDate(true, message, dialogNotification);
        }

        startLoadDialogMessages(dialog, messageDateSent);
    }

    protected void startLoadDialogMessages(DialogModel dialog, long lastDateLoad) {
        QBLoadDialogMessagesCommand.start(this, ChatUtils.createQBDialogFromLocalDialog(dataManager, dialog),
                lastDateLoad, loadMore);
    }

    protected void addActions() {
        addAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION, loadAttachFileSuccessAction);
        addAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION, failAction);

        addAction(QuickBloxServiceConsts.LOAD_DIALOG_MESSAGES_SUCCESS_ACTION, loadDialogMessagesSuccessAction);
        addAction(QuickBloxServiceConsts.LOAD_DIALOG_MESSAGES_FAIL_ACTION, loadDialogMessagesFailAction);

        updateBroadcastActionList();
    }

    protected void removeActions() {
        removeAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION);

        removeAction(QuickBloxServiceConsts.LOAD_DIALOG_MESSAGES_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.LOAD_DIALOG_MESSAGES_FAIL_ACTION);

        updateBroadcastActionList();
    }

    @Override
    protected void loadDialogs() {
        super.loadDialogs();
        createChatLocally();
    }

    @Override
    protected void onDestroy() {
        closeChatLocally();
        removeActions();
        deleteObservers();
        unregisterBroadcastReceivers();
        super.onDestroy();
    }

    private void addObservers() {
        dataManager.getDialogRepository().addObserver(dialogObserver);
        dataManager.getMessageRepository().addObserver(messageObserver);
        dataManager.getDialogNotificationRepository().addObserver(dialogNotificationObserver);
    }

    private void deleteObservers() {
        dataManager.getDialogRepository().deleteObserver(dialogObserver);
        dataManager.getMessageRepository().deleteObserver(messageObserver);
        dataManager.getDialogNotificationRepository().deleteObserver(dialogNotificationObserver);
    }

    private void registerBroadcastReceivers() {
        localBroadcastManager.registerReceiver(updatingDialogBroadcastReceiver,
                new IntentFilter(QuickBloxServiceConsts.UPDATE_DIALOG));
    }

    private void unregisterBroadcastReceivers() {
        localBroadcastManager.unregisterReceiver(updatingDialogBroadcastReceiver);
    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        canPerformLogout.set(true);
        startLoadAttachFile(file);
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        canPerformLogout.set(true);
        ErrorUtils.logError(e);
    }

    @Override
    public void onImagePickClosed(int requestCode) {
        canPerformLogout.set(true);
    }

    protected void startLoadAttachFile(final File file) {
//        TwoButtonsDialogFragment.show(getSupportFragmentManager(), R.string.dialog_confirm_sending_attach,
//                new MaterialDialog.ButtonCallback() {
//                    @Override
//                    public void onPositive(MaterialDialog dialog) {
//                        super.onPositive(dialog);
                        showProgress();
                        QBLoadAttachFileCommand.start(BaseDialogActivity.this, file);
//                    }
//                });
    }

    private class DialogObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.d("Fix double message", "DialogObserver update(Observable observable, Object data) from " + BaseDialogActivity.class.getSimpleName());
            if (data != null && data.equals(DialogRepository.OBSERVE_KEY) && dialog != null) {
                dialog = dataManager.getDialogRepository().getByServerId(dialog.getDialogId());

            }
        }
    }

    private class MessageObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.d("Fix double message", "MessageObserver update(Observable observable, Object data) from " + BaseDialogActivity.class.getSimpleName());
            if (data != null && data.equals(MessageRepository.OBSERVE_KEY)) {
                updateMessagesList();
            }
        }
    }

    private class DialogNotificationObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.d("Fix double message", "DialogNotificationObserver update(Observable observable, Object data) from " + BaseDialogActivity.class.getSimpleName());
            if (data != null && data.equals(DialogNotificationRepository.OBSERVE_KEY)) {
                updateMessagesList();
            }
        }
    }

    private class UpdatingDialogBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(QuickBloxServiceConsts.UPDATE_DIALOG)) {
                updateData();
            }
        }
    }

    public class LoadDialogMessagesSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
//            messageSwipeRefreshLayout.setRefreshing(false);
            int totalEntries = bundle.getInt(QuickBloxServiceConsts.EXTRA_TOTAL_ENTRIES, Constants.ZERO_INT_VALUE);


            if (messagesAdapter != null && !messagesAdapter.isEmpty() && totalEntries != Constants.ZERO_INT_VALUE) {
                scrollMessagesToBottom();
            }

            loadMore = false;

//            hideActionBarProgress();
        }
    }

    public class LoadDialogMessagesFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
//            messageSwipeRefreshLayout.setRefreshing(false);

            loadMore = false;

//            hideActionBarProgress();
        }
    }


    public class LoadAttachFileSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            QBFile file = (QBFile) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_ATTACH_FILE);
            onFileLoaded(file);
            hideProgress();
        }
    }

}
