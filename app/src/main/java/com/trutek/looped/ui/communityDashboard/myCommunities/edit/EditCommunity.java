package com.trutek.looped.ui.communityDashboard.myCommunities.edit;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.tasks.GetFilepathFromUriTask;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.data.contracts.services.ITagService;
import com.trutek.looped.geoCode.GeoCodeIntentService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.activity.create.AddressUtil;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.profile.create.adapter.InterestAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class EditCommunity extends BaseAppCompatActivity implements OnImagePickedListener,View.OnClickListener{
    static final int EDIT_COMMUNITY = 1;
    static final int CROP_PIC = 2;
    @Inject
    ITagService tagService;
    @Inject
    IInterestService interestService;
    @Inject
    ICommunityService communityService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.edit_community_image_view) MaskedImageView edit_community_image_view;
    @BindView(R.id.edit_community_location_private_image_icon) ImageView edit_community_location_private_icon;
    @BindView(R.id.edit_community_location_global_image_icon) ImageView edit_community_location_global_icon;

    @BindView(R.id.edit_community_name) EditText communityName;
    @BindView(R.id.edit_community_add_description) EditText communityDescription;
    @BindView(R.id.txt_community_location) TextView communityLocation;
  //  @BindView(R.id.edit_community_tag) EditText searchTag;
    @BindView(R.id.edit_community_interest) EditText searchInterest;

    @BindView(R.id.edit_community_recycler_view_tag)RecyclerView recyclerViewTag;
    @BindView(R.id.edit_community_recycler_view_interest)RecyclerView recyclerViewInterest;
