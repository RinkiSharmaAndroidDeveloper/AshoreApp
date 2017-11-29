package com.trutek.looped.ui.communityDashboard.myCommunities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.ui.communityDashboard.myCommunities.adapter.MyCommunityAdapter;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreatedCommunityFragment extends BaseV4Fragment implements View.OnClickListener {

    @Inject
    ICommunityService communityService;

    RecyclerView mRecycler;
    ProgressBar progressBar;
    LinearLayout linearLayout_info;

    TextView textView_info_first,textView_info_second;

    private OnFragmentInteractionListener mListener;

    private PageInput communityInput;
    private ArrayList<CommunityModel> communities;
    private MyCommunityAdapter mAdapter;
    private Tracker mTracker;
    Button button_addCommunity;
    public String ACTIVITY_CATEGORY = "1005";

    public CreatedCommunityFragment() {
        // Required empty public constructor
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            communities.clear();
            loadCommunities();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_JOINED_COMMUNITIES));
        //loadCommunities();
        initializeCommunities();
    }

    public static CreatedCommunityFragment newInstance() {
        CreatedCommunityFragment fragment = new CreatedCommunityFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this, view);
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        communityInput = new PageInput();
        communities = new ArrayList<>();

        mAdapter = new MyCommunityAdapter(communities,asyncResult_selectedCommunity);
    }



    private void initListeners() {
        mRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CommunityModel community = communities.get(position);
                        community.isSelected = true;

                        Intent intent = new Intent(getActivity(), DisplayCommunity.class);
                        intent.putExtra("communityModel", community);
                        intent.putExtra("OPEN_FROM", 3);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
        );

        button_addCommunity.setOnClickListener(this);

        linearLayout_info.setOnClickListener(this);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_created_community, container, false);
        activateButterKnife(view);

        initViews(view);

        initializeCommunities();

        initAdapters();

        initListeners();

        setFonts();

        setInfoText(getResources().getString(R.string.error_noJoined_community),getResources().getString(R.string.info_noJoined_community));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_JOINED_COMMUNITIES));
        App application = (App) getActivity().getApplicationContext();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.MYCOMMUNITY_CREATED_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isResumed()) {
            communityService.fetchJoinCommunities(new PageInput());
        }
    }

    private void setFonts() {
        button_addCommunity.setTypeface(avenirNextRegular);
        textView_info_first.setTypeface(avenirNextRegular);
        textView_info_second.setTypeface(avenirNextRegular);
    }

    private void initViews(View view) {
        button_addCommunity = (Button) view.findViewById(R.id.created_community_button_add);
        mRecycler = (RecyclerView) view.findViewById(R.id.layout_recycler_recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.layout_recycler_progressBar);
        linearLayout_info = (LinearLayout) view.findViewById(R.id.layout_recycler_linearLayout_info);
        textView_info_first = (TextView) view.findViewById(R.id.layout_recycler_textView_info_first);
        textView_info_second = (TextView) view.findViewById(R.id.layout_recycler_textView_info_second);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == button_addCommunity.getId()){
            Intent intent=new Intent(getContext().getApplicationContext(), SignupLocationCategoryActivity.class);
            PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
            helper.savePreference(PreferenceHelper.PARENT_ACTIVITY, ACTIVITY_CATEGORY);
            startActivity(intent);

        }else if(view.getId() == linearLayout_info.getId()){
            mListener.showDiscoverFragment();
        }
    }

    public interface OnFragmentInteractionListener {

        void onCreateCommunity();
        void showDiscoverFragment();
    }

    private void initializeCommunities() {
        communityInput.pageNo = 1;
        communities.clear();
        loadCommunities();
        communityService.fetchJoinCommunities(new PageInput());
    }

    private void initAdapters() {
        final LinearLayoutManager layoutManagerCommunities = new LinearLayoutManager(getActivity());

        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mRecycler.setAdapter(mAdapter);
    }

    private void loadCommunities() {

//        communityService.myCommunities(new AsyncResult<Page<CommunityModel>>() {
//            @Override
//            public void success(Page<CommunityModel> communitiesList) {
//
//                communities.addAll(communitiesList.items);

//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAdapter.setModified();
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
        showLoading();
        List<CommunityModel> communityModels = communityService.myCommunities();
        communities.clear();
        if(null != communityModels) {
            communities.addAll(communityModels);
        }
        mAdapter.notifyDataSetChanged();
        hideLoading();
    }

    AsyncResult<CommunityModel> asyncResult_selectedCommunity = new AsyncResult<CommunityModel>() {
        @Override
        public void success(CommunityModel communityModel) {

        }

        @Override
        public void error(String error) {

        }
    };


    void showLoading(){
        mRecycler.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        linearLayout_info.setVisibility(View.GONE);
    }

    void hideLoading(){
        progressBar.setVisibility(View.GONE);
        if(mAdapter.getItemCount()>0){
            mRecycler.setVisibility(View.VISIBLE);
        }else{
            linearLayout_info.setVisibility(View.VISIBLE);
        }
    }

    void setInfoText(String textInfoFirst, String textInfoSecond){
        textView_info_first.setText(textInfoFirst);
        textView_info_second.setText(textInfoSecond);
        textView_info_second.setTextColor(getResources().getColor(R.color.theme_light));
    }


}


