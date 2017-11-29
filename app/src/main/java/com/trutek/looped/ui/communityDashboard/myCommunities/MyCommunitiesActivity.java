package com.trutek.looped.ui.communityDashboard.myCommunities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.impl.services.NotificationService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.adapter.MyCommunityPagerAdapter;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.CreateCommunityActivity;
import com.trutek.looped.ui.home.NotificationActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MyCommunitiesActivity extends BaseAppCompatActivity implements CreatedCommunityFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.pager)ViewPager viewPager;
    @BindView(R.id.new_community_toolbar_plus_icon)ImageView imageView_plus_one;

    TextView textView_tab1, textView_tab2, textView_title;
    RelativeLayout relativeLayout_indicator1, relativeLayout_indicator2;
    int mTotalTabs = 2;
    public String ACTIVITY_CATEGORY = "1003";
    private Tracker mTracker;

    @Override
    protected int getContentResId() {
        return R.layout.activity_my_communities;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.COMMUNITYDASHBOARD_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getApplicationContext().startService(new Intent(getApplicationContext(),NotificationService.class));

        initTabs();
        clickListener();

        initPager();
        setTabs(0);
        setFonts();
    }

    private void initTabs() {
        textView_title = (TextView) findViewById(R.id.my_communities_textView_title);
        textView_tab1 = (TextView) findViewById(R.id.my_community_textView_tab1);
        textView_tab2 = (TextView) findViewById(R.id.my_community_textView_tab2);

        relativeLayout_indicator1 = (RelativeLayout) findViewById(R.id.my_community_relativeLayout_indicator1);
        relativeLayout_indicator2 = (RelativeLayout) findViewById(R.id.my_community_relativeLayout_indicator2);

    }

    void resetTabs(){
        textView_tab1.setTextColor(getResources().getColor(R.color.color_white));
        textView_tab2.setTextColor(getResources().getColor(R.color.color_white));
        textView_tab1.setBackgroundResource(R.color.dark_gray);
        textView_tab2.setBackgroundResource(R.color.dark_gray);
        relativeLayout_indicator1.setBackgroundResource(R.color.dark_gray);
        relativeLayout_indicator2.setBackgroundResource(R.color.dark_gray);
    }

    void setFonts(){
        textView_title.setTypeface(avenirNextRegular);
        textView_tab1.setTypeface(avenirNextRegular);
        textView_tab2.setTypeface(avenirNextRegular);
    }

    void setTabs(int position){
        if(position == -1){
            return;
        }

        resetTabs();

        switch (position){
            case 0: textView_tab1.setTextColor(Color.WHITE);
                relativeLayout_indicator1.setBackgroundResource(R.color.theme_dark);
                textView_tab1.setBackgroundResource(R.color.theme_dark);
                break;
            case 1: textView_tab2.setTextColor(Color.WHITE);
                relativeLayout_indicator2.setBackgroundResource(R.color.theme_dark);
                textView_tab2.setBackgroundResource(R.color.theme_dark);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void clickListener() {
        textView_tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        textView_tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        relativeLayout_indicator1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_tab1.performClick();
            }
        });

        relativeLayout_indicator2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_tab2.performClick();
            }
        });
    }

    private void initPager() {

        MyCommunityPagerAdapter myCommunityPagerAdapter = new MyCommunityPagerAdapter(getSupportFragmentManager(), mTotalTabs);
        viewPager.setAdapter(myCommunityPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//      viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
      /*tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        });*/
    }

    @OnClick(R.id.new_community_toolbar_plus_icon)
    public void plusClick(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("My Dashboard Notifications")
                .setAction("Notification")
                .build());
        Intent intent=new Intent(getApplicationContext(),NotificationActivity.class);
        startActivity(intent);

    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

//        initPager();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateCommunity() {
        Intent intent=new Intent(getApplicationContext(), CreateCommunityActivity.class);
        startActivity(intent);
    }

    @Override
    public void showDiscoverFragment() {
        viewPager.setCurrentItem(1,true);
    }
}
