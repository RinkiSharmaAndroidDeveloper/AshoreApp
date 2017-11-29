package com.trutek.looped.ui.chats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.model.QBFile;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.QBLoadAttachFileCommand;
import com.trutek.looped.chatmodule.commands.chat.QBCreateGroupDialogCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.services.IChatUserService;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myConnections.SelectConnectionAdapter;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateGroupDialogActivity extends BaseAppCompatActivity implements OnImagePickedListener,View.OnClickListener{

    @Inject
    IConnectionService connectionService;

    private ArrayList<ConnectionModel> connections;
    private ArrayList<ConnectionModel> selectedContacts;

    private OnActionListener<ConnectionModel> connectionSelectedActionListeners;
    private OnActionListener<ConnectionModel> connectionUnSelectedActionListeners;

    private SelectConnectionAdapter adapter;
    private ImagePickHelper imagePickHelper;
    private QBFile qbFile;
    private Uri imageUri;
    private DataManager dataManager;
    private Tracker mTracker;

   // @BindView(R.id.toolbar) Toolbar toolbar;
   // @BindView(R.id.iv_group_image) MaskedImageView groupImage;
    @BindView(R.id.group_name_edit_text) EditText groupName;
    @BindView(R.id.recycler_view_connection) RecyclerView recyclerViewConnection;
    TextView createGroup;
    ImageView back_button,groupImage;

    @Override
    protected int getContentResId() {
        return R.layout.activity_create_group_dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setSupportActionBar(toolbar);

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        addActions();

        initFields();

        initAdapter();
        getConnections();
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.GROUPCHAT_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        createGroup.setOnClickListener(this);
        back_button.setOnClickListener(this);
    }

    public void initFields(){
        connections = new ArrayList<>();
        selectedContacts = new ArrayList<>();
        dataManager = DataManager.getInstance();
        imagePickHelper = new ImagePickHelper();
        createGroup =(TextView)findViewById(R.id.textview_create_group);
        back_button =(ImageView)findViewById(R.id.image_view_back);
        groupImage =(ImageView)findViewById(R.id.iv_group_image);
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

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeActions();
    }

    private void getConnections() {

        showProgress();
        connectionService.myConnection(new PageInput(), new AsyncResult<List<ConnectionModel>>() {
            @Override
            public void success(final List<ConnectionModel> connectionModels) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connections.addAll(connectionModels);
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

    private void addActions() {
        addAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION, new LoadAttachFileSuccessAction());
        addAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION, failAction);

        addAction(QuickBloxServiceConsts.CREATE_GROUP_CHAT_SUCCESS_ACTION, new CreateGroupChatSuccessAction());
        addAction(QuickBloxServiceConsts.CREATE_GROUP_CHAT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION);

        removeAction(QuickBloxServiceConsts.CREATE_GROUP_CHAT_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.CREATE_GROUP_CHAT_FAIL_ACTION);

        updateBroadcastActionList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
/*
    @Override
    public boolean onOptiodunginsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;

            *//*case R.id.done:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Group created")
                        .setAction(Constants.GROUPCHAT_SCREEN)
                        .build());
                performDone();
                break;*//*
        }
        return super.onOptionsItemSelected(item);
    }*/

    @OnClick(R.id.iv_group_image)
    public void groupImage(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Group Image uploading")
                .setAction(Constants.GROUPCHAT_SCREEN)
                .build());
        imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
    }

    protected void performDone() {

        if (!TextUtils.isEmpty(groupName.getText())) {
            checkForCreatingGroupChat();
        } else {
            ToastUtils.longToast(getString(R.string.create_group_empty_group_name));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.done,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initAdapter() {
        adapter = new SelectConnectionAdapter(this, connections, connectionSelectedActionListeners,connectionUnSelectedActionListeners);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewConnection.setLayoutManager(linearLayoutManager);
        recyclerViewConnection.setAdapter(adapter);
    }

    private void checkForCreatingGroupChat() {
        if (isChatInitializedAndUserLoggedIn()) {
            if (selectedContacts != null && !selectedContacts.isEmpty() && selectedContacts.size() >= 1) {
                showProgress();

                if (imageUri != null) {
                    QBLoadAttachFileCommand.start(CreateGroupDialogActivity.this, ImageUtils.getCreatedFileFromUri(imageUri));
                } else {
                    createGroupChat();
                }
            } else {
                ToastUtils.longToast(getString(R.string.create_group_empty_group_user));
            }
        } else {
            ToastUtils.longToast(R.string.chat_service_is_initializing);
        }
    }

    private void createGroupChat() {
        String photoUrl = qbFile != null ? qbFile.getPublicUrl() : null;
        QBCreateGroupDialogCommand.start(this, groupName.getText().toString(), selectedContacts, photoUrl);
    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        initMaskedView(bitmap);
        imageUri = Uri.fromFile(file);
    }

    private void initMaskedView(Bitmap bitmap) {
        groupImage.setImageBitmap(bitmap);
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        ErrorUtils.showError(this, e);
    }

    @Override
    public void onImagePickClosed(int requestCode) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.textview_create_group):
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Group created")
                        .setAction(Constants.GROUPCHAT_SCREEN)
                        .build());
                performDone();
                break;
            case (R.id.image_view_back):
                finish();
                break;
        }
    }

    private class LoadAttachFileSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            qbFile = (QBFile) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_ATTACH_FILE);
            createGroupChat();
        }
    }

    private class CreateGroupChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            QBChatDialog dialog = (QBChatDialog) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);

            if (dialog != null) {
                GroupDialogActivity.start(CreateGroupDialogActivity.this, ChatUtils.createLocalDialog(dialog));
                finish();
            } else {
                ErrorUtils.showError(CreateGroupDialogActivity.this, getString(R.string.dlg_fail_create_groupchat));
            }
        }
    }
}
