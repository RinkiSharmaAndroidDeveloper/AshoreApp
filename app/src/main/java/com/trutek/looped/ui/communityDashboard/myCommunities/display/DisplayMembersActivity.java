package com.trutek.looped.ui.communityDashboard.myCommunities.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.utils.ToastUtils;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Rinki on 2/22/2017.
 */
public class DisplayMembersActivity extends BaseAppCompatActivity implements View.OnClickListener {

    public static final int ADD_CONNECTIONS_TO_COMMUNITY = 1;
    @Inject
    IActivityService activityService;
    @Inject
    IConnectionService _connectionService;
    @Inject
    ICommunityService communityService;


    ArrayList<MemberModel> memberList, backMembers;
    CommunityModel communityModel, community;
    ImageView right_arrow, add_members;
    RecyclerView recyclerView;
    DisplayMemberAdapter membersAdapter;
    private ArrayList<ConnectionModel> connections, addMemberList;
    String communityID;
    PageInput peopleInput;

    TextView textView_title;

    @Override
    protected int getContentResId() {
        return R.layout.activity_display_members;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memberList = new ArrayList<>();
        backMembers = new ArrayList<>();
        connections = new ArrayList<>();
        peopleInput = new PageInput();
        addMemberList = new ArrayList<>();
        initFields();
        loadData();
        setFonts();
        memberList = (ArrayList<MemberModel>) getIntent().getSerializableExtra("memberModel");
        communityID = (String) getIntent().getSerializableExtra("CommunityID");
        initMemberAdapter();
        selectUnAddedMember();
        right_arrow.setOnClickListener(this);
        add_members.setOnClickListener(this);
    }

    private void loadData() {
        if (connections != null) {
            connections.clear();
        }
        connections.addAll(_connectionService.getMyConnections(null));
    }

    public void initMemberAdapter() {
        membersAdapter = new DisplayMemberAdapter(this, memberList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(membersAdapter);
    }

    private void initFields() {
        right_arrow = (ImageView) findViewById(R.id.members_back);
        add_members = (ImageView) findViewById(R.id.imageview_add_members);
        recyclerView = (RecyclerView) findViewById(R.id.members_recyclerView);
        textView_title = (TextView) findViewById(R.id.interest_textView_categoryName);
    }

    private void setFonts() {
        textView_title.setTypeface(avenirNextRegular);
    }

    public void selectUnAddedMember() {
        int connection_size, member_size;
        Boolean isMemberExist = true;
        connection_size = connections.size();
        member_size = memberList.size();

        for (int i = 0; i < connection_size; i++) {
            for (int j = 0; j < member_size; j++) {
                if (connections.get(i).profile.getServerId().equals(memberList.get(j).profile.getServerId())) {
                    isMemberExist = true;
                    break;
                } else {
                    isMemberExist = false;
                }
            }
            if (!isMemberExist) {
                addMemberList.add(connections.get(i));
                isMemberExist = true;
            }
        }
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_CONNECTIONS_TO_COMMUNITY:
                    community = (CommunityModel) data.getSerializableExtra("communityModel");
                    joinnedCommunity();
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.members_back):
                finish();
                break;
            case (R.id.imageview_add_members):
                Intent intent = new Intent(this, AddMembersToCommunity.class);
                intent.putExtra("memberList", addMemberList);
                startActivityForResult(intent, ADD_CONNECTIONS_TO_COMMUNITY);
                break;
        }

    }

    public void joinnedCommunity() {
        community.setServerId(communityID);
        communityService.joinCommunityToMember(community, new AsyncResult<Page<CommunityModel>>() {
            @Override
            public void success(final Page<CommunityModel> communityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        List<CommunityModel> addedMemberList = new ArrayList<>();
                        community.isSelected = true;
                        for (CommunityModel community : communityModel.items) {
                            addedMemberList.add(community);
                        }
                        backMembers = addedNewCommnuityMembers(addedMemberList);
                        Intent intentBack = new Intent();
                        intentBack.putExtra("memberList", backMembers);
                        setResult(RESULT_OK, intentBack);
                        finish();
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

    public ArrayList<MemberModel> addedNewCommnuityMembers(List<CommunityModel> community) {
        ArrayList<MemberModel> memberModels = new ArrayList<>();
        MemberModel memberModel = new MemberModel();

        for (CommunityModel communityModel : community) {
            for (ConnectionModel connectionModel : connections) {
                if (communityModel.getServerId().equals(connectionModel.profile.getServerId())) {
                    memberModel.setProfile(connectionModel.getProfile());
                }
            }
            if (memberModel != null) {
                memberModels.add(memberModel);
            }
        }
        return memberModels;
    }
}
