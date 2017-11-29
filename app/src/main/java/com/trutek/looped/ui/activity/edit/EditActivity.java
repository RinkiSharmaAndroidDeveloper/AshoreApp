package com.trutek.looped.ui.activity.edit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.nostra13.universalimageloader.core.ImageLoader;
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
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.geoCode.GeoCodeIntentService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.activity.create.AddressUtil;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myConnections.SelectConnectionActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.imagepicker.ImagePickHelperFragment;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class EditActivity extends BaseAppCompatActivity implements OnImagePickedListener {
    static final int REQUEST_EDIT_ACTIVITY = 2;
    static final int CROP_PIC = 3;

    @Inject
    IActivityService activityService;

    /*Toolbar*/
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.action_bar_text_create_activity)TextView text_create_activity;

    @BindView(R.id.create_activity_write_something) EditText edit_post_description;
    @BindView(R.id.edit_event_title)EditText edit_event_title;
    @BindView(R.id.edit_event_description)EditText edit_event_description;

    @BindView(R.id.layout_write_something) LinearLayout layout_edit_post;
    @BindView(R.id.create_activity_make_event) TextView text_make_event;
    @BindView(R.id.card_view_make_event) CardView cardview_make_event;
    @BindView(R.id.make_event_collapse_image_icon) ImageView image_make_event_collapse;
    @BindView(R.id.make_event_expand_image_icon) ImageView image_make_event_expand;
    @BindView(R.id.create_activity_image) MaskedImageView image_create_event;

    /*Date&Time*/
    @BindView(R.id.edit_date)TextView edit_date;
    @BindView(R.id.edit_time)TextView edit_time;
    @BindView(R.id.create_event_calender_image)ImageView image_calender;
    @BindView(R.id.create_event_time_image)ImageView image_event_time;
    /*location*/
    @BindView(R.id.create_event_text_location) TextView text_location;
    @BindView(R.id.create_event_private_icon)ImageView location_private_image_icon;
    @BindView(R.id.create_event_global_icon)ImageView location_global_image_icon;
    /*InviteGuests*/
    @BindView(R.id.create_event_invite_guests)TextView text_invite_guests;
    @BindView(R.id.create_event_invite_plus_icon)ImageView image_plus_icon;

    private boolean isShowing;
    private ActivityModel activity;
    private ImagePickHelper imagePickHelper;
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    private AddressResultReceiver mResultReceiver;
    List<String> selectedMembersIds;
    Intent cropIntent;
    private Uri picUri;
    File myFile;
    LocationModel locationModel;
    Dialog confirmationDialog;

    @Override
    protected int getContentResId() {
        return R.layout.activity_edit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        initFields();

        setFonts();
    }


    private void initFields(){
        mResultReceiver = new AddressResultReceiver(null);
        imagePickHelper = new ImagePickHelper();
        activity = (ActivityModel) getIntent().getSerializableExtra("activityModel");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();
        selectedMembersIds =new ArrayList<>();
        setDataOnUi();
        expand();
    }

    private void setDataOnUi() {
        if(activity.picUrl != null && !activity.picUrl.isEmpty() && activity.picUrl.contains("http")){
            displayImageByUrl(activity.picUrl, image_create_event);
        } else {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.default_camera);
            image_create_event.setImageBitmap(image);
        }

        edit_event_title.setText(activity.subject);
        edit_event_description.setText(activity.body);
        edit_date.setText(DateHelper.stringify(activity.dueDate));
        edit_time.setText(DateHelper.stringifyTime(activity.dueDate));
        if(activity.location != null){
            text_location.setText(activity.location.name);
        } else {
            text_location.setText(getString(R.string.text_global));
        }

    }

    private void displayImageByUrl(String publicUrl,MaskedImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        edit_post_description.setTypeface(avenirNextRegular);
        edit_event_title.setTypeface(avenirNextRegular);
        edit_event_description.setTypeface(avenirNextRegular);
        edit_date.setTypeface(avenirNextRegular);
        edit_time.setTypeface(avenirNextRegular);
        text_location.setTypeface(avenirNextRegular);
        text_invite_guests.setTypeface(avenirNextRegular);
        text_make_event.setTypeface(avenirNextRegular);
        text_create_activity.setTypeface(avenirNextRegular);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @OnClick(R.id.create_activity_image)
    public void getImageFromGallary(){
        imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
    }

    public void expand() {
        if(isShowing){
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
    AsyncResult<String> asyncResultLocation = new AsyncResult<String>() {
        @Override
        public void success(final String locationName) {
            activity.location.name = locationName;
            if (locationName != null) {
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
        }

        @Override
        public void error(String error) {

        }
    };

    @OnClick(R.id.create_event_text_location)
    public void locationPopUp(){
        popupOpen();
    }

    @OnClick(R.id.create_event_private_icon)
    public void onPrivateIconClick(){
        popupOpen();
    }

    @OnClick(R.id.create_event_global_icon)
    public void onGlobalIconClick(){
        activity.location = new LocationModel();
        activity.location.name = getString(R.string.text_global);
        text_location.setText(getString(R.string.text_global));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.done,menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;

            case R.id.done:
                update();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.create_event_calender_image)
    public void getEventDate(){
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
        dpd.show();
    }

    @OnClick(R.id.create_event_time_image)
    public void getTime(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edit_time.setText(selectedHour + ":" + selectedMinute );
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void popupOpen(){
        confirmationDialog = new Dialog(EditActivity.this);
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
                if(editText_location_popup.getText().toString().isEmpty()){
                    ToastUtils.longToast("Enter Location");
                }else {
                    String address = editText_location_popup.getText().toString();
                    AddressUtil addressUtil = new AddressUtil();
                    showProgress();
                    addressUtil.urlConcat(address,asyncResultLocation);
                   // onLocationClick(address);
                    confirmationDialog.dismiss();
                }
            }
        });
    }

    private void onLocationClick(String address){
        Intent intent = new Intent(EditActivity.this, GeoCodeIntentService.class);
        intent.putExtra(com.trutek.looped.geoCode.Constants.RECEIVER, mResultReceiver);
        int fetchType = com.trutek.looped.geoCode.Constants.USE_ADDRESS_NAME;
        intent.putExtra(com.trutek.looped.geoCode.Constants.FETCH_TYPE_EXTRA, fetchType);
        if(fetchType == com.trutek.looped.geoCode.Constants.USE_ADDRESS_NAME) {
            if(address.length() == 0) {
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

    public void update(){
        if(isNetworkAvailable()){
            showProgress();
            if(activity.picUrl != null){
                startLoadAttachFile(new File(activity.picUrl));
            } else {
                updatePostOrData();
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }
    }

    private void updatePostOrData(){
        if(isShowing){
            activity.subject = edit_event_title.getText().toString();
            activity.body = edit_event_description.getText().toString();
            activity.type = ActivityModel.Type.event.name();

            String date = edit_date.getText().toString();
            String time = edit_time.getText().toString();

            activity.dueDate = DateHelper.parse(date + " " + time, DateHelper.StringifyAs.Custom_format);

            if(activity.subject.isEmpty()){
                ToastUtils.longToast("Enter event Title");
            }

            if(activity.body.isEmpty()){
                ToastUtils.longToast("Enter Event description");
            }

            updateEvent(activity);
        } else {
            activity.subject = edit_post_description.getText().toString();
            activity.type = ActivityModel.Type.post.name();
            if(activity.subject.isEmpty()){
                ToastUtils.longToast("Could not create empty post");
                return;
            }
            updatePost(activity);
        }
    }

    private void updatePost(ActivityModel activity){
//        if(community == null){
//            return;
//        }
//        activity.community = community;
        activityService.updateActivity(activity, new AsyncResult<ActivityModel>() {
            @Override
            public void success(ActivityModel activityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
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
        });
    }

    private void updateEvent(ActivityModel activity){
//        if(community == null){
//            return;
//        }
//        activity.community = community;
        activityService.updateActivity(activity, new AsyncResult<ActivityModel>() {
            @Override
            public void success(final ActivityModel activityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Intent intent = new Intent();
                        intent.putExtra("activityModel", activityModel);
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
                        ToastUtils.longToast(error);
                        hideProgress();
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

    @OnClick(R.id.create_event_invite_plus_icon)
    public void invite(){
       // SelectConnectionActivity.start(this);
        Intent intent =new Intent(EditActivity.this,SelectConnectionActivity.class);
        intent.putExtra("ActivityModel",activity);
        intent.putExtra("OPEN_FROM", 2);
        startActivityForResult(intent,REQUEST_EDIT_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == REQUEST_EDIT_ACTIVITY && data!=null)
            {
                 MemberModel memberModel =new MemberModel();

                 selectedMembersIds = data.getStringArrayListExtra("selectedMembers");
                 for (String members :selectedMembersIds){
                     memberModel.profile.setServerId(members);
                     activity.participantIds.add(memberModel);
                 }
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
                activity.picUrl = myFile.getAbsolutePath();
                initMaskedView(thePic);
            }
        }
    }
    private Uri getImageUri(EditActivity editActivity, Bitmap bitmap) {
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
        image_create_event.setImageBitmap(bitmap);
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
            updatePostOrData();
        }
    }

}
