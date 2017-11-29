package com.trutek.looped.ui.activity.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.base.BaseAppCompatActivity;

import com.trutek.looped.ui.communityDashboard.myCommunities.adapter.CommunityAdapter;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class CommunityListActivity extends BaseAppCompatActivity {

    @Inject
    ICommunityService communityService;

    @BindView(R.id.community_list) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ArrayList<CommunityModel> communities;
    private CommunityAdapter mAdapter;
    Boolean isJoinButtonActive =true;

    @Override
    protected int getContentResId() {
        return R.layout.activity_community_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        communities = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initAdapter();
        initListeners();
        getCommunities();
    }

    private void initListeners() {
        mRecyclerView.addOnItemTouchListener(
            new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(CommunityListActivity.this, CreateActivity.class);
                    intent.putExtra("communityModel", communities.get(position));
                    startActivity(intent);
                }
            })
        );
    }

    private void initAdapter() {
        mAdapter = new CommunityAdapter(this, communities,joinClickActionListeners,isJoinButtonActive,joinClickActionListeners);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
    OnActionListener<CommunityModel> joinClickActionListeners = new OnActionListener<CommunityModel>() {
        @Override
        public void notify(CommunityModel communityModel) {

        }
    };

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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getCommunities(){
//        communityService.allCommunities(new AsyncResult<Page<CommunityModel>>() {
//            @Override
//            public void success(final Page<CommunityModel> communityPage) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        communities.addAll(communityPage.items);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//
//            @Override
//            public void error(final String error) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtils.longToast(error);
//                    }
//                });
//            }
//        });

        List<CommunityModel> myCommunities = communityService.allCommunities();
        communities.addAll(myCommunities);
        mAdapter.notifyDataSetChanged();
    }

}
