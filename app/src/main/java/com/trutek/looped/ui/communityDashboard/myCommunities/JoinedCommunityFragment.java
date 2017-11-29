package com.trutek.looped.ui.communityDashboard.myCommunities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.adapter.CommunityAdapter;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JoinedCommunityFragment extends BaseV4Fragment {

    @Inject
    ICommunityService communityService;
    @Inject
    IProfileService _ProfileService;

    @BindView(R.id.joined_community_recycler_view) RecyclerView mRecycler;

    private ArrayList<CommunityModel> communities;
    private CommunityAdapter communityAdapter;
    private OnFragmentInteractionListener mListener;
    private Tracker mTracker;
    Boolean isJoinButtonActive =false;
    ProgressBar progressBar;
    ProfileModel profileModel;
    CommunityModel communityModel;
    static JoinedCommunityFragment fragment;
    public JoinedCommunityFragment() {
    }


    public static JoinedCommunityFragment newInstance() {
        fragment = new JoinedCommunityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileModel = _ProfileService.getMyProfile(null);
        communities = new ArrayList<>();
        communityAdapter = new CommunityAdapter(getActivity(), communities,joinClickActionListeners,isJoinButtonActive,openCommunityClickActionListeners);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_joined, container, false);
        activateButterKnife(view);

        progressBar = (ProgressBar) view.findViewById(R.id.joined_community_progressBar);

        setAdapter();

        initListeners();
        App application = (App) getActivity().getApplicationContext();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.MYCOMMUNITY_JOINED_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            communityModel =new CommunityModel();
            showProgress();
            communityModel = (CommunityModel) data.getSerializableExtra("communityModel");
            join(communityModel);
        }
    }
    private void initListeners() {
        mRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        /*CommunityModel community = communities.get(position);
                        community.isSelected = true;

                        Intent intent = new Intent(getActivity(), DisplayCommunity.class);
                        intent.putExtra("communityModel", community);
                        intent.putExtra("OPEN_FROM", 4);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/
                    }
                })
        );
    }
    OnActionListener<CommunityModel> joinClickActionListeners = new OnActionListener<CommunityModel>() {
        @Override
        public void notify(CommunityModel communityModel) {
            showProgress();
            join(communityModel);
        }
    };
    OnActionListener<CommunityModel> openCommunityClickActionListeners = new OnActionListener<CommunityModel>() {
        @Override
        public void notify(CommunityModel communityModel) {
          //  JoinedCommunityFragment joinFragment = new JoinedCommunityFragment();
            Intent intent = new Intent(getActivity(), DisplayCommunity.class);
            intent.putExtra("communityModel", communityModel);
            intent.putExtra("OPEN_FROM", 5);
            fragment.startActivityForResult(intent, 1);
        }
    };

    public void join(CommunityModel community){
        if(community.isSelected){
            return;
        }
        community.profileIds.add(profileModel.getServerId());
        communityService.joinCommunity(community, new AsyncResult<CommunityModel>() {
            @Override
            public void success(CommunityModel communityModel) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(communityAdapter.getClickedItemPosition() != -1) {
                            communities.remove(communityAdapter.getClickedItemPosition());
                            communityAdapter.notifyDataSetChanged();
                            communityAdapter.setClickedItemPosition(-1);
                        }
                        hideProgress();
                    }
                });
            }
            @Override
            public void error(final String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),error,Toast.LENGTH_SHORT).show();
                        hideProgress();
                    }
                });
            }
        });
    }

    private void setAdapter() {
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(communityAdapter);
        loadCommunities();
    }

    private void loadCommunities() {
        showProgress();
        communityService.discoverCommunities(new PageInput(), new AsyncResult<Page<CommunityModel>>() {
            @Override
            public void success(final Page<CommunityModel> communityModelPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        communities.clear();
                        communities.addAll(communityModelPage.items);
                        if(null != communityAdapter){
                            communityAdapter.notifyDataSetChanged();
                            hideProgress();
                        }
                    }
                });

            }

            @Override
            public void error(String error) {

            }
        });


//        communityService.joinedCommunities(new AsyncResult<Page<CommunityModel>>() {
//            @Override
//            public void success(Page<CommunityModel> communitiesList) {
//
//                communities.addAll(communitiesList.items);
//
////                Collections.sort(tagList, new Comparator<InterestModel>() {
////                    @Override
////                    public int compare(InterestModel lhs, InterestModel rhs) {
////                        if (lhs.isSelected && !rhs.isSelected)
////                            return -1;
////
////                        if (lhs.isSelected && rhs.isSelected)
////                            return 0;
////
////                        if (!lhs.isSelected && rhs.isSelected)
////                            return 1;
////
////                        return 0;
////                    }
////                });
//
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        communityAdapter.setModified();
//                    }
//                });
//            }
//
//            @Override
//            public void error(final String error) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtils.longToast(error);
//                    }
//                });
//            }
//        });

      /*  List<CommunityModel> joinedCommunities =communityService.joinedCommunities();
        communities.addAll(joinedCommunities);
        communityAdapter.notifyDataSetChanged();*/



    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
    }

    void hideProgress(){
        mRecycler.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
