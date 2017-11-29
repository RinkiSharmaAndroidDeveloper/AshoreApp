package com.trutek.looped.ui.chats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.quickblox.chat.model.QBChatDialog;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.chat.QBAddFriendsToGroupCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.ui.base.BaseChatActivity;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.chatmodule.utils.UserFriendUtils;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myConnections.SelectConnectionAdapter;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class AddFriendsToGroupActivity extends BaseChatActivity {

    public static final int RESULT_ADDED_FRIENDS = 9123;
    @Inject
    IConnectionService connectionService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView_connection) RecyclerView recyclerView;

    private DataManager dataManager;

    private List<ConnectionModel> connections;
    private ArrayList<ConnectionModel> selectedContacts;

    private OnActionListener<ConnectionModel> connectionSelectedActionListeners;
    private OnActionListener<ConnectionModel> connectionUnSelectedActionListeners;

    private List<Integer> friendIdsList;
    private SelectConnectionAdapter adapter;
    private QBChatDialog qbDialog;

    public static void start(Activity activity, QBChatDialog qbDialog) {
        Intent intent = new Intent(activity, AddFriendsToGroupActivity.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG, qbDialog);
        activity.startActivityForResult(intent, RESULT_ADDED_FRIENDS);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_add_friends_to_group;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        addActions();

        initFields();
        initAdapter();
        getConnections();
    }


    public void initFields(){
        connections = new ArrayList<>();
        selectedContacts = new ArrayList<>();
        dataManager = DataManager.getInstance();
        qbDialog = (QBChatDialog) getIntent().getExtras().getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);

        connectionSelectedActionListeners = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connection) {
                selectedContacts.add(connection);
            }
        };

        connectionUnSelectedActionListeners = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connection) {
                selectedContacts.remove(connection);
            }
        };
    }

    private void initAdapter() {
        adapter = new SelectConnectionAdapter(this, connections, connectionSelectedActionListeners,connectionUnSelectedActionListeners);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getConnections() {

        showProgress();
        connectionService.myConnection(new PageInput(), new AsyncResult<List<ConnectionModel>>() {
            @Override
            public void success(final List<ConnectionModel> connectionModels) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (ConnectionModel connection: connectionModels){
                            if(!qbDialog.getOccupants().contains(connection.getProfile().getChat().id)){
                                connections.add(connection);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        hideProgress();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                        hideProgress();
                    }
                });
            }
        });
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;

            case R.id.done:
                performDone();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void performDone() {
        if (!selectedContacts.isEmpty()) {
            boolean joined = groupChatHelper != null && groupChatHelper.isDialogJoined(qbDialog);
            if (isChatInitializedAndUserLoggedIn() && isNetworkAvailable() && joined) {
                showProgress();
                friendIdsList = UserFriendUtils.getFriendIds(selectedContacts);
                QBAddFriendsToGroupCommand.start(this, qbDialog.getDialogId(),
                        (ArrayList<Integer>) friendIdsList);
            } else {
                ToastUtils.longToast(R.string.chat_service_is_initializing);
            }
        } else {
            ToastUtils.longToast(R.string.add_friends_to_group_no_friends_for_adding);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.done,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    private void addActions() {
        addAction(QuickBloxServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION, new AddFriendsToGroupSuccessCommand());
        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QuickBloxServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION);
        updateBroadcastActionList();
    }

    private class AddFriendsToGroupSuccessCommand implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            Intent intent = new Intent();
            intent.putExtra(QuickBloxServiceConsts.EXTRA_FRIENDS, (Serializable) friendIdsList);
            setResult(RESULT_ADDED_FRIENDS, intent);
            finish();
        }
    }

}
