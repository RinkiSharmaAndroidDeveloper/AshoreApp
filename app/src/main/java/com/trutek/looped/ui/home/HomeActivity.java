package com.trutek.looped.ui.home;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.App;
import com.trutek.looped.chatmodule.ui.base.BaseChatActivity;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.data.contracts.services.IUserService;
import com.trutek.looped.data.impl.services.NotificationService;
import com.trutek.looped.gcm.GCMHelper;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.R;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.activity.create.CommunityListActivity;
import com.trutek.looped.ui.authenticate.SignUpActivity;
import com.trutek.looped.ui.authenticate.SignupLocation;
import com.trutek.looped.ui.chats.DialogsFragment;
import com.trutek.looped.ui.communityDashboard.maindashboard.CommunityDashboardFragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.MyCommunitiesActivity;
import com.trutek.looped.ui.communityDashboard.myConnections.MyConnectionActivity;
import com.trutek.looped.ui.planner.PlannerFragment;
import com.trutek.looped.ui.profile.edit.EditProfileActivity;
import com.trutek.looped.ui.recipient.RecipientDashBoardFragment;
import com.trutek.looped.ui.settings.SettingsFragment;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.alarm.AlarmHelper;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseChatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
        ,HomeFragment.OnFragmentInteractionListener, DialogsFragment.OnFragmentInteractionListener, CommunityDashboardFragment.OnFragmentInteractionListener
       ,PlannerFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener, RecipientDashBoardFragment.OnFragmentInteractionListener{

    public static final String TAG = HomeActivity.class.getSimpleName();
    private static final int NOTIFICATION_ACTIVITY = 1;

    @Inject
    IReportBugService reportBugService;
    @Inject
    IUserService userService;

    @Inject
    IProfileService _ProfileService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;
       MenuItem previous_item;
    /*NavigationHeaderBinding*/
    MaskedImageView nav_header_image_prfile_pic;
    TextView nav_header_text_user_name;
    TextView nav_header_text_edit_profile;
    private GCMHelper gcmHelper;
    private Tracker mTracker;


    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return (R.layout.activity_home);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.plus_icon));
        //getApplicationContext().startService(new Intent(getApplicationContext(),NotificationService.class));
        navHeaderView();
        setDefaultFragment();
        toggleAction();
        navigationViewListener();

        gcmHelper = new GCMHelper(this);
        getApplicationContext().startService(new Intent(getApplicationContext(),NotificationService.class));

        //setup alarm

        AlarmHelper helper = new AlarmHelper(this);
        helper.setAlarm();
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.HOME_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