//    Toolbar toolbar;
    //@BindView(R.id.toolbar) Toolbar toolbar;
    private CommunityModel community;
    LinearLayout select_more_interest;
    ToggleButton togle_privacy,toggle_group;
    private EndlessScrollListener scrollListenerTag;
    private EndlessScrollListener scrollListenerInterest;

    private OnActionListener<TagModel> tagSelectedActionListeners;
    private OnActionListener<TagModel> tagUnSelectedActionListeners;
    private OnActionListener<InterestModel> interestSelectedActionListeners;
    private OnActionListener<InterestModel> interestUnSelectedActionListeners;

    private TagAdapter tagAdapter;
    private InterestAdapter interestAdapter;

    private ArrayList<TagModel> tagList;
    private ArrayList<InterestModel> interestsList;

    private ArrayList<TagModel> filteredTags;
    private ArrayList<InterestModel> filteredInterests;

    private HashMap<String, TagModel> mapTags;
    private HashMap<String, InterestModel> mapInterests;

    private ImagePickHelper imagePickHelper;
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    PageInput tagInput;
    PageInput interestsInput;
    Intent cropIntent;
    private Uri picUri;
    File myFile;
    Dialog confirmationDialog;

    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        select_more_interest =(LinearLayout)findViewById(R.id.select_more_interest_linear_layout);
        toggle_group =(ToggleButton)findViewById(R.id.community_group_post_text);
        togle_privacy =(ToggleButton)findViewById(R.id.community_privecy_text);
        mResultReceiver = new AddressResultReceiver(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        community = (CommunityModel) getIntent().getSerializableExtra("CommunityModel");
        initFields();
        initViews();

        togle_privacy.setOnClickListener(this);
        toggle_group.setOnClickListener(this);
        select_more_interest.setOnClickListener(this);
    }

    private void initFields(){

        init();


        tagInput = new PageInput();
        interestsInput = new PageInput();

        tagList = new ArrayList<>();
        interestsList = new ArrayList<>();

        filteredTags = new ArrayList<>();
        filteredInterests = new ArrayList<>();

        tagAdapter = new TagAdapter(tagList, filteredTags, tagSelectedActionListeners, tagUnSelectedActionListeners);
        interestAdapter = new InterestAdapter(interestsList, filteredInterests, interestSelectedActionListeners, interestUnSelectedActionListeners);

//        initializeTags();
        initializeInterests();

        initAdapters();
     //   initListeners();
    }

    private void initViews() {
        if(community.picUrl != null && !community.picUrl.isEmpty() && community.picUrl.contains("http")){
            displayImageByUrl(community.picUrl, edit_community_image_view);
        } else {
            edit_community_image_view.setImageDrawable(edit_community_image_view.getResources().getDrawable(R.drawable.background_round_color_second));
        }

        communityName.setText(community.subject);
        communityDescription.setText(community.body);
        if(community.location != null){
            communityLocation.setText(community.location.name);
        }
        if(community.isPrivate) {
            togle_privacy.setChecked(true);
        }else{
            togle_privacy.setChecked(true);
        }
        if(community.canSeePost) {
            toggle_group.setChecked(false);
        }else{
            toggle_group.setChecked(true);
        }
    }

    private void displayImageByUrl(String publicUrl,MaskedImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    @OnClick(R.id.edit_community_image_view)
    public void selectPic(){
        imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_edit_community;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }


    private void init(){
        imagePickHelper = new ImagePickHelper();
        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();mapTags = new HashMap<>();
        mapInterests = new HashMap<>();
        tagSelectedActionListeners = new OnActionListener<TagModel>() {
            @Override
            public void notify(TagModel tagModel) {
                community.tags.add(tagModel);
            }
        };

        tagUnSelectedActionListeners = new OnActionListener<TagModel>() {
            @Override
            public void notify(TagModel tagModel) {
                community.tags.remove(tagModel);
            }
        };

        interestSelectedActionListeners  = new OnActionListener<InterestModel>() {
            @Override
            public void notify(InterestModel interestModel) {
                community.interests.add(interestModel);
            }
        };

        interestUnSelectedActionListeners  = new OnActionListener<InterestModel>() {
            @Override
            public void notify(InterestModel interestModel) {
                community.interests.remove(interestModel);
            }
        };
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
            community.picUrl = myFile.getAbsolutePath();
            initMaskedView(thePic);
        }
    }
    private Uri getImageUri(EditCommunity editCommunity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(editCommunity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    private void initializeTags() {

        tagInput.pageNo = 1;
        if (scrollListenerTag != null) {
            scrollListenerTag.reset();
        }

        filteredTags.clear();
        tagList.clear();
        mapTags.clear();
        ArrayList<TagModel> selectedInterests = community.tags;
        for(TagModel item : selectedInterests){
            item.isSelected = true;
            mapTags.put(item.getName(),item);
        }
        loadTopics();
    }

    private void initializeInterests() {

        interestsInput.pageNo = 1;
        if (scrollListenerInterest != null) {
            scrollListenerInterest.reset();
        }

        filteredInterests.clear();
        interestsList.clear();
        mapInterests.clear();
        ArrayList<InterestModel> selectedInterests = community.interests;
        for(InterestModel item : selectedInterests){
            item.isSelected = true;
            mapInterests.put(item.getName(),item);
        }
        loadInterests();
    }

    private void initAdapters() {
//        recyclerViewTopic.addOnScrollListener(scrollListenerTag);
//        recyclerViewInterest.addOnScrollListener(scrollListenerInterest);

        final LinearLayoutManager layoutManagerTopic = new LinearLayoutManager(this);
        final LinearLayoutManager layoutManagerInterest = new LinearLayoutManager(this);

        recyclerViewTag.setLayoutManager(layoutManagerTopic);
        recyclerViewInterest.setLayoutManager(layoutManagerInterest);

        recyclerViewTag.setAdapter(tagAdapter);
        recyclerViewInterest.setAdapter(interestAdapter);
    }

    private void initListeners() {
       /* searchTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tagAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchInterest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                interestAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
           case android.R.id.home:
               onBackPressed();
               break;
           case R.id.done :
               updateCommunity();
               break;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.txt_community_location)
    public void location() {
        popupOpen();
    }

    public void popupOpen() {
        confirmationDialog = new Dialog(this);
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        confirmationDialog.setCanceledOnTouchOutside(false);
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
                if(getCurrentFocus()!=null && getCurrentFocus() instanceof EditText){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                if (editText_location_popup.getText().toString().isEmpty()) {
                    ToastUtils.longToast("Enter Location");
                } else {
                    String address = editText_location_popup.getText().toString();
                    //onLocationClick(address);
                    AddressUtil addressUtil = new AddressUtil();
                    showProgress();
                    addressUtil.urlConcat(address,asyncResultLocation);
                    confirmationDialog.dismiss();
                }
            }
        });
        confirmationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(getCurrentFocus()!=null && getCurrentFocus() instanceof EditText){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }
    AsyncResult<String> asyncResultLocation = new AsyncResult<String>() {
        @Override
        public void success(final String locationName) {
            if (locationName != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(locationName !=null) {
                            community.location.name = locationName;
                            communityLocation.setText(locationName);
                            hideProgress();
                        }
                    }
                });
            }
        }

        @Override
        public void error(String error) {

        }
    };

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

    private void updateCommunity() {
        community.subject = communityName.getText().toString();
        community.body = communityDescription.getText().toString();

        if(community.subject.isEmpty()){
            ToastUtils.longToast(getString(R.string.empty_string));
        }
        if(community.body.isEmpty()){
            ToastUtils.longToast(getString(R.string.empty_string));
        }

        if(isNetworkAvailable()){
            showProgress();
            if(community.picUrl != null && !community.picUrl.isEmpty()){
                startLoadAttachFile(new File(community.picUrl));
            } else {
                update(community);
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }
    }

    protected void startLoadAttachFile(final File file) {
        CloudinaryImageUploadCommand.start(this, file);
    }

    private void update(final CommunityModel community){

        communityService.updateCommunity(community, new AsyncResult<CommunityModel>() {
            @Override
            public void success(CommunityModel communityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Intent intent = new Intent();
                        intent.putExtra("communityModel", community);
                        setResult(RESULT_OK, intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.done,menu);
        return super.onCreateOptionsMenu(menu);

    }

    private void loadTopics() {
        tagService.getAll(tagInput, new AsyncResult<Page<TagModel>>() {
            @Override
            public void success(Page<TagModel> models) {

                for (TagModel item : models.items) {
                    if(!mapTags.containsKey(item.getName())){
                        mapTags.put(item.getName(),item);
                    }
                }

                tagList.addAll(mapTags.values());


                Collections.sort(tagList, new Comparator<TagModel>() {
                    @Override
                    public int compare(TagModel lhs, TagModel rhs) {
                        if (lhs.isSelected && !rhs.isSelected)
                            return -1;

                        if (lhs.isSelected && rhs.isSelected)
                            return 0;

                        if (!lhs.isSelected && rhs.isSelected)
                            return 1;

                        return 0;
                    }
                });

                filteredTags.addAll(tagList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tagAdapter.setModified();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });

    }

    private void loadInterests() {
        interestService.getAll(interestsInput, new AsyncResult<Page<InterestModel>>() {
            @Override
            public void success(Page<InterestModel> models) {

                for (InterestModel item : models.items) {
                    if(!mapInterests.containsKey(item.getName())){
                        mapInterests.put(item.getName(),item);
                    }
                }
                interestsList.addAll(mapInterests.values());
                Collections.sort(interestsList, new Comparator<InterestModel>() {
                    @Override
                    public int compare(InterestModel lhs, InterestModel rhs) {
                        if (lhs.isSelected && !rhs.isSelected)
                            return -1;

                        if (lhs.isSelected && rhs.isSelected)
                            return 0;

                        if (!lhs.isSelected && rhs.isSelected)
                            return 1;

                        return 0;
                    }
                });

                filteredInterests.addAll(interestsList);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        interestAdapter.setModified();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        addActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeActions();
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
        edit_community_image_view.setImageBitmap(bitmap);
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        ErrorUtils.logError(e);
        ToastUtils.longToast(e.getMessage());
    }

    @Override
    public void onImagePickClosed(int requestCode) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case(R.id.select_more_interest_linear_layout):
                Intent i = new Intent(EditCommunity.this, SignupLocationCategoryActivity.class);
                startActivityForResult(i,EDIT_COMMUNITY);
                break;
            case(R.id.community_privecy_text):
                    String privacy_selected_text = togle_privacy.getText().toString() ;
                if (privacy_selected_text.contains("Privacy")) {
                    community.isPrivate = false;
                } else {
                    community.isPrivate = true;
                }
                break;
            case(R.id.community_group_post_text):
                String group_selected_text =toggle_group.getText().toString();
                if(group_selected_text.contains("Everyone")){
                    community.canSeePost=true;
                }else{
                    community.canSeePost=false;
                }
                break;
        }
    }

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
                        if(address != null){
                            community.location.coordinates.add(String.valueOf(address.getLongitude()));
                            community.location.coordinates.add(String.valueOf(address.getLatitude()));
                            community.location.description = name;
                            community.location.name = name;
                        }
                        communityLocation.setText(name);
                    }
                });
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("Location not Found");
                    }
                });
            }
        }
    }

    public class CloudinaryUploadSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            CloudinaryModel model = (CloudinaryModel) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL);
            if(model != null){
                community.picUrl = model.url;
            }
            update(community);
        }
    }

}
