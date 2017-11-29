package com.trutek.looped.ui.authenticate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.CreateCommunityActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.edit.EditCommunity;
import com.trutek.looped.ui.communityDashboard.publiccommunity.PublicCommunityActivity;
import com.trutek.looped.ui.profile.create.CreateProfileActivity;
import com.trutek.looped.ui.profile.edit.EditProfileActivity;
import com.trutek.looped.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Rinki on 1/19/2017.
 */
public class SignupLocationInterestActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Inject
    IProfileService _ProfileService;

    ImageView interest_back;
    TextView textView, textView_interestCategoryName;


    InterestFragment interestFragment;

    CategoryModel categoryModel;
    ProfileModel profile;
    List<String> selectedCategoryIds;
    public static String ACTIVITY_CATEGORY = "1002";
    PreferenceHelper helper;


    @Override
    protected int getContentResId() {
        return R.layout.activity_signup_interest;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        categoryModel =(CategoryModel) getIntent().getSerializableExtra(Constants.MODEL_CATEGORY);
        selectedCategoryIds = getIntent().getStringArrayListExtra("CommunityIds");
        intFragment();
        setFonts();
        helper = PreferenceHelper.getPrefsHelper();
        ACTIVITY_CATEGORY = helper.getPreference(PreferenceHelper.PARENT_ACTIVITY);
        if (ACTIVITY_CATEGORY == null) {
            ACTIVITY_CATEGORY = "1002";
        }
        interest_back.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    public void initViews() {
        interest_back = (ImageView) findViewById(R.id.interest_back);
        textView = (TextView) findViewById(R.id.interest_done);
        textView_interestCategoryName = (TextView) findViewById(R.id.interest_textView_categoryName);
        profile = (ProfileModel) getIntent().getSerializableExtra(Constants.MODEL_PROFILE);
        if(null == profile){
            profile =new ProfileModel();
        }
    }

    public void intFragment() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        interestFragment = InterestFragment.newInstance(selectedCategoryIds,profile);
        fragmentTransaction.replace(R.id.signup_interest_fragment, interestFragment);
        fragmentTransaction.commit();
    }

    private void setFonts() {
        textView.setTypeface(avenirNextRegular);
        textView_interestCategoryName.setTypeface(avenirNextRegular);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.interest_back):
                Intent i = new Intent();
                setResult(Activity.RESULT_CANCELED, i);
                finish();
                break;
            case (R.id.interest_done):
                if(profile.interests.size()>0){
                    showProgress();
                    if (ACTIVITY_CATEGORY.contains("1001")) {
                        Intent j = new Intent(this, PublicCommunityActivity.class);
                        update();
                        helper.delete(PreferenceHelper.PARENT_ACTIVITY);
                        startActivity(j);
                    }/*else
                    if (ACTIVITY_CATEGORY.contains("1003")) {
                        Intent j = new Intent(this, CreateCommunityActivity.class);
                        update();
                        helper.delete(PreferenceHelper.PARENT_ACTIVITY);
                        startActivity(j);
                    }*/ else if (ACTIVITY_CATEGORY == "1003") {
                        Intent j = new Intent(this, EditProfileActivity.class);
                        update();
                        helper.delete(PreferenceHelper.PARENT_ACTIVITY);
                        startActivity(j);
                    }else if (ACTIVITY_CATEGORY == "1004") {
                        Intent j = new Intent(this, CreateProfileActivity.class);
                        update();
                        helper.delete(PreferenceHelper.PARENT_ACTIVITY);
                        startActivity(j);
                    }else if (ACTIVITY_CATEGORY == "1005") {
                        Intent j = new Intent(this, CreateCommunityActivity.class);
                        update();
                        helper.delete(PreferenceHelper.PARENT_ACTIVITY);
                        startActivity(j);
                    }
                    else if (ACTIVITY_CATEGORY == "1006") {
                        Intent j = new Intent(this, EditCommunity.class);
                        update();
                        helper.delete(PreferenceHelper.PARENT_ACTIVITY);
                        startActivity(j);
                    }else {
                        update();

                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please select at least one interest before proceeding",Toast.LENGTH_SHORT).show();
                }


              /*  if (ACTIVITY_CATEGORY.contains("1001")) {
                    Intent intent = new Intent(this, SignupLocationInterestActivity.class);
                    intent.putStringArrayListExtra("CommunityIds", selectedCategoriesId);
                    update();
                    helper.delete(PreferenceHelper.PARENT_ACTIVITY);
                    startActivity(intent);
                }*/

                break;
        }

    }

    private void update() {
        _ProfileService.updateInterest(profile, new AsyncResult<ProfileModel>() {
            @Override
            public void success(final ProfileModel profileModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileModel.setId(profile.getId());
                        _ProfileService.saveProfileToDatabase(profileModel);
                        hideProgress();
                        Intent intent = new Intent();
                        intent.putExtra(Constants.MODEL_PROFILE, profile);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });

    }

}
