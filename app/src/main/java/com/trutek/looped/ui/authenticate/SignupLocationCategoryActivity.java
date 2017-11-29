package com.trutek.looped.ui.authenticate;

import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.ICategoryService;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.impl.entities.Category;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseV4Fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.authenticate.CategoryFragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.MyCommunitiesActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.CreateCommunityActivity;
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
public class SignupLocationCategoryActivity extends BaseAppCompatActivity implements View.OnClickListener
        , CategoryFragment.CategoryFragmentListener {

    private static final String TAG = SignupLocationCategoryActivity.class.getSimpleName();

    static final int REQUEST_INTEREST = 1;
    @Inject
    ICategoryService _categoryService;

    @Inject
    IProfileService _profileService;

    ImageView category_back;
    TextView textView, categoryNameTxView,categoryText;
    CategoryFragment categoryFragment;
    List<CategoryModel> categoryModels;
    ArrayList<String> selectedCategoriesId;
    String category_id, category_name;
    ProfileModel profile;
    public static String ACTIVITY_CATEGORY = "1002";
    PreferenceHelper helper;
    @Override
    protected int getContentResId() {
        return R.layout.activity_signup_category;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryModels = new ArrayList<>();
        helper = PreferenceHelper.getPrefsHelper();
        ACTIVITY_CATEGORY = helper.getPreference(PreferenceHelper.PARENT_ACTIVITY);
        initViews();
        intFragment();
        setFonts();
        category_back.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    private void setFonts() {
        textView.setTypeface(avenirNextRegular);
        categoryNameTxView.setTypeface(avenirNextRegular);
    }

    public void initViews() {

        category_back = (ImageView) findViewById(R.id.category_back);
        categoryText = (TextView) findViewById(R.id.textView_category);
        textView = (TextView) findViewById(R.id.category_next);
        categoryNameTxView = (TextView) findViewById(R.id.textView_categoryName);
        textView.setTextColor(getResources().getColor(R.color.dark_gray));
        profile = (ProfileModel) getIntent().getSerializableExtra(Constants.MODEL_PROFILE);

        if(null == profile){
            profile = new ProfileModel();
        }
        if(ACTIVITY_CATEGORY =="1005"){
            categoryText.setText("What is this community about?");
        }else{
            categoryText.setText("Which topics are you interested in?");
        }

        categoryModels.addAll(profile.getCategories());

        for (CategoryModel categoryModel: categoryModels) {
            categoryModel.setSelected(true);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    public void intFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        categoryFragment = CategoryFragment.newInstance(categoryModels);
        fragmentTransaction.replace(R.id.signup_category_fragment, categoryFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.category_back):
                Intent intentBack = new Intent();
                intentBack.putExtra(Constants.MODEL_PROFILE,profile);
                setResult(RESULT_OK,intentBack);
                finish();
                break;
            case (R.id.category_next):
                if (null == selectedCategoriesId || selectedCategoriesId.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one category before proceeding", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, SignupLocationInterestActivity.class);
                intent.putStringArrayListExtra("CommunityIds", selectedCategoriesId);
                intent.putExtra(Constants.MODEL_PROFILE,profile);
                update();
                startActivityForResult(intent,REQUEST_INTEREST);
                Log.d(TAG, selectedCategoriesId.toString());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_INTEREST) {
            profile = (ProfileModel) data.getExtras().getSerializable(Constants.MODEL_PROFILE);
            if (profile != null) {
                textView.setTextColor(getResources().getColor(R.color.color_white));
                textView.setOnClickListener(this);
                category_back.performClick();
            }
        }

    }

    @Override
    public void selectedCategory(CategoryModel categoryModel) {
        if (null == selectedCategoriesId) {
            selectedCategoriesId = new ArrayList<>();
        }


        selectedCategoriesId.add(categoryModel.getServerId());

        if (selectedCategoriesId.size() > 0) {
            textView.setTextColor(getResources().getColor(R.color.color_white));
        }


        if(null == profile.getCategories()){
            profile.setCategories(new ArrayList<CategoryModel>());
        }

        for (CategoryModel model:profile.getCategories()) {
            if(model.getServerId().equals(categoryModel.getServerId())){
                return;
            }
        }
        profile.getCategories().add(categoryModel);


    }

    @Override
    public void removeSelectedCategory(String categoryId) {
        if (selectedCategoriesId.contains(categoryId)) {
            selectedCategoriesId.remove(categoryId);
        }
        if (selectedCategoriesId.size() == 0) {
            textView.setTextColor(getResources().getColor(R.color.dark_gray));
        }

        for (CategoryModel categoryModel:profile.getCategories()) {
            if(categoryModel.getServerId().equals(categoryId)){
                profile.getCategories().remove(categoryModel);
                break;
            }
        }
    }

    private void update() {
        _profileService.updateInterest(profile, new AsyncResult<ProfileModel>() {
            @Override
            public void success(final ProfileModel profileModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileModel.setId(profile.getId());
                        _profileService.saveProfileToDatabase(profileModel);
                        hideProgress();
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
