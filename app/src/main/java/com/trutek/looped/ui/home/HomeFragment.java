package com.trutek.looped.ui.home;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.androidservices.NotificationBackGroundService;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.activity.display.DisplayActivity;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.communityDashboard.discoverCommunity.DiscoverCommunityAdapter;
import com.trutek.looped.ui.communityDashboard.discoverPeople.DiscoverPeopleAdapter;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.ui.communityDashboard.myConnections.InviteConnectionFromContacts;
import com.trutek.looped.ui.home.dashboardadapter.AgendaAdapter;
import com.trutek.looped.ui.home.dashboardadapter.RecentActivityAdapter;
import com.trutek.looped.utils.DialogUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class HomeFragment extends BaseV4Fragment implements View.OnClickListener {

    static final String TAG = HomeFragment.class.getSimpleName();

    static final int REQUEST_OPEN_COMMUNITY = 1;
    public static final int DISPLAY_ACTIVITY = 2;
    @Inject
    ICommunityService communityService;
    @Inject
    IActivityService _activityService;
    @Inject
    IProfileService _ProfileService;

    @Inject
    IConnectionService _ConnectionService;

    private ArrayList<ActivityModel> agendaList;
    private ArrayList<ActivityModel> activityModels;

    private ArrayList<CommunityModel> discoverCommunities;
    private ArrayList<ProfileModel> discoverPeoples;

    private AgendaAdapter agendaAdapter;
    private RecentActivityAdapter recentActivityAdapter;

    DiscoverPeopleAdapter discoverPeopleAdapter;
    DiscoverCommunityAdapter discoverCommunityAdapter;

    private OnActionListener<ActivityModel> openCommunityListener;
    private OnActionListener<ActivityModel> openActivityListener;

    private OnFragmentInteractionListener mListener;

    TextView textView_upcomingEvent_title, textView_recentActivity_title, textView_discoverPeople_title, textView_discoverCommunities_title;
    TextView textView_upcomingEvent, textView_recentActivity, textView_discoverPeople, textView_discoverCommunities, textView_invite_friends, textView_createNewCommunity;

    RecyclerView mRecyclerView_upcomintEvents, mRecyclerView_recentActivity, mRecyclerView_discoverPeople, mRecyclerView_discoverCommunities;

    LinearLayoutManager layoutManagerHorizontal, layoutManagerVertical;

    ProgressDialog progressDialog;

    int openedCommunityIndex = -1;
    String profileId;
    public String ACTIVITY_CATEGORY = "1005";

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            loadActivitiesFromLocal();
        }
    };

    BroadcastReceiver communityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            loadCommunitiesFromLocal();
//            loadActivitiesFromLocal();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
//        loadCommunitiesFromLocal();
        loadActivitiesFromLocal();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_ACTIVITIES));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(communityBroadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_COMMUNITIES));

    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        agendaList = new ArrayList<>();
        activityModels = new ArrayList<>();
        discoverCommunities = new ArrayList<>();
        discoverPeoples = new ArrayList<>();
        profileId = _ProfileService.getMyProfile(null).getServerId();

    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        activateButterKnife(view);
        initViews(view);
        initListeners();

        initUpcomingEventsAdapter();
        initRecentActivityAdapter();
        initDiscoverPeopleAdapter();
        initDiscoverCommunitiesAdapter();
