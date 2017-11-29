package com.trutek.looped.ui.profile.create;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.ui.profile.create.adapter.Step1PagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CreateProfileStep1Fragment extends Fragment {

    @BindView(R.id.htab_tabs) TabLayout tabs;
    @BindView(R.id.htab_viewpager) ViewPager pager;

    ProfileModel profile;
    private OnFragmentInteractionListener mListener;

    public CreateProfileStep1Fragment() {
        // Required empty public constructor
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    public static CreateProfileStep1Fragment newInstance(ProfileModel profile) {
        CreateProfileStep1Fragment fragment = new CreateProfileStep1Fragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profile = (ProfileModel) getArguments().getSerializable("profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_profile_step1, container, false);
        activateButterKnife(view);
        mListener.setPagerReference(pager);

       initTabs();
      initPager();

        return view;
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
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onTabSelected(boolean expand, String buttonText);

        void setPagerReference(ViewPager pager);
    }

    private void initTabs() {
        tabs.addTab(tabs.newTab().setText(" "));
      /*  tabs.addTab(tabs.newTab().setText("ABOUT"));
        tabs.addTab(tabs.newTab().setText("INTERESTS"));*/

       // tabs.setTabGravity(TabLayout.);
    }

    private void initPager() {
        Step1PagerAdapter adapter = new Step1PagerAdapter(getFragmentManager() , tabs.getTabCount(), profile);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(0);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());

                int currentItem = pager.getCurrentItem();
                switch (currentItem){
                    case 0:
                        mListener.onTabSelected(false, getString(R.string.text_done));
                        break;

                   /* case 1:
                        mListener.onTabSelected(true, getString(R.string.text_next));
                        break;

                    case 2:
                        mListener.onTabSelected(false, getString(R.string.text_done));
                        break;*/
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
