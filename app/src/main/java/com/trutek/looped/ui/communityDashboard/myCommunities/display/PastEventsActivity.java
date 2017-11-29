package com.trutek.looped.ui.communityDashboard.myCommunities.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.data.contracts.services.ICommentService;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.activity.display.DisplayActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.settings.Adapter.StarredMessagesAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Rinki on 3/23/2017.
 */
public class PastEventsActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Inject
    IActivityService _activityService;

    ImageView back_arrow;
    PastEventsAdapter mAdapter;
    RecyclerView recyclerView;
    List<ActivityModel> pastEventList = new ArrayList<>();
    String communityId;
    TextView defaultText;
    public static final int DISPLAY_ACTIVITY = 2;


    @Override
    protected int getContentResId() {
        return R.layout.activity_past_events;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        communityId = (String) getIntent().getSerializableExtra("communityId");
        init();
        getPastEvent();
        initAdapter();
        back_arrow.setOnClickListener(this);
    }
    public void isDefaultTextVisible(List<ActivityModel> pastEventList)
    {
           if (pastEventList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            defaultText.setVisibility(View.GONE);
        } else {
            defaultText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
    public void init() {
        back_arrow = (ImageView) findViewById(R.id.past_events_back);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_past_events);
        defaultText = (TextView) findViewById(R.id.textView_default_text);

    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initAdapter() {
        mAdapter = new PastEventsAdapter(pastEventList,activitySelectedActionListeners);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(mAdapter);
    }
    OnActionListener<ActivityModel> activitySelectedActionListeners = new OnActionListener<ActivityModel>() {
        @Override
        public void notify(ActivityModel activityModel) {
            Intent intent = new Intent(PastEventsActivity.this, DisplayActivity.class);
            intent.putExtra("activityModel", activityModel);
            startActivityForResult(intent, DISPLAY_ACTIVITY);
        }
    };

    private void getPastEvent() {
        _activityService.pastActivities(communityId, new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(final Page<ActivityModel> activityModelPage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pastEventList.addAll(activityModelPage.items);
                        mAdapter.notifyDataSetChanged();
                        isDefaultTextVisible(pastEventList);
                    }
                });
            }
            @Override
            public void error(String error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.past_events_back):
                finish();
                break;
        }

    }
}