//        initAdapters();
        setFonts();
        textView_invite_friends.setOnClickListener(this);
        textView_createNewCommunity.setOnClickListener(this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_ACTIVITIES));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(communityBroadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_COMMUNITIES));
        getActivity().startService(new Intent(getActivity(), NotificationBackGroundService.class));
        return view;
    }

    private void initUpcomingEventsAdapter() {
        agendaAdapter = new AgendaAdapter(agendaList);
        mRecyclerView_upcomintEvents.setLayoutManager(layoutManagerHorizontal);
        mRecyclerView_upcomintEvents.setAdapter(agendaAdapter);
        setEmptyText(mRecyclerView_upcomintEvents, textView_upcomingEvent, textView_upcomingEvent, agendaAdapter.getItemCount() != 0);
        agendaAdapter.SelectActivityOnClickImage(activitySelectedActionListeners);
        loadAgenda();
    }

    private void initRecentActivityAdapter() {
        recentActivityAdapter = new RecentActivityAdapter(activityModels, openCommunityListener, openActivityListener);
        mRecyclerView_recentActivity.setLayoutManager(layoutManagerVertical);
        mRecyclerView_recentActivity.setAdapter(recentActivityAdapter);
        setEmptyText(mRecyclerView_recentActivity, textView_recentActivity, textView_upcomingEvent, recentActivityAdapter.getItemCount() != 0);
        loadRecentActivity();
    }

    private void initDiscoverPeopleAdapter() {
        discoverPeopleAdapter = new DiscoverPeopleAdapter(discoverPeoples, actionListener_makeConnection, null, getActivity(), discoverPeoples);
        mRecyclerView_discoverPeople.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView_discoverPeople.setAdapter(discoverPeopleAdapter);
        setEmptyText(mRecyclerView_discoverPeople, textView_discoverPeople, textView_invite_friends, discoverPeopleAdapter.getItemCount() != 0);
        loadPeoplesToBeDiscovered();

    }

    private void initDiscoverCommunitiesAdapter() {
        discoverCommunityAdapter = new DiscoverCommunityAdapter(discoverCommunities, listener_openCommunity, asyncResult_communityJoining, getActivity(), discoverCommunities);
        mRecyclerView_discoverCommunities.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView_discoverCommunities.setAdapter(discoverCommunityAdapter);
        setEmptyText(mRecyclerView_discoverCommunities, textView_discoverCommunities, textView_createNewCommunity, discoverCommunityAdapter.getItemCount() != 0);
        loadCommunities();
    }

    private void initViews(View view) {
        textView_upcomingEvent_title = (TextView) view.findViewById(R.id.homeFragment_textView_upcomingEvent_title);
        textView_upcomingEvent = (TextView) view.findViewById(R.id.homeFragment_textView_upcomingEvent);

        textView_recentActivity_title = (TextView) view.findViewById(R.id.homeFragment_textView_recentActivity_title);
        textView_recentActivity = (TextView) view.findViewById(R.id.homeFragment_textView_recentActivity);

        textView_invite_friends = (TextView) view.findViewById(R.id.layout_recycler_textView_info_second);
        textView_createNewCommunity = (TextView) view.findViewById(R.id.layout_recycler_textView_info_second2);

        textView_discoverPeople_title = (TextView) view.findViewById(R.id.homeFragment_textView_discoverPeople_title);
        textView_discoverPeople = (TextView) view.findViewById(R.id.homeFragment_textView_discoverPeople);

        textView_discoverCommunities_title = (TextView) view.findViewById(R.id.homeFragment_textView_discoverCommunities_title);
        textView_discoverCommunities = (TextView) view.findViewById(R.id.homeFragment_textView_discoverCommunities);

        layoutManagerHorizontal = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerVertical = new LinearLayoutManager(getActivity());

        mRecyclerView_upcomintEvents = (RecyclerView) view.findViewById(R.id.homeFragment_recyclerView_upcomingEvent);
        mRecyclerView_recentActivity = (RecyclerView) view.findViewById(R.id.homeFragment_recyclerView_recentActivity);
        mRecyclerView_discoverPeople = (RecyclerView) view.findViewById(R.id.homeFragment_recyclerView_discoverPeople);
        mRecyclerView_discoverCommunities = (RecyclerView) view.findViewById(R.id.homeFragment_recyclerView_discoverCommunity);

    }

    void setEmptyText(RecyclerView recyclerView, TextView textView_noFound, TextView textView_inviteFriend, boolean anyRecordFound) {
        if (anyRecordFound) {
            recyclerView.setVisibility(View.VISIBLE);
            textView_noFound.setVisibility(View.INVISIBLE);
            textView_inviteFriend.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            textView_noFound.setVisibility(View.VISIBLE);
            textView_inviteFriend.setVisibility(View.VISIBLE);
        }
    }

    OnActionListener<ActivityModel> activitySelectedActionListeners = new OnActionListener<ActivityModel>() {
        @Override
        public void notify(ActivityModel activityModel) {
            Intent intent = new Intent(getContext().getApplicationContext(), DisplayActivity.class);
            intent.putExtra("activityModel", activityModel);
            startActivityForResult(intent, DISPLAY_ACTIVITY);
        }
    };

    OnActionListener<CommunityModel> asyncResult_communityJoining = new OnActionListener<CommunityModel>() {
        @Override
        public void notify(CommunityModel communityModel) {
            join(communityModel);
        }
    };

    public void join(CommunityModel community) {
        if (community.isSelected) {
            return;
        }
        community.profileIds.add(profileId);
        communityService.joinCommunity(community, new AsyncResult<CommunityModel>() {
            @Override
            public void success(CommunityModel communityModel) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (discoverCommunityAdapter.getClickedItemPosition() != -1) {
                            discoverCommunities.remove(discoverCommunityAdapter.getClickedItemPosition());
                            discoverCommunityAdapter.notifyDataSetChanged();
                            discoverCommunityAdapter.setClickedItemPosition(-1);
                        }
                    }
                });
            }

            @Override
            public void error(final String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((HomeActivity) getActivity()).setActionBarTitle(getString(R.string.drawer_text_home));
    }

    private void initAdapters() {
        agendaAdapter = new AgendaAdapter(agendaList);

       /* recyclerViewAgenda.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewCommunities.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewAgenda.setAdapter(agendaAdapter);
        recyclerViewCommunities.setAdapter(recentActivityAdapter);*/

        loadActivitiesFromLocal();
        loadCommunities();
    }

    private void loadAgenda() {
        _activityService.upcomingActivities(new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(final Page<ActivityModel> activityModelPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        agendaList.clear();
                        agendaList.addAll(activityModelPage.items);
                        agendaAdapter.notifyDataSetChanged();
                        setEmptyText(mRecyclerView_upcomintEvents, textView_upcomingEvent, textView_upcomingEvent, agendaAdapter.getItemCount() != 0);
                    }
                });


            }

            @Override
            public void error(String error) {

            }
        });
