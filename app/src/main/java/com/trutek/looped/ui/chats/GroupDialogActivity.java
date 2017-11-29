package com.trutek.looped.ui.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.chats.adapters.GroupMessagesAdapter;

import java.util.ArrayList;

import butterknife.BindView;

public class GroupDialogActivity extends BaseDialogActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    public static void start(Context context, DialogModel dialog) {
        Intent intent = new Intent(context, GroupDialogActivity.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG, dialog);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_group_dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFields();

        if (dialog == null) {
            finish();
        }

        initMessagesRecyclerView();
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void initMessagesRecyclerView() {
        super.initMessagesRecyclerView();
        messagesAdapter = new GroupMessagesAdapter(this, combinationMessagesList, dialog);
        messagesRecyclerView.addItemDecoration(
                new StickyRecyclerHeadersDecoration((StickyRecyclerHeadersAdapter) messagesAdapter));
        messagesRecyclerView.setAdapter(messagesAdapter);

        scrollMessagesToBottom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();

        if (isNetworkAvailable()) {
            startLoadDialogMessages();
            PreferenceHelper.getPrefsHelper().savePreference(PreferenceHelper.CURRENTLY_VISIBLE_DIALOG_ID,dialog.getDialogId());
        }
    }

    @Override
    protected void updateMessagesList() {
        int oldMessagesCount = messagesAdapter.getAllItems().size();

        this.combinationMessagesList = createCombinationMessagesList();
        messagesAdapter.setList(combinationMessagesList);

        checkForScrolling(oldMessagesCount);
    }

    @Override
    protected Bundle generateBundleToInitDialog() {
        return null;
    }

    @Override
    protected void onConnectServiceLocally(QuickBloxService service) {
        onConnectServiceLocally();
    }

    @Override
    protected void onFileLoaded(QBFile file) {
        try {
            ((GroupChatHelper) baseChatHelper).sendGroupMessageWithAttachImage(dialog.getXmppRoomJid(), file);
        } catch (QBResponseException e) {
            ErrorUtils.showError(this, e);
        }
    }

    private void initFields() {
        chatHelperIdentifier = QuickBloxService.GROUP_CHAT_HELPER;
        dialog = (DialogModel) getIntent().getExtras().getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);
        combinationMessagesList = createCombinationMessagesList();
        if (dialog != null)
            title = dialog.getName();

        headerName.setText(title);
    }

    public void sendMessage(View view) {
        sendMessage(false);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.edit_marker:
                DetailsDialogActivity.start(this, dialog.getDialogId());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_icon_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        PreferenceHelper.getPrefsHelper().savePreference(PreferenceHelper.CURRENTLY_VISIBLE_DIALOG_ID,"");
        super.onPause();
    }
}
