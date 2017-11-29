package com.trutek.looped.ui.recipient.recipient.display;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.contracts.models.LoopModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.INotificationService;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.Utils.ageCalculator.Age;
import com.trutek.looped.msas.common.Utils.ageCalculator.AgeCalculator;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.dialogs.PreviewImageFragment;
import com.trutek.looped.ui.recipient.recipient.adapter.DiseaseAdapter;
import com.trutek.looped.ui.recipient.recipient.disease.DisplayDiseaseActivity;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class DisplayRecipientActivity extends BaseAppCompatActivity implements OnImagePickedListener, PreviewImageFragment.OnFragmentInteractionListener {

    private final static String PREVIEW_IMAGE_FRAGMENT = "PreviewImageFragment";
    private static final float BLUR_RADIUS = 25f;
    static final int REQUEST_DISEASE_LIST = 1;

    @Inject
    IRecipientService recipientService;
    @Inject
    INotificationService notificationService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.header) EditText header;
    @BindView(R.id.image_edit) ImageView imageEdit;
    @BindView(R.id.image_done) ImageView imageDone;
    @BindView(R.id.masked_image_view_profile) MaskedImageView maskedImageViewProfile;
    @BindView(R.id.imageView_profile_blur) ImageView imageViewProfileBlur;

    @BindView(R.id.display_recipient_textView_gender) TextView textGender;
    @BindView(R.id.display_recipient_textView_dob) TextView textAge;
    @BindView(R.id.recyclerView_condition) RecyclerView recyclerViewCondition;
    @BindView(R.id.imageView_condition_edit) ImageView conditionEdit;

    @BindView(R.id.card_accept_reject) CardView cardAcceptReject;
    @BindView(R.id.button_accept) Button buttonAccept;
    @BindView(R.id.button_reject) Button buttonReject;

    private RecipientModel recipient;
    private DiseaseAdapter adapter;
    private ImagePickHelper imagePickHelper;
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    private String imageURL;

    private ArrayList<DiseaseModel> diseaseList;
    private ArrayList<DiseaseModel> filteredDiseasesList;

    private boolean editable;
    private boolean requestForRecipient;
    private String notificationId;

    @Override
    protected int getContentResId() {
        return R.layout.activity_display_recipient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFields();
        initAdapter();

        if(requestForRecipient){
            recipient = (RecipientModel) getIntent().getSerializableExtra("recipient");
            notificationId = getIntent().getStringExtra("notificationId");
            getRecipientDetailsFromServer();
        } else {
            recipient = getRecipientDetails();
            setRecipientData();
            getRecipientDetailsFromServerAndSave();
        }

    }

    private void initFields(){
        diseaseList = new ArrayList<>();
        filteredDiseasesList = new ArrayList<>();
        imagePickHelper = new ImagePickHelper();
        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();

        requestForRecipient = getIntent().getBooleanExtra("requestForRecipient", false);

        if(requestForRecipient){
            setEditableFalse();
            imageEdit.setVisibility(View.GONE);
        } else {
            setEditableFalse();
        }
    }

    private void initAdapter() {

        adapter = new DiseaseAdapter(diseaseList, filteredDiseasesList, null, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewCondition.setLayoutManager(layoutManager);
        recyclerViewCondition.setAdapter(adapter);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private RecipientModel getRecipientDetails(){
        return recipientService.getLastRecipientFromLocal();
    }

    private void getRecipientDetailsFromServer(){
        showProgress();
        recipientService.getRecipient(recipient.getServerId(), new AsyncResult<RecipientModel>() {
            @Override
            public void success(final RecipientModel recipientModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recipient = recipientModel;
                        setRecipientData();
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

    private void resolveAcceptRejectCard(){
        if(requestForRecipient){
            cardAcceptReject.setVisibility(View.VISIBLE);
        } else {
            cardAcceptReject.setVisibility(View.GONE);
        }
    }

    private void getRecipientDetailsFromServerAndSave(){
        recipientService.getRecipientAndSave(recipient.getServerId(), new AsyncResult<RecipientModel>() {
            @Override
            public void success(final RecipientModel recipientModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recipient = recipientModel;

                        if(recipientModel.getLoops() != null && recipientModel.getLoops().size() > 0){
                            for (LoopModel model : recipientModel.getLoops()) {
                                model.profileId = model.profile.id;
                            }

                        }

                        setRecipientData();
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

    private void setRecipientData(){
        header.setText(recipient.getName());
        resolveAcceptRejectCard();

        if (recipient.picUrl != null && !recipient.picUrl.isEmpty() && recipient.picUrl.contains("http")) {
            displayImageByUrl(recipient.picUrl, imageViewProfileBlur);
            displayMaskedImageByUrl(recipient.picUrl, maskedImageViewProfile);
        } else {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_icon);
            maskedImageViewProfile.setImageBitmap(image);

            Bitmap image_to_blur = BitmapFactory.decodeResource(getResources(), R.drawable.default_blur_profile_icon);
            imageViewProfileBlur.setImageBitmap(image_to_blur);
        }

        if(recipient != null && recipient.getGender() != null){
            textGender.setText(recipient.getGender());
        }

        if(recipient != null){
            textAge.setText(String.valueOf(recipient.age));
        }

        if(recipient.diseases != null){
            initDiseaseList();
            recipient.setDiseasesId(getDiseaseIdList(recipient.diseases));
        }
    }

    private void displayImageByUrl(String publicUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS, new ImageLoadingListener(imageView));
    }

    private void displayMaskedImageByUrl(String publicUrl, MaskedImageView maskedImageView) {
        ImageLoader.getInstance().displayImage(publicUrl,maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void initDiseaseList() {
        diseaseList.clear();
        filteredDiseasesList.clear();


        for (DiseaseModel item : recipient.diseases) {
            if (!diseaseList.contains(item)) {
                item.isSelected = true;
                diseaseList.add(item);
            }
        }
        filteredDiseasesList.addAll(diseaseList);

        adapter.setModified();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.image_edit)
    public void edit() {
        setEditableTrue();

        editClick();
    }

    @OnClick(R.id.image_done)
    public void saveClick() {
        setEditableFalse();

        updateOnServer();
    }

    private void setEditableFalse() {
        header.setEnabled(false);
        editable = false;
        doneClick();
    }

    private void setEditableTrue() {
        header.setEnabled(true);
        editable = true;
        editClick();
    }

    public void editClick() {
        imageEdit.setVisibility(View.GONE);
        conditionEdit.setVisibility(View.VISIBLE);
        imageDone.setVisibility(View.VISIBLE);
    }

    public void doneClick() {
        conditionEdit.setVisibility(View.GONE);
        imageDone.setVisibility(View.GONE);
        imageEdit.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.masked_image_view_profile)
    public void getImage() {
        if (editable)
            imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
        else
            setupImageFragment();
    }

    private void setupImageFragment() {
        // Transition for fragment1
        Slide slideTransition;
        PreviewImageFragment previewImageFragment = PreviewImageFragment.newInstance(recipient.picUrl);
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

    @OnClick(R.id.display_recipient_textView_gender)
    public void selectGender(){
        if(editable){
            DialogUtil.showGenderDialog(DisplayRecipientActivity.this, genderDialogCallback);
        }
    }

    @OnClick(R.id.display_recipient_textView_dob)
    public void selectAge(){
        if(editable){
            agePicker();
        }
    }

    @OnClick(R.id.imageView_condition_edit)
    public void addConditions(){
        Intent intent = new Intent(this, DisplayDiseaseActivity.class);
        intent.putExtra("recipientModel", recipient);
        intent.putExtra("OPEN_FORM", 1);
        startActivityForResult(intent, REQUEST_DISEASE_LIST);
    }

    private void agePicker(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                if (DateHelper.getDate(year, monthOfYear, dayOfMonth).getTime() > System.currentTimeMillis()) {
                    ToastUtils.shortToast("Make sure you birth date is in the past.");
                } else {
                    Date dateOfBirth = DateHelper.getDate(year, monthOfYear, dayOfMonth);
                    Age age = AgeCalculator.calculateAge(dateOfBirth);
                    recipient.age = age.getYears();
                    textAge.setText(String.valueOf(recipient.age));
                }
            }
        }, year, month, day);
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        mDatePicker.show();
    }

    AsyncResult<String> genderDialogCallback = new AsyncResult<String>() {
        @Override
        public void success(String gender) {
            textGender.setText(gender);
            recipient.setGender(gender);
        }

        @Override
        public void error(String error) {

        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void updateOnServer() {
        recipient.setName(header.getText().toString());

        if (isNetworkAvailable()) {
            showProgress();
            if (imageURL != null && !imageURL.isEmpty()) {
                startLoadAttachFile(new File(imageURL));
            } else {
                update(recipient);
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }
    }

    protected void startLoadAttachFile(final File file) {
        CloudinaryImageUploadCommand.start(this, file);
    }


    @Override
    protected void onResume() {
        super.onResume();

        addActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
        unbinder = null;
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

    private void update(final RecipientModel recipient) {

        recipientService.updateRecipient(recipient, new AsyncResult<RecipientModel>() {
            @Override
            public void success(final RecipientModel recipientModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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

    @Override
    public void onImagePicked(int requestCode, File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Bitmap bitmap_blur = BitmapFactory.decodeFile(file.getAbsolutePath());
        initMaskedView(bitmap, bitmap_blur);
        imageURL = file.getAbsolutePath();
    }

    private void initMaskedView(Bitmap bitmap, Bitmap bitmap_blur) {
        maskedImageViewProfile.setImageBitmap(bitmap);
        Bitmap blurred_image = makeImageBlur(bitmap_blur);
        imageViewProfileBlur.setImageBitmap(blurred_image);

    }

    public Bitmap makeImageBlur(Bitmap bitmapImage){
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
        }
        catch (Exception e){
            Log.d("Exception ","method exception "+e.getMessage());
        }
        return outputBitmap;
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        ErrorUtils.logError(e);
        ToastUtils.longToast(e.getMessage());
    }

    @Override
    public void onImagePickClosed(int requestCode) {

    }

    public class CloudinaryUploadSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            CloudinaryModel model = (CloudinaryModel) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL);
            if (model != null) {
                recipient.picUrl = model.url;
            }
            update(recipient);
        }
    }

    public class ImageLoadingListener extends SimpleImageLoadingListener {

        private String imageUrl;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DISEASE_LIST && null != data) {
            recipient = (RecipientModel) data.getSerializableExtra("recipientModel");
            recipient.setDiseasesId(getDiseaseIdList(recipient.diseases));
            initDiseaseList();
        }
    }

    ArrayList<String> getDiseaseIdList(List<DiseaseModel> diseaseModels) {
        ArrayList<String> diseaseIds = new ArrayList<>();
        for (DiseaseModel diseaseModel : diseaseModels) {
            if(diseaseModel.isSelected()) {
                diseaseIds.add(diseaseModel.getServerId());
            }
        }

        return diseaseIds;
    }

    @OnClick(R.id.button_accept)
    public void accept(){
        List<RecipientModel> recipientModels = recipientService.search(new PageInput()).items;
        if(recipientModels.size() > 0){
            ToastUtils.longToast("You have already one recipient.");
            return;
        }
        acceptRecipient();
    }

    private void acceptRecipient() {
        showProgress();
        recipientService.acceptRecipientInvitation(recipient, new AsyncResult<RecipientModel>() {
            @Override
            public void success(RecipientModel recipientModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deleteNotification();
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

    private void deleteNotification(){
        notificationService.deleteNotification(notificationId, new AsyncNotify() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        cardAcceptReject.setVisibility(View.GONE);
                        ToastUtils.longToast("Request Accepted");
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


    @OnClick(R.id.button_reject)
    public void reject(){

    }

}