//        _activityService.fetchMyActivities();
    }

    private void loadActivitiesFromLocal() {
       /* agendaList.clear();
        agendaList.addAll(_activityService.getAgenda());
        agendaAdapter.notifyDataSetChanged();*/
//        Log.d(TAG, "ActivitiesSize: " + recyclerViewAgenda.getAdapter().getItemCount());

//        makeViewsVisible();
    }

    private void loadCommunities() {
//        communityService.fetchMyAllCommunities(_ProfileService.getMyProfile(null));
        communityService.discoverCommunities(new PageInput(), new AsyncResult<Page<CommunityModel>>() {
            @Override
            public void success(final Page<CommunityModel> communityModelPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discoverCommunities.clear();
                        discoverCommunities.addAll(communityModelPage.items);
                        discoverCommunityAdapter.notifyDataSetChanged();
                        setEmptyText(mRecyclerView_discoverCommunities, textView_discoverCommunities, textView_createNewCommunity, discoverCommunityAdapter.getItemCount() != 0);
                    }
                });
            }

            @Override
            public void error(String error) {

            }
        });
    }

    void loadPeoplesToBeDiscovered() {
        _ProfileService.discoverProfiles(new PageInput(), new AsyncResult<Page<ProfileModel>>() {
            @Override
            public void success(final Page<ProfileModel> profileModelPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discoverPeoples.clear();
                        discoverPeoples.addAll(profileModelPage.items);
                        discoverPeopleAdapter.notifyDataSetChanged();
                        setEmptyText(mRecyclerView_discoverPeople, textView_discoverPeople, textView_invite_friends, discoverPeopleAdapter.getItemCount() != 0);
                    }
                });
            }

            @Override
            public void error(String error) {

            }
        });
    }


    private void loadRecentActivity() {
        activityModels.clear();
        _activityService.recentActivities(new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(final Page<ActivityModel> activityModelPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityModels.addAll(activityModelPage.items);
                        recentActivityAdapter.notifyDataSetChanged();
                        setEmptyText(mRecyclerView_recentActivity, textView_recentActivity, textView_recentActivity, recentActivityAdapter.getItemCount() != 0);

                    }
                });
            }

            @Override
            public void error(String error) {

            }
        });

