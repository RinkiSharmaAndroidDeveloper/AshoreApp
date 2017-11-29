package com.trutek.looped.ui.profile.edit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.tasks.GetFilepathFromUriTask;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.impl.services.NotificationService;
import com.trutek.looped.geoCode.GeoCodeIntentService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.Utils.ageCalculator.Age;
import com.trutek.looped.msas.common.Utils.ageCalculator.AgeCalculator;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.ui.dialogs.PreviewImageFragment;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.ui.profile.InterestTagsActivity;
import com.trutek.looped.ui.profile.create.adapter.InterestAdapter;
import com.trutek.looped.ui.authenticate.BaseAuthActivity;
import com.trutek.looped.ui.profile.create.adapter.TagAdapter;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class EditProfileActivity extends BaseAuthActivity implements OnImagePickedListener, PreviewImageFragment.OnFragmentInteractionListener, View.OnClickListener {

    private static final int SECOND_ACTIVITY_RESULT_CODE = 1;
    private static final int EDIT_CODE = 0;
    private final static String PREVIEW_IMAGE_FRAGMENT = "PreviewImageFragment";
    public static String ACTIVITY_EDIT_PROFILE = "1003";

    static final int REQUEST_CATEGORY = 2;
    static final int CROP_PIC = 3;

    @Inject
    IProfileService _profileService;

    private AddressResultReceiver mResultReceiver;

    /*Toolbar*/
    //  @BindView(R.id.toolbar) Toolbar toolbar;
   /* @BindView(R.id.image_done) ImageView text_save;*/
    @BindView(R.id.edit_txt_header)
    TextView header;

    /*About Me*/
    @BindView(R.id.txt_about_me)
    EditText aboutMe;
    //   @BindView(R.id.title_about_me) TextView text_title_about_me;
    @BindView(R.id.image_edit)
    ImageView image_edit;

    /*Detail*/
    //  @BindView(R.id.title_details) TextView text_title_detail;
    @BindView(R.id.txt_date_of_birth)
    TextView birthday;
    @BindView(R.id.txt_gender)
    TextView gender;
    @BindView(R.id.txt_location)
    TextView location;

    //  @BindView(R.id.edit_profile_editable_layout) LinearLayout layout_editable;
    @BindView(R.id.image_view_profile_blur_editProfile)
    ImageView image_icon_blur;
    @BindView(R.id.image_view_profile_editProfile)
    MaskedImageView masked_image_icon;
    LinearLayout linearLayoutInterest;

    /*Recipients*/
/*    @BindView(R.id.edit_profile_title_recipients) TextView text_title_recipient;
    @BindView(R.id.recipient_image_plus_icon) ImageView imageView_recipients_plus_icon;
    @BindView(R.id.text_add_recipient_info) TextView text_add_recipient_here;
    @BindView(R.id.layout_recipient_plus_icon) LinearLayout layout_recipent_plus_icon;*/

    /*InterestedIn*/
   /* @BindView(R.id.edit_profile_title_interested_in)
    TextView text_title_interested_in;
    @BindView(R.id.interested_in_image_plus_icon)
    ImageView imageView_interested_plus_icon;
    @BindView(R.id.edit_profile_recycler_view_topics)
    RecyclerView recyclerViewTopics;
    @BindView(R.id.layout_interested_in_plus_icon)
    LinearLayout layout_interested_in_plus_icon;*/


    /*MyTopics*/
   /* @BindView(R.id.edit_profile_title_my_topics)
    TextView text_title_my_topics;
    @BindView(R.id.my_topics_image_plus_icon)
    ImageView imageView_my_topics_plus_icon;
    @BindView(R.id.edit_profile_recycler_view_interest)
    RecyclerView recyclerViewInterest;
    @BindView(R.id.layout_my_topics_plus_icon)
    LinearLayout layout_my_topics_plus_icon;
*/
    private TagAdapter tagAdapter;
    private InterestAdapter interestAdapter;

    private EndlessScrollListener scrollListenerTopic;
    private EndlessScrollListener scrollListenerInterest;

    PageInput tagInput;
    PageInput interestsInput;

    private ArrayList<TagModel> tagList;
    private ArrayList<InterestModel> interestsList;

    private ProfileModel profile;
    private boolean editable = true;
    private boolean updateDataOnClick = false;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final float BLUR_RADIUS = 25f;

    private ImagePickHelper imagePickHelper;
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    private String imageURL;
    Intent cropIntent;
    File myFile;
    private boolean updateDataClickOnEditBtn = false;

    @Override
    protected int getContentResId() {
        return R.layout.activity_profile_display;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linearLayoutInterest = (LinearLayout) findViewById(R.id.select_more_interest_linear_layout);
        // setSupportActionBar(toolbar);

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
*/      profile = _profileService.getMyProfile(null);
        initFields();
        setFonts();

        linearLayoutInterest.setOnClickListener(this);

        //setEditableFalse();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_PROFILE_VIEW));
        //getApplicationContext().startService(new Intent(getApplicationContext(),NotificationService.class));

        //setData();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
           /* connections.clear();
            loadData();
*/
           /* communities.clear();
            loadCommunities();*/
        }
    };

    private void initFields() {

        imagePickHelper = new ImagePickHelper();
        mResultReceiver = new AddressResultReceiver(null);
        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();


        setProfileData();
       /* initInterestAdapter();
        initTopicAdapter();*/
        hideProgress();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);

    }
    /*private void initInterestAdapter() {
        interestsInput = new PageInput();
        interestsList = new ArrayList<>();
        interestAdapter = new InterestAdapterSignUp(interestsList, interestsList, null, null);
        initializeInterests();
        recyclerViewInterest.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInterest.setAdapter(interestAdapter);
    }*/

   /* private void initTopicAdapter() {
        tagInput = new PageInput();
        tagList = new ArrayList<>();
        tagAdapter = new TagAdapter(tagList, tagList, null, null);
        initializeTopics();
        recyclerViewTopics.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTopics.setAdapter(tagAdapter);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    @OnClick(R.id.image_edit)
    public void edit() {
        updateDataClickOnEditBtn=true;
        updateOnServer();

    }

    public void editClick(Boolean updateDataOnClick) {
        if (updateDataOnClick == true) {
            updateOnServer();
        }
    }
    private void setProfileData() {
        if(profile.getName()!=null)
        {
        header.setText(profile.getName());}

        if (profile.picUrl != null && !profile.picUrl.isEmpty() && profile.picUrl.contains("http")) {
            displayImageByUrl(profile.picUrl, image_icon_blur);
            displayMaskedImageByUrl(profile.picUrl, masked_image_icon);
            imageURL = profile.picUrl;
        } else {
            displayMaskedImageByUrl("drawable://" + R.drawable.default_profile_icon, masked_image_icon);
            displayImageByUrl("drawable://" + R.drawable.default_blur_profile_icon, image_icon_blur);
        }


        if (profile.about != null)
            aboutMe.setText(profile.about);

        if (profile.gender != null)
            gender.setText(profile.gender);

        birthday.setText(String.valueOf(profile.age) + " years");

        if (null != profile.location && profile.location.name != null) {
            location.setText(profile.location.name);
        } else {
            location.setText("");
        }
    }

    private void displayImageByUrl(String publicUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS, new ImageLoadingListener(imageView));
    }

    private void displayMaskedImageByUrl(String publicUrl, MaskedImageView maskedImageView) {
        ImageLoader.getInstance().displayImage(publicUrl, maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

   /* private void setEditableFalse() {
      //  aboutMe.setEnabled(false);
      //  header.setEnabled(false);

        editable = false;
    }*/

    private void setEditableTrue() {
        aboutMe.setEnabled(true);
        //  header.setEnabled(true);

    }

    @OnClick(R.id.txt_gender)
    public void gender() {
        if (editable) {
            selectGender();
        }
        updateDataOnClick = true;
        editClick(updateDataOnClick);
    }


    @OnClick(R.id.txt_location)
    public void location() {
        if (editable) {
            popupOpen();
        }
        updateDataOnClick = true;
        editClick(updateDataOnClick);
    }


    public void selectGender() {
        final Dialog genderDialog = new Dialog(this);
        genderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        genderDialog.setContentView(R.layout.custom_gender_list);
        final TextView textMale, textFemale, textNone, textOthers;
        textMale = (TextView) genderDialog.findViewById(R.id.text_male);
        textFemale = (TextView) genderDialog.findViewById(R.id.text_female);
        textNone = (TextView) genderDialog.findViewById(R.id.text_none);
        textOthers = (TextView) genderDialog.findViewById(R.id.text_others);
        Typeface lato_regular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        textMale.setTypeface(lato_regular);
        textFemale.setTypeface(lato_regular);
        textNone.setTypeface(lato_regular);
        textOthers.setTypeface(lato_regular);
        genderDialog.show();

        textMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderDialog.dismiss();
                gender.setText("Male");
                profile.gender = "Male";
            }
        });
        textFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderDialog.dismiss();
                gender.setText("Female");
                profile.gender = "Female";
            }
        });
        textNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderDialog.dismiss();
                gender.setText("None");
                profile.gender = "None";
            }
        });
        textOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderDialog.dismiss();
                gender.setText("Others");
                profile.gender = "Others";
            }
        });
    }

    @OnClick(R.id.txt_date_of_birth)
    public void birthday() {
        if (editable) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    if (DateHelper.getDate(year, monthOfYear, dayOfMonth).getTime() > System.currentTimeMillis()) {
                        ToastUtils.shortToast("Make sure your birth date is in the past.");
                    } else {
                        profile.dateOfBirth = DateHelper.getDate(year, monthOfYear, dayOfMonth);
                        Age age = AgeCalculator.calculateAge(profile.dateOfBirth);
                        profile.age = age.getYears();
                        birthday.setText(DateHelper.stringify(profile.dateOfBirth));
                    }
                }
            }, year, month, day);
            mDatePicker.show();
        }
        updateDataOnClick = true;
        editClick(updateDataOnClick);

    }

   /* @OnClick({R.id.my_topics_image_plus_icon, R.id.interested_in_image_plus_icon})
    public void plusImageClick() {
        Intent intent = new Intent(this, InterestTagsActivity.class);
        intent.putExtra("profileModel", profile);
        intent.putExtra("OPEN_FORM", 0);
        startActivityForResult(intent, EDIT_CODE);
    }*/
