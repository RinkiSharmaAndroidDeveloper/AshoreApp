package com.trutek.looped.ui.communityDashboard.myCommunities.create;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.adapter.CommunityStep3PagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityStep3Fragment extends BaseV4Fragment {

    @BindView(R.id.step_three_pager) ViewPager viewPager;
    @BindView(R.id.new_community_step_three_tab_layout) TabLayout tabLayout;

    private OnFragmentInteractionListener mListener;

    CommunityStep3PagerAdapter communityStep3PagerAdapter;
    private CommunityModel community;

    public CommunityStep3Fragment() {
        // Required empty public constructor
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    public static CommunityStep3Fragment newInstance(CommunityModel model) {
        CommunityStep3Fragment fragment = new CommunityStep3Fragment();
        Bundle args = new Bundle();
        args.putSerializable(CreateCommunityActivity.COMMUNITY_MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        community = (CommunityModel) getArguments().getSerializable(CreateCommunityActivity.COMMUNITY_MODEL);
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_step3, container, false);
        activateButterKnife(view);

        initTabs();
        initPager();
        return view;
    }

    private void initTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("From Loop"));
        tabLayout.addTab(tabLayout.newTab().setText("From Contact"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initPager() {
        communityStep3PagerAdapter = new CommunityStep3PagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(communityStep3PagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
    public void onResume() {
        super.onResume();
        initPager();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