//        Log.d(TAG, "CommunitySize: " + recyclerViewCommunities.getAdapter().getItemCount());

        makeViewsVisible();
    }

    private void makeViewsVisible() {

        /*if (recyclerViewAgenda.getAdapter().getItemCount() > 0 && recyclerViewCommunities.getAdapter().getItemCount() > 0) {
            linearLayoutAgenda.setVisibility(View.VISIBLE);
            linearLayoutCommunities.setVisibility(View.VISIBLE);
            txt_any_scheduled_text3.setVisibility(View.GONE);

        } else if (recyclerViewAgenda.getAdapter().getItemCount() > 0) {
            linearLayoutAgenda.setVisibility(View.VISIBLE);
            linearLayoutCommunities.setVisibility(View.INVISIBLE);
            txt_any_scheduled_text3.setVisibility(View.INVISIBLE);

        } else if (recyclerViewCommunities.getAdapter().getItemCount() > 0) {
            linearLayoutCommunities.setVisibility(View.VISIBLE);
            linearLayoutAgenda.setVisibility(View.GONE);
            txt_any_scheduled_text3.setVisibility(View.GONE);
        } else {
            txt_any_scheduled_text3.setVisibility(View.VISIBLE);
            linearLayoutAgenda.setVisibility(View.INVISIBLE);
            linearLayoutCommunities.setVisibility(View.INVISIBLE);
        }*/
    }

    private void setFonts() {
        textView_upcomingEvent_title.setTypeface(avenirNextRegular);
        textView_upcomingEvent.setTypeface(avenirNextRegular);

        textView_recentActivity_title.setTypeface(avenirNextRegular);
        textView_recentActivity.setTypeface(avenirNextRegular);

        textView_discoverPeople_title.setTypeface(avenirNextRegular);
        textView_discoverPeople.setTypeface(avenirNextRegular);

        textView_discoverCommunities_title.setTypeface(avenirNextRegular);
        textView_discoverCommunities.setTypeface(avenirNextRegular);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(communityBroadcastReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.layout_recycler_textView_info_second):
                Intent i = new Intent(getActivity().getApplication(), InviteConnectionFromContacts.class);
                startActivity(i);
                break;
            case (R.id.layout_recycler_textView_info_second2):
                Intent intent = new Intent(getContext().getApplicationContext(), SignupLocationCategoryActivity.class);
                PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
                helper.savePreference(PreferenceHelper.PARENT_ACTIVITY, ACTIVITY_CATEGORY);
                startActivity(intent);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initListeners() {

       /* recyclerViewAgenda.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        ActivityModel activity = agendaList.get(position);
                        DisplayActivity.start(getActivity(), activity);
                    }
                })
        );*/

        openCommunityListener = new OnActionListener<ActivityModel>() {
            @Override
            public void notify(ActivityModel activityModel) {

//                community.isSelected = true;
//
               /* Intent intent = new Intent(getActivity(), DisplayCommunity.class);
                intent.putExtra("communityModel", community);
                intent.putExtra("OPEN_FROM", 2);
                startActivity(intent);*/
            }
        };

        openActivityListener = new OnActionListener<ActivityModel>() {
            @Override
            public void notify(ActivityModel activity) {

                DisplayActivity.start(getActivity(), activity);
            }
        };


    }

    OnActionListener<ProfileModel> actionListener_makeConnection = new OnActionListener<ProfileModel>() {
        @Override
        public void notify(ProfileModel profileModel) {
            DialogUtil.showProgress(null, getActivity());
            ConnectionModel model = new ConnectionModel();
            model.setProfile(profileModel);
            _ConnectionService.linkConnection(model, new AsyncResult<ConnectionModel>() {
                @Override
                public void success(ConnectionModel connectionModel) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtil.hideProgress();
                        }
                    });
                }

                @Override
                public void error(String error) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtil.hideProgress();
                        }
                    });
                }
            });
        }
    };

    OnActionListener<CommunityModel> listener_openCommunity = new OnActionListener<CommunityModel>() {
        @Override
        public void notify(CommunityModel communityModel) {
            openedCommunityIndex = discoverCommunities.indexOf(communityModel);
            Intent intent = new Intent(getActivity(), DisplayCommunity.class);
            intent.putExtra("communityModel", communityModel);
            intent.putExtra("OPEN_FROM", 2);
            startActivityForResult(intent, REQUEST_OPEN_COMMUNITY);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_OPEN_COMMUNITY && resultCode == getActivity().RESULT_OK) {
            if (openedCommunityIndex != -1) {
                discoverCommunities.remove(openedCommunityIndex);
                discoverCommunityAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == DISPLAY_ACTIVITY && resultCode == getActivity().RESULT_OK) {
        }
    }
}
