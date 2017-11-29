package com.trutek.looped.ui.communityDashboard.discoverPeople;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.FilterModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.filter.FilterActivity;
import com.trutek.looped.ui.profile.InterestTagsActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class DiscoverPeopleActivity extends BaseAppCompatActivity {

    public static final int FILTER_ACTIVITY = 1;

    @Inject
    IProfileService profileService;
    @Inject
    IConnectionService connectionService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.discover_people_recycler_view)RecyclerView recyclerDiscoverPeople;
    @BindView(R.id.text_discover_people)TextView text_discover_people;
    @BindView(R.id.discover_people_edit_text_search) EditText search;
    @BindView(R.id.discover_people_progress_bar) ProgressBar progressBar;
    @BindView(R.id.discover_people_swipeRefreshLayout)SwipeRefreshLayout discover_swipe_layout;

    private OnActionListener<ProfileModel> peopleSelectedActionListeners;
    private OnActionListener<ProfileModel> peopleUnSelectedActionListeners;

    private DiscoverPeopleAdapter peopleAdapter;

    private EndlessScrollListener scrollListenerPeople;
    private EndlessScrollListener scrollListenerCommunity;

    private ArrayList<ProfileModel> discoverPeoples;
    private ArrayList<ProfileModel> filteredProfiles;

    private FilterModel filter;
    PageInput peopleInput;

    TextView interest, topics, location;
    private Tracker mTracker;

    @Override
    protected int getContentResId() {
        return R.layout.activity_discover_people;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        init();
        showProgress();
        initializePeople();
        setFonts();
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.DISCOVERPEOPLE_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        text_discover_people.setTypeface(avenirNextRegular);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void init() {
        filter = new FilterModel();
        peopleInput = new PageInput();

        discoverPeoples = new ArrayList<>();
        filteredProfiles = new ArrayList<>();

        peopleSelectedActionListeners = new OnActionListener<ProfileModel>() {
            @Override
            public void notify(ProfileModel profile) {

                linkWithProfile(profile);
            }
        };

        peopleUnSelectedActionListeners = new OnActionListener<ProfileModel>() {
            @Override
            public void notify(ProfileModel interestModel) {

            }
        };

        discover_swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                discover_swipe_layout.setRefreshing(true);
                initializePeople();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                peopleAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initAdapter();
    }

    private void initializePeopleForFilter(String query) {
        peopleInput.pageNo = 1;
        if (scrollListenerPeople != null) {
            scrollListenerPeople.reset();
        }

        discoverPeoples.clear();
        filteredProfiles.clear();
        loadPeopleForFilter(query);
    }

    private void loadPeopleForFilter(String query) {
        showProgress();
        profileService.discoverProfilesForFilter(query, new AsyncResult<Page<ProfileModel>>() {
            @Override
            public void success(Page<ProfileModel> models) {

                for (ProfileModel item : models.items) {
                    if(!discoverPeoples.contains(item)){
                        discoverPeoples.add(item);
                    }

                }
                filteredProfiles.addAll(discoverPeoples);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(discover_swipe_layout.isRefreshing()) {
                            discover_swipe_layout.setRefreshing(false);
                        }
                        hideProgress();
                        peopleAdapter.notifyDataSetChanged();
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
                        if(discover_swipe_layout.isRefreshing()) {
                            discover_swipe_layout.setRefreshing(false);
                        }
                    }
                });
            }
        });
    }

    private void initAdapter() {
        peopleAdapter = new DiscoverPeopleAdapter(discoverPeoples, peopleSelectedActionListeners, peopleUnSelectedActionListeners,
                getApplicationContext(),filteredProfiles);
        recyclerDiscoverPeople.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerDiscoverPeople.setAdapter(peopleAdapter);
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
        final int[] current_progress = new int[1];
        SeekBar seekBar;
        Button btn_applyFilters;
        final Dialog filterDialog = new Dialog(DiscoverPeopleActivity.this);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setCanceledOnTouchOutside(false);
        filterDialog.setContentView(R.layout.layout_filter_dialog);

        interest=(TextView)filterDialog.findViewById(R.id.filter_dialog_edit_text_interest);
        topics=(TextView)filterDialog.findViewById(R.id.filter_dialog_edit_text_topics);
        location=(TextView)filterDialog.findViewById(R.id.filter_dialog_edit_text_location);

        txt_redius1=(TextView)filterDialog.findViewById(R.id.filter_dialog_txt_redius1);
        txt_redius2=(TextView)filterDialog.findViewById(R.id.filter_dialog_txt_redius2);
        txt_miles_0=(TextView)filterDialog.findViewById(R.id.filter_dialog_miles0);
        txt_miles_100=(TextView)filterDialog.findViewById(R.id.filter_dialog_miles100);

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
                        .setCategory("Discover people interest")
                        .setAction("Interest")
                        .build());
                Intent intent = new Intent(DiscoverPeopleActivity.this, InterestTagsActivity.class);
                intent.putExtra("filterModel", filter);
                intent.putExtra("OPEN_FORM", 1);
                startActivityForResult(intent, FILTER_ACTIVITY);
            }
        });

        topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Discover people topic")
                        .setAction("Topic")
                        .build());
                Intent intent = new Intent(DiscoverPeopleActivity.this, InterestTagsActivity.class);
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

    private void setInputPage(int progress) {

        String query = makeQuery(progress);
        initializePeopleForFilter(query);
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

    private void initializePeople() {
        peopleInput.pageNo = 1;
        if (scrollListenerPeople != null) {
            scrollListenerPeople.reset();
        }

        filteredProfiles.clear();
        discoverPeoples.clear();
        peopleAdapter.notifyDataSetChanged();
        loadPeople();
    }

    private void loadPeople() {
        profileService.discoverProfiles(peopleInput, new AsyncResult<Page<ProfileModel>>() {
            @Override
            public void success(Page<ProfileModel> models) {

                for (ProfileModel item : models.items) {
                    if(!discoverPeoples.contains(item)){
                        discoverPeoples.add(item);
                    }
                }
                filteredProfiles.addAll(discoverPeoples);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(discover_swipe_layout.isRefreshing()) {
                            discover_swipe_layout.setRefreshing(false);
                        }
                        hideProgress();
                        peopleAdapter.notifyDataSetChanged();
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
                        if(discover_swipe_layout.isRefreshing()) {
                            discover_swipe_layout.setRefreshing(false);
                        }

                    }
                });
            }
        });
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

    private void linkWithProfile(ProfileModel profile){
        ConnectionModel model = new ConnectionModel();
        model.profile = profile;
        connectionService.linkConnection(model, new AsyncResult<ConnectionModel>() {
            @Override
            public void success(ConnectionModel connectionModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("Connect Successfully");
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
    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerDiscoverPeople.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        recyclerDiscoverPeople.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.filter_icon_layout,menu);
        return super.onCreateOptionsMenu(menu);

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
