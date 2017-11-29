package com.trutek.looped.ui.communityDashboard.myCommunities.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Rinki on 2/23/2017.
 */
public class AddMembersToCommunity extends BaseAppCompatActivity implements View.OnClickListener {

    @Inject
    IActivityService activityService;

    ArrayList<CommunityModel> memberList;
    ArrayList<ConnectionModel> connectionsMembers;
    CommunityModel communityModel;
    ImageView right_arrow;
    TextView add_members,default_text;
    RecyclerView recyclerView;
    AddMemberToCommunityAdapter membersAdapter;
    int i = 0;


    @Override
    protected int getContentResId() {
        return R.layout.activity_add_members_to_community;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memberList = new ArrayList<>();
        communityModel = new CommunityModel();
        initFields();
        connectionsMembers = (ArrayList<ConnectionModel>) getIntent().getSerializableExtra("memberList");
        if(connectionsMembers.size()>0){
            default_text.setVisibility(View.GONE);
        }else {
            default_text.setVisibility(View.VISIBLE);
        }
        initMemberAdapter();
        right_arrow.setOnClickListener(this);
        add_members.setOnClickListener(this);
    }

    public void initMemberAdapter() {
        membersAdapter = new AddMemberToCommunityAdapter(connectionsMembers, connectionSelectedActionListeners, connectionUnSelectedActionListeners);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(membersAdapter);
    }

    private void initFields() {
        right_arrow = (ImageView) findViewById(R.id.members_back);
        add_members = (TextView) findViewById(R.id.imageview_add_members);
        default_text = (TextView) findViewById(R.id.textView_default_members);
        recyclerView = (RecyclerView) findViewById(R.id.members_recyclerView);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    OnActionListener<ConnectionModel> connectionSelectedActionListeners = new OnActionListener<ConnectionModel>() {
        @Override
        public void notify(ConnectionModel connectionModel) {
            communityModel.getProfileIds().add(connectionModel.profile.getServerId());
        }
    };
    OnActionListener<ConnectionModel> connectionUnSelectedActionListeners = new OnActionListener<ConnectionModel>() {
        @Override
        public void notify(ConnectionModel connectionModel) {
            communityModel.getProfileIds().remove(connectionModel.profile.getServerId());
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.members_back):
                finish();
                break;
            case (R.id.imageview_add_members):
                if(connectionsMembers.size()>0){
                    if(communityModel.profileIds !=null && communityModel.profileIds.size()>0) {
                        Intent intentBack = new Intent();
                        intentBack.putExtra("communityModel", communityModel);
                        setResult(RESULT_OK, intentBack);
                        finish();
                    }else{
                        Toast.makeText(AddMembersToCommunity.this,"Please select at least one connection",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AddMembersToCommunity.this, "There is no connection to add member", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

}