/*

    @OnClick(R.id.image_done)
    public void saveClick() {
      */
/*  layout_interested_in_plus_icon.setVisibility(View.GONE);
        layout_my_topics_plus_icon.setVisibility(View.GONE);*//*

       // text_save.setVisibility(View.GONE);
       // image_edit.setVisibility(View.VISIBLE);

       // setEditableFalse();
        updateOnServer();
    }
*/


    public void popupOpen() {
        final Dialog confirmationDialog = new Dialog(this);
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDialog.setContentView(R.layout.location_popup);
        Button buttonDone;
        final TextView textAddlocation;
        final EditText editText_location_popup;
        buttonDone = (Button) confirmationDialog.findViewById(R.id.popup_button_done);
        textAddlocation = (TextView) confirmationDialog.findViewById(R.id.popup_text_add_location);
        editText_location_popup = (EditText) confirmationDialog.findViewById(R.id.popup_edittext_location);
        Typeface lato_regular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        buttonDone.setTypeface(lato_regular);
        textAddlocation.setTypeface(lato_regular);
        editText_location_popup.setTypeface(lato_regular);
        confirmationDialog.show();
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                if (editText_location_popup.getText().toString().isEmpty()) {
                    ToastUtils.longToast("Enter Location");
                } else {
                    String address = editText_location_popup.getText().toString();
                    onLocationClick(address);
                    confirmationDialog.dismiss();
                }
            }
        });
        confirmationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }


    private void onLocationClick(String address) {
        Intent intent = new Intent(this, GeoCodeIntentService.class);
        intent.putExtra(com.trutek.looped.geoCode.Constants.RECEIVER, mResultReceiver);
        int fetchType = com.trutek.looped.geoCode.Constants.USE_ADDRESS_NAME;
        intent.putExtra(com.trutek.looped.geoCode.Constants.FETCH_TYPE_EXTRA, fetchType);
        if (fetchType == com.trutek.looped.geoCode.Constants.USE_ADDRESS_NAME) {
            if (address.length() == 0) {
                ToastUtils.longToast("Please enter an address name");
                return;
            }
            intent.putExtra(com.trutek.looped.geoCode.Constants.LOCATION_NAME_DATA_EXTRA, address);
        }
        Log.e("General Fragment", "Starting Service");
        startService(intent);
    }


    private void updateOnServer() {
        profile.name = header.getText().toString();
        profile.about = aboutMe.getText().toString();
        profile.gender = gender.getText().toString();

        if (isNetworkAvailable()) {
            showProgress();
            if (profile.picUrl != null) {
                startLoadAttachFile(new File(profile.picUrl));
            } else {
                update(profile);
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }
    }

    protected void startLoadAttachFile(final File file) {
        CloudinaryImageUploadCommand.start(this, file);
    }

    private void update(final ProfileModel profile) {

        _profileService.updateProfile(profile, new AsyncResult<ProfileModel>() {
            @Override
            public void success(final ProfileModel profileModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageURL = profileModel.picUrl;
                        hideProgress();
                        saveGeneralDataIntoPreference();
                        updateDataOnClick = false;
                        // setEditableFalse();
                        if(updateDataClickOnEditBtn){
                            finish();
                        }

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

    private void saveGeneralDataIntoPreference() {
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        helper.savePreference(PreferenceHelper.FULL_NAME, profile.name);
        helper.savePreference(PreferenceHelper.GENDER, profile.gender);
        helper.savePreference(PreferenceHelper.USER_PIC_URL, profile.picUrl);
        helper.savePreference(PreferenceHelper.USER_PROFILE_ID, profile.id);

       /* if (profile.location != null){
            helper.savePreference(PreferenceHelper.LOCATION_NAME, profile.location.name);
            helper.savePreference(PreferenceHelper.LOCATION_DESCRIPTION, profile.location.description);
            if(profile.location.coordinates != null){
                helper.savePreference(PreferenceHelper.LOCATION_LONG, profile.location.coordinates.get(0));
                helper.savePreference(PreferenceHelper.LOCATION_LAT, profile.location.coordinates.get(1));
            }
        }*/

        helper.savePreference(PreferenceHelper.ABOUT_US, profile.about);

        if (profile.dateOfBirth != null)
            helper.savePreference(PreferenceHelper.B_DAY, DateHelper.stringify(profile.dateOfBirth));
    }

    @OnClick(R.id.image_view_profile_editProfile)
    public void getImage() {
        if (editable) {
            imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
        } else {
            setupImageFragment();
        }
        updateDataOnClick = true;
        editClick(updateDataOnClick);

    }

    private void setFonts() {
        Typeface avenirNextRegular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);

        // header.setTypeface(avenirNextRegular);
        aboutMe.setTypeface(avenirNextRegular);
        //   text_title_about_me.setTypeface(avenirNextRegular);
        //text_title_detail.setTypeface(avenirNextRegular);
        birthday.setTypeface(avenirNextRegular);
        gender.setTypeface(avenirNextRegular);
        location.setTypeface(avenirNextRegular);

        /*text_title_interested_in.setTypeface(avenirNextRegular);

        text_title_my_topics.setTypeface(avenirNextRegular);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

   /* private void initializeInterests() {

        interestsInput.pageNo = 1;
        if (scrollListenerInterest != null) {
            scrollListenerInterest.reset();
        }

        interestsList.clear();
        loadInterests();
    }*/

    /*private void loadInterests() {
        List<InterestModel> selectedInterests = profile.interests;
        for (InterestModel item : selectedInterests) {
            item.isSelected = true;
            interestsList.add(item);
        }
        interestAdapter.setModified();
    }
*/

   /* private void initializeTopics() {

      tagInput.pageNo = 1;
        if (scrollListenerTopic != null) {
            scrollListenerTopic.reset();
        }

        tagList.clear();
        loadTopics();
    }*/

   /* private void loadTopics() {
       List<TagModel> selectedTags = profile.tags;
        for (TagModel item : selectedTags) {
            item.isSelected = true;
            tagList.add(item);
        }

        tagAdapter.setModified();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == EDIT_CODE) {

                profile = (ProfileModel) data.getSerializableExtra("profileModel");
                //  tagList.clear();
                // loadTopics();
                interestsList.clear();
                //  loadInterests();
            }
            if (requestCode == REQUEST_CATEGORY) {

                profile = (ProfileModel) data.getSerializableExtra(Constants.MODEL_PROFILE);
            }
            if(requestCode == CROP_PIC)
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
                Bitmap bitmap_blur = BitmapFactory.decodeFile(myFile.getAbsolutePath());
                initMaskedView(thePic,bitmap_blur);
            }
        }

    }
    private Uri getImageUri(EditProfileActivity editProfileActivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(editProfileActivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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

    private void initMaskedView(Bitmap bitmap, Bitmap bitmap_blur) {
        masked_image_icon.setImageBitmap(bitmap);
        Bitmap blurred_image = makeImageBlur(bitmap_blur);
        image_icon_blur.setImageBitmap(blurred_image);

    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        ErrorUtils.logError(e);
        ToastUtils.longToast(e.getMessage());
    }

    @Override
    public void onImagePickClosed(int requestCode) {

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.select_more_interest_linear_layout):
                Intent intent = new Intent(this, SignupLocationCategoryActivity.class);
                intent.putExtra(Constants.MODEL_PROFILE,profile);
                startActivityForResult(intent,REQUEST_CATEGORY);
                break;
        }
    }


    //onActivityResult
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == com.trutek.looped.geoCode.Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(com.trutek.looped.geoCode.Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String name = resultData.getString(com.trutek.looped.geoCode.Constants.RESULT_DATA_KEY);
                        if (address != null) {
                            profile.location = new LocationModel();
                            profile.location.coordinates.add(String.valueOf(address.getLongitude()));
                            profile.location.coordinates.add(String.valueOf(address.getLatitude()));
                            profile.location.description = name;
                            profile.location.name = name;
                        }
                        location.setText(name);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(R.string.location_error);
                    }
                });
            }
        }
    }


    public class CloudinaryUploadSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            CloudinaryModel model = (CloudinaryModel) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL);
            if (model != null) {
                profile.picUrl = model.url;
            }
            update(profile);
        }
    }

    public class ImageLoadingListener extends SimpleImageLoadingListener {

        private Bitmap loadedImageBitmap;
        private String imageUrl;
        private MaskedImageView maskedImageView;
        private ImageView imageView;

        public ImageLoadingListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            super.onLoadingStarted(imageUri, view);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            imageUrl = null;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedBitmap) {
            initMaskedImageView(loadedBitmap);
            this.imageUrl = imageUri;
        }

        private void initMaskedImageView(Bitmap bitmap_blur) {
            Bitmap blurred_image = makeImageBlur(bitmap_blur);
            imageView.setImageBitmap(blurred_image);
        }

    }

    public Bitmap makeImageBlur(Bitmap bitmapImage) {
        Bitmap outputBitmap = null;
        try {
            if (null == bitmapImage)
                return null;

            outputBitmap = Bitmap.createBitmap(bitmapImage);
            final RenderScript renderScript = RenderScript.create(getApplicationContext());
            Allocation tmpIn = Allocation.createFromBitmap(renderScript, bitmapImage);
            Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
            //Intrinsic Gausian blur filter
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
            renderScript.destroy();
        } catch (Exception e) {
            Log.d("Exception ", "method exception " + e.getMessage());
        }
        return outputBitmap;
    }

    private void setupImageFragment() {
        // Transition for fragment1
        Slide slideTransition;
        PreviewImageFragment previewImageFragment = PreviewImageFragment.newInstance(imageURL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slideTransition = new Slide(Gravity.LEFT);
            slideTransition.setDuration(1000);

            // Create fragment and define some of it transitions
            previewImageFragment.setReenterTransition(slideTransition);
            previewImageFragment.setExitTransition(slideTransition);
            previewImageFragment.setSharedElementEnterTransition(new ChangeBounds());
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(previewImageFragment, PREVIEW_IMAGE_FRAGMENT);
        fragmentTransaction.commit();
    }

}
