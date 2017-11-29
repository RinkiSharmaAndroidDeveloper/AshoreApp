package com.trutek.looped.ui.recipient.recipient.loops;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.LoopModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.StepThree.InviteFromLoopAdapter;
import com.trutek.looped.ui.communityDashboard.myCommunities.MyCommunitiesActivity;
import com.trutek.looped.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class InviteFromLoopActivity extends BaseAppCompatActivity {

    static final String TAG = InviteFromLoopActivity.class.getSimpleName();

    @Inject
    IConnectionService connectionService;

    @Inject
    IRecipientService recipientService;

    @Inject
    ICommunityService _CommunityService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view_from_loop) RecyclerView recyclerViewConnection;
    @BindView(R.id.button_invite)Button buttonInvite;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    TextView textView_done;

    private List<String> profileIds;
    private List<ConnectionModel> connections;
    private InviteFromLoopAdapter inviteFromLoopAdapter;

    private OnActionListener<ConnectionModel> onSelectedActionListener;
    private OnActionListener<ConnectionModel> onUnSelectedActionListener;

    CommunityModel communityModel;

    public static void start(Context context, RecipientModel recipient){
        Intent intent = new Intent(context, InviteFromLoopActivity.class);
        intent.putExtra("recipientModel", recipient);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_invite_from_loop;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initView();

        getDataFromServer();
    }

    private void initView() {
        profileIds = new ArrayList<>();

        onSelectedActionListener = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connectionModel) {
                profileIds.add(connectionModel.profile.getServerId());
                Log.d(TAG,"final profile onSelectedAction: " + profileIds.toString());
            }
        };

        onUnSelectedActionListener = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connectionModel) {
                profileIds.remove(connectionModel.profile.getServerId());
                Log.d(TAG,"final profile onUnSelectedAction: " + profileIds.toString());
            }
        };

        textView_done = (TextView) findViewById(R.id.invite_from_looped_textView_done);
        textView_done.setOnClickListener(onClick_inviteMember);
        communityModel = (CommunityModel) getIntent().getSerializableExtra(Constants.MODEL_COMMUNITY);

        setFonts();
        setAdapter();
    }

    private void setAdapter() {
        connections = new ArrayList<>();
        inviteFromLoopAdapter = new InviteFromLoopAdapter(connections, onSelectedActionListener, onUnSelectedActionListener);
        recyclerViewConnection.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewConnection.setAdapter(inviteFromLoopAdapter);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    public void getDataFromServer() {
        showProgress();
        connectionService.myConnection(new PageInput(), new AsyncResult<List<ConnectionModel>>() {
            @Override
            public void success(final List<ConnectionModel> connectionModels) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inviteFromLoopAdapter.addList(connectionModels);
                        inviteFromLoopAdapter.notifyDataSetChanged();
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

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        buttonInvite.setTypeface(avenirNextRegular);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_invite)
    public void done(){

        showProgress();

        if(profileIds.size() <= 0){
            Toast.makeText(getApplicationContext(),"Please select at least one connection",Toast.LENGTH_SHORT).show();
            return;
        }

        communityModel.setProfileIds(new ArrayList<String>(profileIds));

        _CommunityService.inviteMembersIntoCommunity(communityModel, new AsyncNotify() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Intent intent =new Intent(InviteFromLoopActivity.this, MyCommunitiesActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.shortToast(error);
                        hideProgress();
                    }
                });
            }
        });

        /*recipient.setLoops(loops);

        showProgress();
        recipientService.updateRecipient(recipient, new AsyncResult<RecipientModel>() {
            @Override
            public void success(RecipientModel recipientModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast("Invitation Sent");
                        finish();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
        unbinder = null;
    }

   /* @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }*/

    View.OnClickListener onClick_inviteMember = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == textView_done.getId()) {
                buttonInvite.performClick();
            }
        }
    };

}
