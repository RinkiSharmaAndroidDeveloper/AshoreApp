package com.trutek.looped.ui.communityDashboard.myCommunities.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.trutek.looped.ui.communityDashboard.myCommunities.CreatedCommunityFragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.JoinedCommunityFragment;


public class MyCommunityPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public MyCommunityPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return CreatedCommunityFragment.newInstance();
            case 1:
                return JoinedCommunityFragment.newInstance();
            default:
                return CreatedCommunityFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}

