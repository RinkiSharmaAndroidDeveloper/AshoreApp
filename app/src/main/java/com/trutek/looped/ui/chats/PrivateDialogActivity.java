package com.trutek.looped.ui.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.ui.chats.adapters.PrivateMessagesAdapter;

import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.BindView;

public class PrivateDialogActivity extends BaseDialogActivity {

    private final String TAG = PrivateDialogActivity.class.getSimpleName();
    @BindView(R.id.toolbar) Toolbar toolbar;

    public static void start(Context context, ChatUserModel opponent, DialogModel dialog) {
        Intent intent = new Intent(context, PrivateDialogActivity.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_OPPONENT, opponent);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG, dialog);
        context.startActivity(intent);
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

    private void initFields() {
        chatHelperIdentifier = QuickBloxService.PRIVATE_CHAT_HELPER;
        initActualExtras();

        combinationMessagesList = createCombinationMessagesList();

        if(opponentUser!= null){
            title = opponentUser.name;
            headerName.setText(title);
        }
    }

    @Override
    protected void initMessagesRecyclerView() {
        super.initMessagesRecyclerView();
        messagesAdapter = new PrivateMessagesAdapter(this, combinationMessagesList, dialog);
        messagesRecyclerView.addItemDecoration(
                new StickyRecyclerHeadersDecoration((StickyRecyclerHeadersAdapter) messagesAdapter));

        messagesRecyclerView.setAdapter(messagesAdapter);
        scrollMessagesToBottom();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkForCorrectChat();

        if (isNetworkAvailable()) {
            startLoadDialogMessages();
        }
    }

    @Override
    protected void updateMessagesList() {
        initActualExtras();
        checkForCorrectChat();

        int oldMessagesCount = messagesAdapter.getAllItems().size();

        this.combinationMessagesList = createCombinationMessagesList();
        Log.d(TAG, "combinationMessagesList = " + combinationMessagesList);
        messagesAdapter.setList(combinationMessagesList);

        checkForScrolling(oldMessagesCount);
    }

    @Override
    protected void onFileLoaded(QBFile file) {
        try {
            privateChatHelper.sendPrivateMessageWithAttachImage(file, opponentUser.getUserId());
        } catch (QBResponseException exc) {
            ErrorUtils.showError(this, exc);
        }
    }

    @Override
    protected Bundle generateBundleToInitDialog() {
        Bundle bundle = new Bundle();
        if (opponentUser != null && opponentUser.getUserId()!=null){
            bundle.putInt(QuickBloxServiceConsts.EXTRA_OPPONENT_ID, opponentUser.getUserId());
        }
        return bundle;
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        sendMessage(true);
    }

    @Override
    protected void onConnectServiceLocally(QuickBloxService service) {
        onConnectServiceLocally();
    }

    private void checkForCorrectChat() {
        DialogModel updatedDialog = null;
        if (dialog != null) {
            updatedDialog = dataManager.getDialogRepository().getByServerId(dialog.getServerId());
        } else {
            finish();
        }

        if (updatedDialog == null) {
            finish();
        } else {
            dialog = updatedDialog;
        }
    }

    @Override
    protected void addActions() {
        super.addActions();
    }

    private void initActualExtras() {
        opponentUser = (ChatUserModel) getIntent().getExtras().getSerializable(QuickBloxServiceConsts.EXTRA_OPPONENT);
        dialog = (DialogModel) getIntent().getExtras().getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);
    }

    public static class DateComparator implements Comparator<MessageModel> {

        @Override
        public int compare(MessageModel combinationMessage1, MessageModel combinationMessage2) {
            return ((Long) combinationMessage1.getDateSent()).compareTo(
                    ((Long) combinationMessage2.getDateSent()));
        }
    }

}
