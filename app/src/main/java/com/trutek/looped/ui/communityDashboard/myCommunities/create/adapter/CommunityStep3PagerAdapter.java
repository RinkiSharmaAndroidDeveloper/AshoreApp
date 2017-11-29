package com.trutek.looped.ui.communityDashboard.myCommunities.create.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.trutek.looped.ui.communityDashboard.myCommunities.create.InviteFromContactFragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.InviteFromLoopFragment;

/**
 * Created by Windows 8 on 14-Sep-16.
 */
public class CommunityStep3PagerAdapter extends FragmentStatePagerAdapter {

    int numOfTabs;

    public CommunityStep3PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.numOfTabs=tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new InviteFromLoopFragment();
            case 1:
                return new InviteFromContactFragment();
            default:
                return new InviteFromLoopFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
