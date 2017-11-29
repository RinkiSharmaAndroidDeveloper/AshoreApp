package com.trutek.looped.ui.chats;

import android.app.Activity;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.chat.QBUpdateGroupDialogCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.ui.base.BaseChatActivity;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.chats.adapters.GroupDialogOccupantsAdapter;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DetailsDialogActivity extends BaseChatActivity implements OnImagePickedListener,View.OnClickListener{

    public static final int UPDATE_DIALOG_REQUEST_CODE = 100;

    //@BindView(R.id.toolbar) Toolbar toolbar;
  //  @BindView(R.id.iv_group_image) MaskedImageView groupImage;
    @BindView(R.id.group_name_edit_text) EditText groupName;
    @BindView(R.id.recycler_view_members) RecyclerView recyclerViewMembers;
    @BindView(R.id.add_new_member) TextView addNewMember;

    private OnActionListener<ChatUserModel> memberSelectedActionListeners;

    private List<DialogNotificationModel.Type> currentNotificationTypeList;

    private Uri imageUri;
    private DataManager dataManager;
    private ImagePickHelper imagePickHelper;
    private QBChatDialog qbDialog;
    private String dialogId;
    private String groupNameCurrent;
    private String photoUrlOld;
    private String groupNameOld;
    private boolean isNeedUpdateImage;
    private ArrayList<Integer> newFriendIdsList;
    private List<ChatUserModel> occupantsList;
    private GroupDialogOccupantsAdapter adapter;
    private Integer userId;
    ImageView groupImage,backImage;
    TextView textDone;

    public static void start(Activity context, String dialogId) {
        Intent intent = new Intent(context, DetailsDialogActivity.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG_ID, dialogId);
        context.startActivityForResult(intent, UPDATE_DIALOG_REQUEST_CODE);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_details_dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        addActions();

        initFields();
        initListView();
        textDone.setOnClickListener(this);
        backImage.setOnClickListener(this);
    }

    public void initFields(){
        occupantsList = new ArrayList<>();
        dataManager = DataManager.getInstance();
        imagePickHelper = new ImagePickHelper();
        textDone =(TextView)findViewById(R.id.header_create_group_done);
        backImage =(ImageView)findViewById(R.id.detail_group_back);
        groupImage =(ImageView)findViewById(R.id.iv_group_image);
        dialogId = (String) getIntent().getExtras().getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG_ID);
        currentNotificationTypeList = new ArrayList<>();

        memberSelectedActionListeners = new OnActionListener<ChatUserModel>() {
            @Override
            public void notify(ChatUserModel member) {

            }
        };
    }

    private void canAddMember() {
        if(AppSession.getSession().getQbUser().getId().equals(qbDialog.getUserId())){
            addNewMember.setVisibility(View.VISIBLE);
        } else {
            addNewMember.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.iv_group_image)
    public void groupImage(){
        imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
    }

    private void initListView() {
        adapter = new GroupDialogOccupantsAdapter(this, memberSelectedActionListeners, occupantsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMembers.setLayoutManager(linearLayoutManager);
        recyclerViewMembers.setAdapter(adapter);
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
    protected void onResume() {
        super.onResume();

        fillUIWithData();
        canAddMember();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeActions();
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.header_create_group_done):
                checkForSaving();
                break;
            case (R.id.detail_group_back):
                finish();
                break;
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.done,menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    private void addActions() {
        addAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION, new LoadAttachFileSuccessAction());
        addAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION, failAction);

        addAction(QuickBloxServiceConsts.UPDATE_GROUP_DIALOG_SUCCESS_ACTION, new UpdateGroupDialogSuccessAction());
        addAction(QuickBloxServiceConsts.UPDATE_GROUP_DIALOG_FAIL_ACTION, new UpdateGroupFailAction());

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION);

        removeAction(QuickBloxServiceConsts.UPDATE_GROUP_DIALOG_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.UPDATE_GROUP_DIALOG_FAIL_ACTION);

        updateBroadcastActionList();
    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        initMaskedView(bitmap);
        imageUri = Uri.fromFile(file);
        isNeedUpdateImage = true;
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

    private void updateCurrentData() {
        qbDialog = ChatUtils.createQBDialogFromLocalDialog(dataManager,
                dataManager.getDialogRepository().getByServerId(qbDialog.getDialogId()));

        for (Integer chatUser : qbDialog.getOccupants()) {
            occupantsList.add(dataManager.getChatUserRepository().get(new PageQuery().add("userId", chatUser)));
        }
        groupNameCurrent = groupName.getText().toString();
    }

    private void updateOldGroupData() {
        groupNameOld = qbDialog.getName();
        photoUrlOld = qbDialog.getPhoto();
    }

    private void fillUIWithData() {
        updateDialog();
        groupName.setText(qbDialog.getName());

        if (!isNeedUpdateImage) {
            loadAvatar(qbDialog.getPhoto());
        }

        updateOldGroupData();
    }

    private boolean isGroupDataChanged() {
        return !groupNameCurrent.equals(groupNameOld) || isNeedUpdateImage;
    }

    private void checkForSaving() {
        updateCurrentData();
        if (isGroupDataChanged()) {
            saveChanges();
        }
    }

    private boolean isUserDataCorrect() {
        return !TextUtils.isEmpty(groupNameCurrent);
    }

    private void saveChanges() {
        if (!isUserDataCorrect()) {
            ToastUtils.longToast(R.string.dialog_details_name_not_entered);
            return;
        }

        if (!qbDialog.getName().equals(groupNameCurrent)) {
            qbDialog.setName(groupNameCurrent);

            currentNotificationTypeList.add(DialogNotificationModel.Type.NAME_DIALOG);
        }

        if (isNeedUpdateImage) {
            currentNotificationTypeList.add(DialogNotificationModel.Type.PHOTO_DIALOG);
            updateGroupDialog(ImageUtils.getCreatedFileFromUri(imageUri));
        } else {
            updateGroupDialog(null);
        }

        showProgress();
    }

    private void updateGroupDialog(File imageFile) {
        QBUpdateGroupDialogCommand.start(this, qbDialog, imageFile);
    }

    private void updateDialog() {
        List<ChatUserModel> occupantsList = new ArrayList<>();
        qbDialog = ChatUtils.createQBDialogFromLocalDialog(dataManager, dataManager.getDialogRepository().getByServerId(dialogId));
        adapter.setUserId(qbDialog.getUserId());
        for (Integer id: qbDialog.getOccupants()) {
            occupantsList.add(dataManager.getChatUserRepository().get(new PageQuery().add("dialogId", qbDialog.getDialogId()).add("userId", id)));
        }
        qbDialog.setOccupantsIds(ChatUtils.createOccupantsIdsFromUsersList(occupantsList));
        adapter.setNewData(occupantsList);
    }

    private void loadAvatar(String photoUrl) {
        if(photoUrl != null && !photoUrl.isEmpty() && photoUrl.contains("http")){
            ImageLoader.getInstance().displayImage(photoUrl, groupImage, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        } else {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.default_placeholder);
            groupImage.setImageBitmap(image);
        }
    }

    private void resetGroupData() {
        groupName.setText(groupNameOld);
        isNeedUpdateImage = false;
        loadAvatar(photoUrlOld);
    }

    @OnClick(R.id.add_new_member)
    public void addMember(){
        AddFriendsToGroupActivity.start(this, qbDialog);
    }

    private void sendNotificationToGroup(boolean leavedFromDialog) {
        for (DialogNotificationModel.Type messagesNotificationType : currentNotificationTypeList) {
            try {
                QBChatDialog localDialog = qbDialog;
                if (qbDialog != null) {
                    localDialog = ChatUtils.createQBDialogFromLocalDialogWithoutLeaved(dataManager,
                            dataManager.getDialogRepository().getByServerId(qbDialog.getDialogId()));
                }
                groupChatHelper.sendGroupMessageToFriends(localDialog, messagesNotificationType,
                        newFriendIdsList, leavedFromDialog, dataManager);
            } catch (QBResponseException e) {
                ErrorUtils.logError(e);
                hideProgress();
            }
        }
        currentNotificationTypeList.clear();
    }

    private void handleAddedFriends(Intent data) {
        newFriendIdsList = (ArrayList<Integer>) data.getSerializableExtra(QuickBloxServiceConsts.EXTRA_FRIENDS);
        if (newFriendIdsList != null) {

            updateCurrentData();
            updateOccupantsList();

            try {
                groupChatHelper.sendSystemMessageAboutCreatingGroupChat(qbDialog, newFriendIdsList);
            } catch (Exception e) {
                ErrorUtils.logError(e);
            }
            currentNotificationTypeList.add(DialogNotificationModel.Type.ADDED_DIALOG);
            sendNotificationToGroup(false);
        }
    }

    private void updateOccupantsList() {
        adapter.setNewData(occupantsList);
    }


    private class UpdateGroupDialogSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            qbDialog = (QBChatDialog) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);

            updateCurrentData();
            updateOldGroupData();
            fillUIWithData();

            sendNotificationToGroup(false);
            hideProgress();
            finish();
        }
    }

    private class UpdateGroupFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Exception exception = (Exception) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_ERROR);
            if (exception != null) {
                ToastUtils.longToast(exception.getMessage());
            }

            resetGroupData();
            hideProgress();
        }
    }

    private class LoadAttachFileSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddFriendsToGroupActivity.RESULT_ADDED_FRIENDS) {
            if (data != null) {
                handleAddedFriends(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