//        navigationView.setCheckedItem(R.id.nav_home);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.getIntExtra("OPEN_FROM", 0) == NOTIFICATION_ACTIVITY){
            loadDialogsFragment();
        }
        super.onNewIntent(intent);
    }

    private void loadDialogsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new DialogsFragment());
        fragmentTransaction.commit();
    }

    private void setDefaultFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
        fragmentTransaction.commit();
    }

    private void navHeaderView() {
        View headerview = navigationView.getHeaderView(0);
        nav_header_image_prfile_pic = (MaskedImageView) headerview.findViewById(R.id.imageView_drawer);
        nav_header_text_user_name=(TextView)headerview.findViewById(R.id.nav_header_user_name);
        nav_header_text_edit_profile=(TextView)headerview.findViewById(R.id.nav_header_text_edit_my_profile);
        setHeaderFonts();
        navHeaderlisteners();
        setheaderData();
    }

    private void setheaderData() {
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        String picUrl = helper.getPreference(PreferenceHelper.USER_PIC_URL, "");
        String name = helper.getPreference(PreferenceHelper.FULL_NAME, "");

        if(picUrl != null && !picUrl.isEmpty() && picUrl.contains("http")){
            displayImageByUrl(picUrl, nav_header_image_prfile_pic);
        } else {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.default_camera);
            nav_header_image_prfile_pic.setImageBitmap(image);
        }

        nav_header_text_user_name.setText(name);
    }

    private void displayImageByUrl(String publicUrl, MaskedImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void setHeaderFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(),Constants.AvenirNextRegular);
        Typeface avenirNextBold=Typeface.createFromAsset(getAssets(),Constants.AvenirNextBold);
        nav_header_text_user_name.setTypeface(avenirNextBold);
        nav_header_text_edit_profile.setTypeface(avenirNextRegular);
    }

    private void navHeaderlisteners() {
        nav_header_text_edit_profile.setOnClickListener(this);
        nav_header_image_prfile_pic.setOnClickListener(this);
        nav_header_text_user_name.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_header_text_edit_my_profile:
            case R.id.nav_header_user_name:
            case R.id.imageView_drawer:
                openEditProfile();
        }
    }

    private void openEditProfile() {
        Intent intent=new Intent(getApplicationContext(),EditProfileActivity.class);
        startActivity(intent);
    }

    private void navigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void toggleAction() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @OnClick(R.id.fab)
    public void floatingActionListener() {
        Intent intent = new Intent(getApplication(), CommunityListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }

    private void floatingPopUp() {
        final Dialog floatingDialog = new Dialog(this);
        floatingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        floatingDialog.setCanceledOnTouchOutside(false);
        floatingDialog.setContentView(R.layout.floating_pop_up);

        final TextView text_add_community,text_add_event
                        ,text_add_medication,text_add_note
                         ,text_add_status_update,text_add_expense;
//        text_add_community = (TextView) floatingDialog.findViewById(R.id.text_add_community);
        text_add_event=(TextView)floatingDialog.findViewById(R.id.text_add_event);
        text_add_medication=(TextView)floatingDialog.findViewById(R.id.text_add_medication);
        text_add_note=(TextView)floatingDialog.findViewById(R.id.text_add_note);
        text_add_status_update=(TextView)floatingDialog.findViewById(R.id.text_Add_Status_update);
        text_add_expense=(TextView)floatingDialog.findViewById(R.id.text_add_expense);

        Typeface avenirNextRegular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
//        text_add_community.setTypeface(avenirNextRegular);
        text_add_event.setTypeface(avenirNextRegular);
        text_add_medication.setTypeface(avenirNextRegular);
        text_add_note.setTypeface(avenirNextRegular);
        text_add_status_update.setTypeface(avenirNextRegular);
        text_add_expense.setTypeface(avenirNextRegular);
        floatingDialog.show();
//        text_add_community.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                floatingDialog.dismiss();
//                Intent intent = new Intent(getApplication(), CreateCommunityActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
////                Toast.makeText(getApplicationContext(),"Coming soon..",Toast.LENGTH_LONG).show();
//            }
//        });
        text_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingDialog.dismiss();
                Intent intent = new Intent(getApplication(), CommunityListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
            }
        });
        text_add_medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Coming soon..",Toast.LENGTH_LONG).show();
            }
        });
        text_add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Coming soon..",Toast.LENGTH_LONG).show();
            }
        });
        text_add_status_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Coming soon..",Toast.LENGTH_LONG).show();
            }
        });
        text_add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Coming soon..",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void checkGCMRegistration() {

        if (gcmHelper.checkPlayServices()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                return;
            }
            gcmHelper.registerInBackground();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gcmHelper.registerInBackground();
        } else {
            ToastUtils.longToast("Required permissions are not granted");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.notification) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("My Dashboard Notifications")
                    .setAction("Notification")
                    .build());
            Intent intent=new Intent(getApplicationContext(),NotificationActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(Constants.HOME_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (!isChatInitializedAndUserLoggedIn()) {
            loginChat();
        }
        setheaderData();
       // checkGCMRegistration();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        selectDrawerItem(item);
        return true;
    }

    private void selectDrawerItem(MenuItem item) {
//        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.drawer_text_tint)));
//        SpannableString spanString = new SpannableString(item.getTitle().toString());
//        spanString.setSpan(new ForegroundColorSpan(Color.CYAN), 0, spanString.length(), 0);
//        item.setTitle(spanString);

//        item.setChecked(true);
      /*  if (previous_item!=null){
            previous_item.setChecked(false);
        }*/
        Fragment fragment = null;

        switch(item.getItemId()) {
            case R.id.nav_home:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("My Dashboard")
                        .setAction("Home")
                        .build());
                fragment = HomeFragment.newInstance();
                floatingActionButton.setVisibility(View.GONE);
                item.setChecked(true);
                if (previous_item!=null){
                    previous_item.setChecked(false);
                }
                previous_item = item;
                break;
            case R.id.nav_planner:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Go to planner")
                        .setAction("Calendar")
                        .build());
                fragment = PlannerFragment.newInstance();
                floatingActionButton.setVisibility(View.VISIBLE);
                item.setChecked(true);
                if (previous_item!=null){
                    previous_item.setChecked(false);
                }
                previous_item = item;
                break;
            case R.id.nav_community:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Go to community")
                        .setAction("Community")
                        .build());

                Intent intentCommunity = new Intent(HomeActivity.this, MyCommunitiesActivity.class);
                startActivity(intentCommunity);
//                fragment = CommunityDashboardFragment.newInstance();
                fragment = null;
                item.setChecked(true);
                if (previous_item!=null){
                    previous_item.setChecked(false);
                }
                previous_item = item;
                floatingActionButton.setVisibility(View.GONE);
                break;
            case R.id.nav_connection:
//                fragment= RecipientDashBoardFragment.newInstance("","");
                Intent intent = new Intent(HomeActivity.this, MyConnectionActivity.class);
                startActivity(intent);
                fragment = null;
                item.setChecked(true);
                if (previous_item!=null){
                    previous_item.setChecked(false);
                }
                previous_item = item;
                floatingActionButton.setVisibility(View.GONE);
                break;
            case R.id.nav_chats:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Go to chat")
                        .setAction("Recent Chats")
                        .build());
                fragment = DialogsFragment.newInstance();
                floatingActionButton.setVisibility(View.GONE);
                item.setChecked(true);
                if (previous_item!=null){
                    previous_item.setChecked(false);
                }
                previous_item = item;
                break;
            case R.id.nav_settings:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Go to settings")
                        .setAction("Settings")
                        .build());
                fragment = SettingsFragment.newInstance();
                floatingActionButton.setVisibility(View.GONE);
                item.setChecked(true);
                if (previous_item!=null){
                    previous_item.setChecked(false);
                }
                previous_item = item;
                break;
            case R.id.nav_log_out:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Logout from the app")
                        .setAction("Logout")
                        .build());
                logOut();
                item.setChecked(true);
                if (previous_item!=null){
                    previous_item.setChecked(false);
                }
                previous_item = item;
                break;
            default:
                fragment = HomeFragment.newInstance();
        }

//        previous_item = item;

        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
            item.setChecked(true);
        }

        drawer.closeDrawers();
    }

    private void logOut() {
        AppSession.getSession().closeAndClear();
        clearDatabaseTabels();
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        helper.delete(PreferenceHelper.SESSION_TOKEN);
        _ProfileService.clearMyProfile();
        SignupLocation.start(this);
        finish();
    }

    private void clearDatabaseTabels() {
        userService.clearDatabase();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
