package com.trutek.looped.ui.profile.create;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.App;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.ui.profile.create.adapter.DiscoverCommunityAdapter;
import com.trutek.looped.ui.profile.create.adapter.DiscoverPeopleAdapter;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateProfileStep2Activity extends BaseAppCompatActivity {

    @Inject
    ICommunityService communityService;
    @Inject
    IProfileService profileService;
    @Inject
    IConnectionService connectionService;

    @BindView(R.id.discover_edit_text_topic_search) EditText searchPeople;
    @BindView(R.id.discover_edit_text_interest_search) EditText searchCommunity;
    @BindView(R.id.discover_recycler_view_topic) RecyclerView recyclerViewPeople;
    @BindView(R.id.discover_recycler_view_interest) RecyclerView recyclerViewCommunity;
    @BindView(R.id.discover_create_profile_button_next)Button button_next;
    @BindView(R.id.tv_terms_and_cond)TextView termsAndCondition;
    @BindView(R.id.progress_bar_discoverPeople) ProgressBar progressBar_discoverPeople;
    @BindView(R.id.progress_bar_discoverComm) ProgressBar progressBar_discoverComm;

    private OnActionListener<ProfileModel> peopleSelectedActionListeners;
    private OnActionListener<ProfileModel> peopleUnSelectedActionListeners;
    private OnActionListener<CommunityModel> communitySelectedActionListeners;
    private OnActionListener<CommunityModel> communityUnSelectedActionListeners;

    private EndlessScrollListener scrollListenerPeople;
    private EndlessScrollListener scrollListenerCommunity;

    private DiscoverPeopleAdapter peopleAdapter;
    private DiscoverCommunityAdapter communityAdapter;

    private ArrayList<ProfileModel> peoples;
    private ArrayList<CommunityModel> communities;

    private ArrayList<ProfileModel> filteredProfiles;
    private ArrayList<CommunityModel> filteredCommunities;

    PageInput peopleInput;
    PageInput communityInput;

    @Override
    protected int getContentResId() {
        return R.layout.activity_create_profile_step2_discover;
    }

    public static void start(Activity context) {
        Intent intent = new Intent(context, CreateProfileStep2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setFonts();

        peopleAdapter = new DiscoverPeopleAdapter(peoples, filteredProfiles, peopleSelectedActionListeners, peopleUnSelectedActionListeners,getApplicationContext());
        communityAdapter = new DiscoverCommunityAdapter(communities, filteredCommunities, communitySelectedActionListeners, communityUnSelectedActionListeners,getApplicationContext());
        initializePeople();
        initializeCommunities();
        initAdapters();
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        searchCommunity.setTypeface(avenirNextRegular);
        searchPeople.setTypeface(avenirNextRegular);
        button_next.setTypeface(avenirNextRegular);
        termsAndCondition.setTypeface(avenirNextRegular);
    }

    @OnClick(R.id.discover_create_profile_button_next)
    public void nextStep(){
        DialogUtil.showInviteDialog(CreateProfileStep2Activity.this,inviteDialogCallback);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initAdapters() {
//        recyclerViewPeople.addOnScrollListener(scrollListenerPeople);
//        recyclerViewCommunity.addOnScrollListener(scrollListenerCommunity);

        final LinearLayoutManager layoutManagerPeople = new LinearLayoutManager(this);
        layoutManagerPeople.setOrientation(LinearLayoutManager.HORIZONTAL);
        final LinearLayoutManager layoutManagerCommunity = new LinearLayoutManager(this);
        layoutManagerCommunity.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerViewPeople.setLayoutManager(layoutManagerPeople);
        recyclerViewCommunity.setLayoutManager(layoutManagerCommunity);

        recyclerViewPeople.setAdapter(peopleAdapter);
        recyclerViewCommunity.setAdapter(communityAdapter);
    }

    private void init(){
        peopleInput = new PageInput();
        communityInput = new PageInput();

        peoples = new ArrayList<>();
        communities = new ArrayList<>();

        filteredProfiles = new ArrayList<>();
        filteredCommunities = new ArrayList<>();

        searchPeople.addTextChangedListener(new TextWatcher() {
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

        searchCommunity.addTextChangedListener(new TextWatcher() {
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

        peopleSelectedActionListeners = new OnActionListener<ProfileModel>() {
            @Override
            public void notify(ProfileModel profile) {

                linkWithProfile(profile);
            }
        };

        peopleUnSelectedActionListeners = new OnActionListener<ProfileModel>() {
            @Override
            public void notify(ProfileModel profile) {

                deLinkWithProfile(profile);
            }
        };

        communitySelectedActionListeners  = new OnActionListener<CommunityModel>() {
            @Override
            public void notify(CommunityModel community) {

                joinCommunity(community);
            }
        };

        communityUnSelectedActionListeners  = new OnActionListener<CommunityModel>() {
            @Override
            public void notify(CommunityModel community) {

                unJoinCommunity(community);
            }
        };

        recyclerViewPeople.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ProfileModel profile = peoples.get(position);

            }
        }));

        recyclerViewCommunity.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                CommunityModel community = communities.get(position);

            }
        }));
    }

    private void InvitePopup() {
        final Dialog inviteDialog = new Dialog(this);
        inviteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inviteDialog.setCanceledOnTouchOutside(false);
        inviteDialog.setContentView(R.layout.invite_pop_up);
        Button button_invite;
        Button button_skip;
        final TextView text_line_one,text_line_two,text_line_three;
        button_invite = (Button) inviteDialog.findViewById(R.id.popup_invite_button);
        text_line_one = (TextView) inviteDialog.findViewById(R.id.text_popup_invite_line_one);
        text_line_two=(TextView)inviteDialog.findViewById(R.id.text_popup_invite_line_two);
        text_line_three=(TextView)inviteDialog.findViewById(R.id.text_popup_invite_line_three);
        button_skip = (Button) inviteDialog.findViewById(R.id.invite_button_skip);
        Typeface avenirNextRegular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        button_invite.setTypeface(avenirNextRegular);
        button_skip.setTypeface(avenirNextRegular);
        text_line_one.setTypeface(avenirNextRegular);
        text_line_two.setTypeface(avenirNextRegular);
        text_line_three.setTypeface(avenirNextRegular);
        button_skip.setTypeface(avenirNextRegular);
        inviteDialog.show();
        button_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDialog.dismiss();
                Intent intent = new Intent(getApplication(), CreateProfileStep3Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                finish();
            }
        });
        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDialog.dismiss();
                setProfileStatusDone();
                HomeActivity.start(getApplication());
                finish();
            }
        });
    }

    private void initializePeople() {

        peopleInput.pageNo = 1;
        if (scrollListenerPeople != null) {
            scrollListenerPeople.reset();
        }

        peoples.clear();
        loadPeople();
    }

    private void loadPeople() {
        showProgress();
        profileService.discoverProfiles(peopleInput, new AsyncResult<Page<ProfileModel>>() {
            @Override
            public void success(Page<ProfileModel> models) {

                for (ProfileModel item : models.items) {
                    if(!peoples.contains(item)){
                        peoples.add(item);
                    }
                }
                filteredProfiles.addAll(peoples);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        peopleAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void error(final String error) {
                hideProgress();
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
        loadCommunities();
    }

    private void loadCommunities() {
        showProgressComm();
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
                        hideProgressComm();
                        communityAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressComm();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
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

    private void deLinkWithProfile(ProfileModel profile){
        ConnectionModel model = new ConnectionModel();
        model.profile = profile;
        /*connectionService.deLinkConnection(model, new AsyncResult<ConnectionModel>() {
            @Override
            public void success(ConnectionModel model) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("Disconnect Successfully");
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
        });*/
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

    private void unJoinCommunity(CommunityModel community){
        communityService.unJoinCommunity(community, new AsyncNotify() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("unJoin Successfully");
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

    // progressBar People RecyclerView
    @Override
    public void showProgress() {
        super.showProgress();

        progressBar_discoverPeople.setVisibility(View.VISIBLE);
        recyclerViewPeople.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        super.hideProgress();

        progressBar_discoverPeople.setVisibility(View.GONE);
        recyclerViewPeople.setVisibility(View.VISIBLE);
    }
    //progressBar Community RecyclerView
    public void showProgressComm() {
        progressBar_discoverComm.setVisibility(View.VISIBLE);
        recyclerViewCommunity.setVisibility(View.GONE);
    }

    public void hideProgressComm() {
        progressBar_discoverComm.setVisibility(View.GONE);
        recyclerViewCommunity.setVisibility(View.VISIBLE);
    }

    AsyncNotify inviteDialogCallback=new AsyncNotify() {
        @Override
        public void success() {
            Intent intent = new Intent(getApplication(), CreateProfileStep3Activity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
            finish();
        }

        @Override
        public void error(String error) {
            HomeActivity.start(getApplication());
            finish();
        }
    };

}


