package com.trutek.looped.ui.communityDashboard.discoverCommunity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.FilterModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.profile.InterestTagsActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

public class DiscoverCommunityActivity extends BaseAppCompatActivity {

    public static final int FILTER_ACTIVITY = 1;

    @Inject
    ICommunityService communityService;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.discover_community_recycler_view) RecyclerView recyclerViewCommunity;
    @BindView(R.id.discover_community_edit_text_search)EditText community_search;
    @BindView(R.id.discover_comm_progress_bar)ProgressBar progressBar;
    @BindView(R.id.discover_community_swipeRefreshLayout) SwipeRefreshLayout community_swipe_layout;

    private OnActionListener<CommunityModel> communitySelectedActionListeners;
    private OnActionListener<CommunityModel> communityUnSelectedActionListeners;
    private ArrayList<CommunityModel> filteredCommunities;
    private EndlessScrollListener scrollListenerPeople;
    private EndlessScrollListener scrollListenerCommunity;
    private DiscoverCommunityAdapter communityAdapter;

    private ArrayList<CommunityModel> communities;
    PageInput communityInput;

    private FilterModel filter;
    TextView interest, topics, location;
    private Tracker mTracker;

    @Override
    protected int getContentResId() {
        return R.layout.activity_discover_community;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        init();
        showProgress();
        initializeCommunities();
        setFonts();
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.DISCOVERCOMMUNITY_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        community_search.setTypeface(avenirNextRegular);
    }

    private void initAdapter() {
        communityAdapter = new DiscoverCommunityAdapter(communities, communitySelectedActionListeners, communityUnSelectedActionListeners,
                getApplicationContext(),
                filteredCommunities);
        recyclerViewCommunity.setLayoutManager(new GridLayoutManager(this,2));
        recyclerViewCommunity.setAdapter(communityAdapter);
    }

    private void init(){
        filter = new FilterModel();
        communityInput = new PageInput();
        communities = new ArrayList<>();
        filteredCommunities = new ArrayList<>();

        communitySelectedActionListeners  = new OnActionListener<CommunityModel>() {
            @Override
            public void notify(CommunityModel communityModel) {
                joinCommunity(communityModel);
            }
        };

        communityUnSelectedActionListeners  = new OnActionListener<CommunityModel>() {
            @Override
            public void notify(CommunityModel interestModel) {

            }
        };

        community_swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                community_swipe_layout.setRefreshing(true);
                initializeCommunities();
            }
        });

        community_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                communityAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initAdapter();
    }

    private void joinCommunity(CommunityModel community){
        communityService.joinCommunity(community, new AsyncResult<CommunityModel>() {
            @Override
            public void success(CommunityModel model) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("Join Successfully");
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    private void initializeCommunities() {
        communityInput.pageNo = 1;
        if (scrollListenerCommunity != null) {
            scrollListenerCommunity.reset();
        }

        communities.clear();
        filteredCommunities.clear();
        communityAdapter.notifyDataSetChanged();
        loadCommunities();
    }

    private void loadCommunities() {
        communityService.discoverCommunities(communityInput, new AsyncResult<Page<CommunityModel>>() {
            @Override
            public void success(Page<CommunityModel> models) {

                for (CommunityModel item : models.items) {
                    if(!communities.contains(item)){
                        communities.add(item);
                    }
                }
                filteredCommunities.addAll(communities);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(community_swipe_layout.isRefreshing()) {
                            community_swipe_layout.setRefreshing(false);
                        }

                        hideProgress();
                        communityAdapter.notifyDataSetChanged();

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
                        if(community_swipe_layout.isRefreshing()) {
                            community_swipe_layout.setRefreshing(false);
                        }
                    }
                });
            }
        });
    }

    private void setInputPage(int progress) {

        String query = makeQuery(progress);
        initializeCommunitiesForFilter(query);
    }

    private String makeQuery(int progress){
        String query = "?";

        for (InterestModel interestModel : filter.interests){
            query = query + "interests=" + interestModel.getServerId() + "&";
        }

        for (TagModel tag : filter.tags){
            query = query + "tags=" + tag.getServerId() + "&";
        }

        query = query + "around" + progress;

        return query;
    }

    private void initializeCommunitiesForFilter(String query) {

        communityInput.pageNo = 1;
        if (scrollListenerCommunity != null) {
            scrollListenerCommunity.reset();
        }

        communities.clear();
        filteredCommunities.clear();
        loadCommunitiesForFilter(query);
    }

    private void loadCommunitiesForFilter(String query) {
        showProgress();
        communityService.discoverCommunitiesForFilter(query, new AsyncResult<Page<CommunityModel>>() {
            @Override
            public void success(Page<CommunityModel> models) {

                for (CommunityModel item : models.items) {
                    if(!communities.contains(item)){
                        communities.add(item);
                    }
                }
                filteredCommunities.addAll(communities);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        communityAdapter.notifyDataSetChanged();
                        if(community_swipe_layout.isRefreshing()) {
                            community_swipe_layout.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(community_swipe_layout.isRefreshing()) {
                            community_swipe_layout.setRefreshing(false);
                        }
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.filter:
                showFilterDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showFilterDialog(){
        final TextView txt_redius1,txt_redius2, txt_miles_0,txt_miles_100;
        SeekBar seekBar;
        final int[] current_progress = new int[1];
        Button btn_applyFilters;
        final Dialog filterDialog = new Dialog(DiscoverCommunityActivity.this);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setCanceledOnTouchOutside(false);
        filterDialog.setContentView(R.layout.layout_filter_dialog);

        txt_redius1=(TextView)filterDialog.findViewById(R.id.filter_dialog_txt_redius1);
        txt_redius2=(TextView)filterDialog.findViewById(R.id.filter_dialog_txt_redius2);
        txt_miles_0=(TextView)filterDialog.findViewById(R.id.filter_dialog_miles0);
        txt_miles_100=(TextView)filterDialog.findViewById(R.id.filter_dialog_miles100);


        interest=(TextView)filterDialog.findViewById(R.id.filter_dialog_edit_text_interest);
        topics=(TextView)filterDialog.findViewById(R.id.filter_dialog_edit_text_topics);
        location=(TextView)filterDialog.findViewById(R.id.filter_dialog_edit_text_location);

        btn_applyFilters = (Button)filterDialog.findViewById(R.id.filter_dialog_btn);
        seekBar = (SeekBar)filterDialog.findViewById(R.id.filter_dialog_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                current_progress[0] = seekBar.getProgress();
                if (current_progress[0] != seekBar.getMax())
                    txt_miles_100.setText(Integer.toString(current_progress[0])+" Miles");
                else
                    txt_miles_100.setText("Everywhere");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        filterDialog.show();

        interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Discover community interest")
                        .setAction("Interest")
                        .build());
                Intent intent = new Intent(DiscoverCommunityActivity.this, InterestTagsActivity.class);
                intent.putExtra("filterModel", filter);
                intent.putExtra("OPEN_FORM", 1);
                startActivityForResult(intent, FILTER_ACTIVITY);
            }
        });

        topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Discover community topic")
                        .setAction("Topics")
                        .build());
                Intent intent = new Intent(DiscoverCommunityActivity.this, InterestTagsActivity.class);
                intent.putExtra("filterModel", filter);
                intent.putExtra("OPEN_FORM", 1);
                startActivityForResult(intent, FILTER_ACTIVITY);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });

        btn_applyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setInputPage(current_progress[0]);
                filterDialog.dismiss();
            }
        });

    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewCommunity.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        recyclerViewCommunity.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.filter_icon_layout,menu);
        return super.onCreateOptionsMenu(menu);

    }

    private void setDialogData(FilterModel filter) {

        if(filter.tags.size() > 0){
            if(filter.tags.size() == 1)
                interest.setText(filter.tags.size() + " " + "category selected");
            else
                interest.setText(filter.tags.size() + " " + "categories selected");
        } else {
            interest.setText(getString(R.string.category));
        }

        if(filter.interests.size() > 0){
            if(filter.interests.size() == 1)
                topics.setText(filter.interests.size() + " " + "topic selected");
            else
                topics.setText(filter.interests.size() + " " + "topics selected");
        } else {
            topics.setText(getString(R.string.topics));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FILTER_ACTIVITY && resultCode == RESULT_OK){

            filter = (FilterModel) data.getSerializableExtra("filterModel");

            setDialogData(filter);
        }

    }

}
