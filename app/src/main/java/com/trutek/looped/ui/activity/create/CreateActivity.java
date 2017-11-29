package com.trutek.looped.ui.activity.create;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.tasks.GetFilepathFromUriTask;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.geoCode.GeoCodeIntentService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.ui.communityDashboard.myConnections.SelectConnectionActivity;
import com.trutek.looped.ui.planner.PlannerFragment;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.helpers.alarm.AlarmHelper;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class
CreateActivity extends BaseAppCompatActivity  implements OnImagePickedListener {
    static final int REQUEST_CREATE_ACTIVITY = 1;
    static final int CROP_PIC = 2;

    @Inject
    IActivityService activityService;

    /*Toolbar*/
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.action_bar_text_create_activity)
    TextView text_create_activity;

    @BindView(R.id.create_activity_write_something)
    EditText edit_post_description;
    @BindView(R.id.edit_event_title)
    EditText edit_event_title;
    @BindView(R.id.edit_event_description)
    EditText edit_event_description;

    @BindView(R.id.layout_write_something)
    LinearLayout layout_edit_post;
    @BindView(R.id.create_activity_make_event)
    TextView text_make_event;
    @BindView(R.id.card_view_make_event)
    CardView cardview_make_event;
    @BindView(R.id.make_event_collapse_image_icon)
    ImageView image_make_event_collapse;
    @BindView(R.id.make_event_expand_image_icon)
    ImageView image_make_event_expand;
    @BindView(R.id.create_activity_image)
    MaskedImageView image_create_event;

    @BindView(R.id.create_activity_button_create)
    Button button_create;
    /*Date&Time*/
    @BindView(R.id.edit_date)
    TextView edit_date;
    @BindView(R.id.edit_time)
    TextView edit_time;
    @BindView(R.id.create_event_calender_image)
    ImageView image_calender;
    @BindView(R.id.create_event_time_image)
    ImageView image_event_time;
    /*location*/
    @BindView(R.id.create_event_text_location)
    TextView text_location;
    @BindView(R.id.create_event_private_icon)
    ImageView location_private_image_icon;
    @BindView(R.id.create_event_global_icon)
    ImageView location_global_image_icon;
    /*InviteGuests*/
    @BindView(R.id.create_event_invite_guests)
    TextView text_invite_guests;
    @BindView(R.id.create_event_invite_plus_icon)
    ImageView image_plus_icon;
    TextView imageText;

    private boolean isShowing;
    private CommunityModel community;
    private ActivityModel activity;
    private ImagePickHelper imagePickHelper;
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;

    private AddressResultReceiver mResultReceiver;
    private Tracker mTracker;
    Fragment fragment = null;
    static Intent cropIntent;
    private Uri picUri;
    File myFile;
    List<String> selectedMembersIds;
    LocationModel location;
     Dialog confirmationDialog;

    @Override
    protected int getContentResId() {
        return R.layout.activity_create;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        initFields();
        setFonts();
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.HOME_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    private void initFields() {
        mResultReceiver = new AddressResultReceiver(null);
        community = (CommunityModel) getIntent().getSerializableExtra("communityModel");
        imagePickHelper = new ImagePickHelper();
        activity = new ActivityModel();
        activity.location =new LocationModel();
        imageText = (TextView) findViewById(R.id.create_event_textView_imageTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();
    }

    private void setFonts() {
        Typeface avenirNextRegular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        edit_post_description.setTypeface(avenirNextRegular);
        edit_event_title.setTypeface(avenirNextRegular);
        edit_event_description.setTypeface(avenirNextRegular);
        edit_date.setTypeface(avenirNextRegular);
        edit_time.setTypeface(avenirNextRegular);
        text_location.setTypeface(avenirNextRegular);
        text_invite_guests.setTypeface(avenirNextRegular);
        button_create.setTypeface(avenirNextRegular);
        text_make_event.setTypeface(avenirNextRegular);
        text_create_activity.setTypeface(avenirNextRegular);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @OnClick(R.id.create_activity_image)
    public void getImageFromGallary() {
        imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
        imageText.setVisibility(View.GONE);
    }

    @OnClick(R.id.create_event_invite_plus_icon)
    public void invite() {
        Intent intent = new Intent(this, SelectConnectionActivity.class);
        intent.putExtra("OPEN_FROM", 3);
        startActivityForResult(intent, REQUEST_CREATE_ACTIVITY);
    }

    @OnClick(R.id.create_event_invite_guests)
    public void inviteButton() {
        Intent intent = new Intent(this, SelectConnectionActivity.class);
        intent.putExtra("OPEN_FROM", 3);
        startActivityForResult(intent, REQUEST_CREATE_ACTIVITY);
    }

    @OnClick(R.id.make_event_layout)
    public void expand() {
        if (isShowing) {
            cardview_make_event.setVisibility(View.GONE);
            image_make_event_collapse.setVisibility(View.GONE);
            layout_edit_post.setVisibility(View.VISIBLE);
            image_make_event_expand.setVisibility(View.VISIBLE);
            isShowing = false;
        } else {
            image_make_event_expand.setVisibility(View.GONE);
            layout_edit_post.setVisibility(View.GONE);
            cardview_make_event.setVisibility(View.VISIBLE);
            image_make_event_collapse.setVisibility(View.VISIBLE);
            isShowing = true;
        }
    }

    @OnClick(R.id.create_event_text_location)
    public void locationPopUp() {
        popupOpen();
    }

    @OnClick(R.id.create_event_private_icon)
    public void onPrivateIconClick() {
        popupOpen();
        //Toast.makeText(getApplicationContext(),"Coming..",Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.create_event_global_icon)
    public void onGlobalIconClick() {
        activity.location = new LocationModel();
        activity.location.name = getString(R.string.text_global);
        text_location.setText(getString(R.string.text_global));

    }

    @OnClick(R.id.create_activity_button_create)
    public void create() {

        if (isNetworkAvailable()) {
            if (activity.picUrl == null) {
                createPostOrData();
            } else {
                showProgress();
                startLoadAttachFile(new File(activity.picUrl));
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }

    }

    private boolean checkValidations() {


        if (activity.subject == null || activity.subject.isEmpty()) {
            ToastUtils.longToast(getString(R.string.empty_subject));
            return false;
        }
        if (activity.location == null) {
            ToastUtils.longToast(getString(R.string.location_validation));
            return false;
        }
        if (activity.dueDate == null) {
            ToastUtils.longToast(getString(R.string.date_validation));
            return false;
        }
        return true;
    }

    private void createPostOrData() {
        if (isShowing) {
            activity.subject = edit_event_title.getText().toString();
            activity.body = edit_event_description.getText().toString();
            activity.type = ActivityModel.Type.event.name();

            String date = edit_date.getText().toString();
            String time = edit_time.getText().toString();

            activity.dueDate = DateHelper.parse(date + " " + time, DateHelper.StringifyAs.Custom_format);

            if (!checkValidations()) {
                return;
            }

            createEvent(activity);
        } else {
            activity.subject = edit_post_description.getText().toString();
            activity.type = ActivityModel.Type.post.name();
            if (activity.subject.isEmpty()) {
                ToastUtils.longToast("Could not create empty post");
                return;
            }
            createPost(activity);
        }
    }

    @OnClick(R.id.create_event_calender_image)
    public void getEventDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edit_date.setText(DateHelper.stringify(DateHelper.getDate(year, monthOfYear, dayOfMonth)));
            }
        }, year, month, day);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.show();
    }

    @OnClick(R.id.create_event_time_image)
    public void getTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Date date = DateHelper.convertToDate(selectedHour, selectedMinute);
                edit_time.setText(DateHelper.stringifyTime(date));
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void popupOpen() {
        confirmationDialog = new Dialog(CreateActivity.this);
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
                if (editText_location_popup.getText().toString().isEmpty()) {
                    ToastUtils.longToast("Enter Location");
                } else {
                    String address = editText_location_popup.getText().toString();
                    AddressUtil addressUtil = new AddressUtil();
                    showProgress();
                    addressUtil.urlConcat(address,asyncResultLocation);
                    confirmationDialog.dismiss();
                }
            }
        });
    }

    private void onLocationClick(String address) {
        Intent intent = new Intent(CreateActivity.this, GeoCodeIntentService.class);
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

    protected void startLoadAttachFile(final File file) {
        CloudinaryImageUploadCommand.start(this, file);
    }

    AsyncResult<String> asyncResultLocation = new AsyncResult<String>() {
        @Override
        public void success(final String locationName) {
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (locationName != null) {
                            activity.location.name = locationName;
                            text_location.setText(locationName);
                            hideProgress();
                        }
                    }
                });

        }

        @Override
        public void error(String error) {

        }
    };



    private void createPost(ActivityModel activity){
        if(community == null){
            return;
        }
        showProgress();
        activity.community = community;
        activityService.createActivity(activity, new AsyncResult<ActivityModel>() {
            @Override
            public void success(ActivityModel activityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        setResult(RESULT_OK);
                        AlarmHelper helper = new AlarmHelper(CreateActivity.this);
                        helper.setAlarm();
                        finish();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                        hideProgress();
                    }
                });
            }
        },Constants.BROADCAST_EVENT_CREATED);
    }

    private void createEvent(ActivityModel activity){
        if(community == null){
            return;
        }
        activity.community = community;
        showProgress();
        activityService.createActivity(activity, new AsyncResult<ActivityModel>() {
            @Override
            public void success(ActivityModel activityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        setResult(RESULT_OK);

                          /*set alarm*/
                        AlarmHelper helper = new AlarmHelper(CreateActivity.this);
                        helper.setAlarm();
                        finish();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                        hideProgress();
                    }
                });
            }
        },Constants.BROADCAST_POST_CREATED);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
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
    protected void onResume() {
        super.onResume();

        addActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @Override
    public void onImagePicked(int requestCode, File file) {

        Uri contentUri = Uri.fromFile(file);
        performCrop(contentUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == CROP_PIC) {
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
                activity.picUrl = myFile.getAbsolutePath();
                initMaskedView(thePic);
            }
            if(requestCode == REQUEST_CREATE_ACTIVITY)
            {
                MemberModel memberModel =new MemberModel();
                selectedMembersIds = data.getStringArrayListExtra("selectedMembers");
                for(String memberIds :selectedMembersIds)
                {
                  //  activity.lastUpdate.profile.setServerId(memberIds);
                    memberModel.profile.setServerId(memberIds);
                    activity.participantIds.add(memberModel);
                }
            }
        }
    }

    private Uri getImageUri(CreateActivity editActivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(editActivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void initMaskedView(Bitmap bitmap) {
        image_create_event.setImageBitmap(bitmap);
    }
    private void performCrop(Uri contentUri) {
        try {
             cropIntent= new Intent("com.android.camera.action.CROP");

             cropIntent.setDataAndType(contentUri,"image/*");

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
                        if(address != null) {
                            if(activity != null){
                                activity.location = new LocationModel();
                            }
                            activity.location.coordinates.add(String.valueOf(address.getLongitude()));
                            activity.location.coordinates.add(String.valueOf(address.getLatitude()));
                            activity.location.name = name;
                            activity.location.description = name;
                        }
                        text_location.setText(name);
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
            if(model != null){
                activity.picUrl = model.url;
            }
            hideProgress();
            createPostOrData();
        }
    }
}
