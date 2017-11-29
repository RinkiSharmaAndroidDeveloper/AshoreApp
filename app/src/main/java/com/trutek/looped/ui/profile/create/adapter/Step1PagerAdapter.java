package com.trutek.looped.ui.profile.create.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.ui.profile.create.CreateProfileStep1AboutFragment;
import com.trutek.looped.ui.profile.create.CreateProfileStep1GeneralFragment;
import com.trutek.looped.ui.profile.create.CreateProfileStep1InterestFragment;

public class Step1PagerAdapter extends FragmentPagerAdapter {

    private ProfileModel profile;
    int mNumOfTabs;

    public Step1PagerAdapter(FragmentManager fm, int tabCount, ProfileModel profile) {
        super(fm);
        this.mNumOfTabs = tabCount;
        this.profile = profile;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return CreateProfileStep1GeneralFragment.newInstance(profile);
            case 1:
                return CreateProfileStep1AboutFragment.newInstance(profile);
            case 2:
                return CreateProfileStep1InterestFragment.newInstance(profile);
            default:
                return CreateProfileStep1GeneralFragment.newInstance(profile);
        }
    }
}
