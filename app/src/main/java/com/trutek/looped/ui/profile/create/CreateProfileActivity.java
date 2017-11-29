package com.trutek.looped.ui.profile.create;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.tasks.GetFilepathFromUriTask;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.data.contracts.services.ITagService;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.recipient.recipient.create.AddRecipientActivity;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateProfileActivity extends BaseAppCompatActivity implements CreateProfileStep1Fragment.OnFragmentInteractionListener,
        CreateProfileStep1GeneralFragment.OnFragmentInteractionListener, CreateProfileStep1AboutFragment.OnFragmentInteractionListener, OnImagePickedListener,View.OnClickListener {
    static final int CROP_PIC = 2;
    @Inject
    IProfileService _profileService;

    @Inject
    IInterestService _InterestService;
    @Inject
    ITagService _TagService;

  //  @BindView(R.id.htab_header)ImageView create_profile_image_header;
//    @BindView(R.id.htab_appbar) AppBarLayout app_bar;
   // @BindView(R.id.htab_collapse_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.submit_button) Button button_next;
   // @BindView(R.id.tv_terms_and_cond) TextView termsAndConditions;
  //  @BindView(R.id.progress_bar) ProgressBar progressBar;
    //@BindView(R.id.profile_frame_layout)FrameLayout frameLayout;

    private ProfileModel profile;
    private ViewPager pager;
    private EditText name, aboutUs;
    private MaskedImageView ivProfilePic;
    private String profilePicUri;
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    static Intent cropIntent;
    private Uri picUri;
    File myFile;



    public static void start(Activity activity) {
        Intent intent = new Intent(activity, CreateProfileActivity.class);
     //   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

 @Override
    protected int getContentResId() {
        return R.layout.activity_create_profile;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profile =_profileService.getMyProfile(null);
        init();
        setFonts();
        button_next.setOnClickListener(this);
       // initCollapseToolbar();
        addFragmentWithoutStackEntry(R.id.profile_frame_layout, CreateProfileStep1Fragment.newInstance(profile), "step1Fragment");
      /*  create_profile_image_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });*/
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_PROFILE_VIEW));

    }

    private void init() {
     //   profile = _profileService.getMyProfile(null);

        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initCollapseToolbar(){
      //  collapsingToolbarLayout.setTitleEnabled(false);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.create_profile_step_1);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {

                int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                int vibrantDarkColor = palette.getDarkVibrantColor(R.color.primary_700);
               // collapsingToolbarLayout.setContentScrimColor(vibrantColor);
               // collapsingToolbarLayout.setStatusBarScrimColor(vibrantDarkColor);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

   /* @OnClick(R.id.create_profile_button_next)
    public void next(){

        int currentItem = pager.getCurrentItem();
        switch (currentItem){
            case 0:
                app_bar.setExpanded(true);
                button_next.setText(getString(R.string.text_next));
                pager.setCurrentItem(1);
                break;

            case 1:
                app_bar.setExpanded(true);
                button_next.setText(getString(R.string.text_next));
                pager.setCurrentItem(2);
                break;

            case 2:
                app_bar.setExpanded(false);
                button_next.setText(getString(R.string.text_done));

                nextStep();
                break;
        }
    }
*/
    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        button_next.setTypeface(avenirNextRegular);
        //termsAndConditions.setTypeface(avenirNextRegular);
    }

    @Override
    public void onTabSelected(boolean expand, String buttonText){
     //   app_bar.setExpanded(expand);
        button_next.setText(buttonText);
    }

    @Override
    public void setPagerReference(ViewPager pager) {
        this.pager = pager;
    }

    @Override
    public void setNameTextReference(EditText name) {
        this.name = name;
    }

    @Override
    public void setProfilePicReference(MaskedImageView imageView)    {
        ivProfilePic = imageView;
    }

    @Override
    public void setAboutUsReference(EditText aboutUs) {
        this.aboutUs = aboutUs;
    }


    private void nextStep() {
     //   app_bar.setExpanded(true);

        if(name != null)
            profile.name = name.getText().toString();
        if(aboutUs != null)
            profile.about = aboutUs.getText().toString();

        if(!checkValidations()){
            return;
        }
        saveGeneralDataIntoPreference();

        if(isNetworkAvailable()){
          showProgress();
            if(profilePicUri == null){
                updateProfile();
            } else {
                startLoadAttachFile(new File(profilePicUri));
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }

    }

    private boolean checkValidations() {
        if(profile.getName() == null || profile.getName().isEmpty()){
            ToastUtils.longToast(getString(R.string.empty_name));
            return false;
        }
        /*if(profile.location == null){
            ToastUtils.longToast(getString(R.string.empty_location));
            return false;
        }*/
      /*  if(profile.interests == null || profile.interests.size() == 0){
            ToastUtils.longToast(getString(R.string.empty_string));
            return false;
        }
        if(profile.tags == null || profile.tags.size() == 0){
            ToastUtils.longToast(getString(R.string.empty_string));
            return false;
        }*/
        return true;
    }

    protected void startLoadAttachFile(final File file) {
        CloudinaryImageUploadCommand.start(this, file);
    }

    protected void addActions() {
        addAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_SUCCESS_ACTION, cloudinaryUploadSuccessAction);
        addAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    protected void removeActions() {
        removeAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_FAIL_ACTION);

        updateBroadcastActionList();
    }

    private void saveGeneralDataIntoPreference(){
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        helper.savePreference(PreferenceHelper.FULL_NAME, profile.name);
        helper.savePreference(PreferenceHelper.GENDER, profile.gender);
        helper.savePreference(PreferenceHelper.USER_PROFILE_ID, profile.id);

        if (profile.location != null){
            helper.savePreference(PreferenceHelper.LOCATION_NAME, profile.location.name);
            helper.savePreference(PreferenceHelper.LOCATION_DESCRIPTION, profile.location.description);
            if(profile.location.coordinates != null){
                helper.savePreference(PreferenceHelper.LOCATION_LONG, profile.location.coordinates.get(0));
                helper.savePreference(PreferenceHelper.LOCATION_LAT, profile.location.coordinates.get(1));
            }
        }

        helper.savePreference(PreferenceHelper.ABOUT_US, profile.about);

        if(profile.dateOfBirth != null)
            helper.savePreference(PreferenceHelper.B_DAY, DateHelper.stringify(profile.dateOfBirth));

    }

    private void updateProfile(){
        profile.status = ProfileModel.Status.Active.name().toLowerCase();

        _profileService.updateProfile(profile, new AsyncResult<ProfileModel>() {
            @Override
            public void success(ProfileModel profileModel) {

               /* _InterestService.saveInterests(profileModel.getInterests());
                _TagService.saveTag(profileModel.getTags());*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                     /*  DialogUtil.showAddRecipientDialog(CreateProfileActivity.this, null,
                                createRecipient,false);*/
                       /* DialogUtil.showDiscoverDialog(CreateProfileActivity.this, discoverDialogCallback);
                        setProfileStatusDone();*/
                        setProfileStatusDone();
                        Intent intent = new Intent(CreateProfileActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

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

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_PROFILE_VIEW));

        addActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeActions();
    }


    @Override
    public void showProgress() {
        super.showProgress();

       /* progressBar.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.INVISIBLE);*/

    }

    @Override
    public void hideProgress() {
        super.hideProgress();

        /*progressBar.setVisibility(View.INVISIBLE);
        frameLayout.setVisibility(View.VISIBLE);*/
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        EditText ed=(EditText)findViewById(R.id.general_edittext_name);
        ed.setCursorVisible(false);
        if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return true;

    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        Uri contentUri = Uri.fromFile(file);
        performCrop(contentUri);

    }
    private void performCrop(Uri contentUri) {
        try {
            cropIntent= new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(contentUri, "image/*");

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);

            setResult(RESULT_OK);

            startActivityForResult(cropIntent, CROP_PIC);

        }

        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void initMaskedView(Bitmap bitmap) {
        ivProfilePic.setImageBitmap(bitmap);
       // ivProfilePic.displayImageByUrl(bitmap);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == CROP_PIC)
        {
            Bitmap thePic;
            Bundle extras = data.getExtras();
            try {
                thePic = extras.getParcelable("data");
            }catch (Exception ex){
                String path=data.getDataString().replace("file://","");
                thePic = BitmapFactory.decodeFile(path);
            }
            Uri uri= getImageUri(this,thePic);
            GetFilepathFromUriTask obj =new GetFilepathFromUriTask();
            String path_image= getRealPathFromURI(uri);
            myFile= obj.getFileFromBitmap(path_image);
            profile.picUrl = myFile.getAbsolutePath();
            initMaskedView(thePic);
        }
    }
    private Uri getImageUri(CreateProfileActivity createProfileActivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(createProfileActivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    @Override
    public void onImagePickError(int requestCode, Exception e) {
        ErrorUtils.logError(e);
    }

    @Override
    public void onImagePickClosed(int requestCode) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.submit_button):
                nextStep();
                break;
        }
    }

    public class CloudinaryUploadSuccessAction implements Command {
        @Override
        public void execute(Bundle bundle) {
            CloudinaryModel model = (CloudinaryModel) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL);
            if(model != null){
                profile.picUrl = model.url;
            }
            PreferenceHelper.getPrefsHelper().savePreference(PreferenceHelper.USER_PIC_URL, profile.picUrl);
            updateProfile();
        }
    }

    AsyncResult<Boolean> createRecipient = new AsyncResult<Boolean>() {
        @Override
        public void success(Boolean isNew) {
            if(isNew){
                Intent intent=new Intent(CreateProfileActivity.this, AddRecipientActivity.class);
                intent.putExtra(Constants.MODEL_PROFILE, profile);
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                startActivity(intent);
                finish();
            } else {
                //TODO choose from existing recipient
            }
        }

        @Override
        public void error(String error) {
            DialogUtil.showDiscoverDialog(CreateProfileActivity.this,discoverDialogCallback);
        }
    };

   AsyncNotify discoverDialogCallback = new AsyncNotify() {
        @Override
        public void success() {
            Intent intent = new Intent(getApplication(), CreateProfileStep2Activity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
            finish();
        }

        @Override
        public void error(String error) {
            DialogUtil.showInviteDialog(CreateProfileActivity.this, inviteDialogCallback);

        }
    };


  /*  AsyncNotify discoverDialogCallback=new AsyncNotify() {
        @Override
        public void success() {
            CreateProfileStep2Activity.start(CreateProfileActivity.this);
            overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
            finish();
        }

        @Override
        public void error(String error) {
           DialogUtil.showInviteDialog(CreateProfileActivity.this,inviteDialogCallback);
        }
    };*/

    AsyncNotify inviteDialogCallback=new AsyncNotify() {
        @Override
        public void success() {
             Intent intent = new Intent(getApplication(), CreateProfileStep3Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                finish();
        }

        @Override
        public void error(String error) {
              HomeActivity.start(getApplication());
                finish();
        }
    };
}
