package com.trutek.looped.ui.communityDashboard.maindashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.activity.display.DisplayActivity;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.communityDashboard.discoverCommunity.DiscoverCommunityActivity;
import com.trutek.looped.ui.communityDashboard.discoverPeople.DiscoverPeopleActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.MyCommunitiesActivity;
import com.trutek.looped.ui.communityDashboard.myConnections.MyConnectionActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityDashboardFragment extends BaseV4Fragment {
    @Inject
    IActivityService activityService;

    @BindView(R.id.layout_recent_activities) LinearLayout layoutRecentActivities;
    @BindView(R.id.community_dashboard_recycler_view) RecyclerView recyclerViewNotification;
    @BindView(R.id.community_text_discover_people) TextView text_discover_people;
    @BindView(R.id.community_text_discover_communities)TextView text_doscover_community;
    @BindView(R.id.community_text_my_connections)TextView text_my_connection;
    @BindView(R.id.community_text_my_communities)TextView text_my_communities;
    @BindView(R.id.community_text_resources)TextView text_resources;
    private Tracker mTracker;

    RecentActivityAdapter recentActivityAdapter;
    private ArrayList<ActivityModel> activities;

    private OnFragmentInteractionListener mListener;

    public CommunityDashboardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CommunityDashboardFragment newInstance() {
        CommunityDashboardFragment fragment = new CommunityDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_community_dashboard, container, false);
        activateButterKnife(view);

        initAdapter();
        setFonts();

        initListeners();
        getRecentActivities();
        App application = (App) getActivity().getApplicationContext();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.COMMUNITYDASHBOARD_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        return view;
    }

    private void initListeners() {
        recyclerViewNotification.addOnItemTouchListener(
                        new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        DisplayActivity.start(getActivity(), activities.get(position));
                    }
                })
        );
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity)getActivity()).setActionBarTitle(getString(R.string.drawer_text_community_dashboard));
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getActivity().getAssets(), com.trutek.looped.msas.common.Utils.Constants.AvenirNextRegular);
        text_discover_people.setTypeface(avenirNextRegular);
        text_doscover_community.setTypeface(avenirNextRegular);
        text_my_communities.setTypeface(avenirNextRegular);
        text_my_connection.setTypeface(avenirNextRegular);
        text_resources.setTypeface(avenirNextRegular);
    }


    private void initAdapter() {
        activities = new ArrayList<>();
        recentActivityAdapter =new RecentActivityAdapter(getActivity(), activities);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerViewNotification.setAdapter(recentActivityAdapter);
    }

    private void getRecentActivities() {

        activityService.upcomingActivities(new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(final Page<ActivityModel> activityModelPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activities.addAll(activityModelPage.items);
                        recentActivityAdapter.notifyDataSetChanged();
                        resolveLayoutRecentActivities(activities.size());
                    }
                });
            }

            @Override
            public void error(final String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    private void resolveLayoutRecentActivities(int size) {
        if(size > 0){
            layoutRecentActivities.setVisibility(View.VISIBLE);
        } else {
            layoutRecentActivities.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.community_text_discover_people)
    public void discoverPeople(){
        Intent intent=new Intent(getActivity().getApplicationContext(),DiscoverPeopleActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.community_text_discover_communities)
    public void discoverCommunities(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Go to the discoverCommunities")
                .setAction(Constants.DISCOVERCOMMUNITY_SCREEN)
                .build());
        Intent intent=new Intent(getActivity().getApplicationContext(),DiscoverCommunityActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.community_text_my_communities)
    public void myCommunities(){
        Intent intent=new Intent(getActivity().getApplicationContext(),MyCommunitiesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.community_text_my_connections)
    public void myConnections(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Go to the myConnections")
                .setAction(Constants.MYCONNECTION_SCREEN)
                .build());
        MyConnectionActivity.start(getActivity());
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
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
