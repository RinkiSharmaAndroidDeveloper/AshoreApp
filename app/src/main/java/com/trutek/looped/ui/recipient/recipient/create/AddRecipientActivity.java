package com.trutek.looped.ui.recipient.recipient.create;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ageCalculator.Age;
import com.trutek.looped.msas.common.Utils.ageCalculator.AgeCalculator;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.ui.profile.create.CreateProfileStep2Activity;
import com.trutek.looped.ui.profile.create.CreateProfileStep3Activity;
import com.trutek.looped.ui.recipient.recipient.disease.DisplayDiseaseActivity;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class AddRecipientActivity extends BaseAppCompatActivity implements View.OnClickListener, OnImagePickedListener {

    @Inject
    IRecipientService recipientService;

    static final int REQUEST_DISEASE_LIST = 1;

    ImageView imageView_recipientPic;
    EditText editText_name;
    TextView textView_gender, textView_disease, textView_birthday, textView_terms_condition;
    Button button_done;
    RecipientModel recipientModel;

    String imageURI, errorMessage = "";
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    private ImagePickHelper imagePickHelper;

    @Override
    protected int getContentResId() {
        return R.layout.activity_add_recipient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setFonts();
        listener();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void init() {
        imageView_recipientPic = (ImageView) findViewById(R.id.add_recipient_profile_pic_icon);
        editText_name = (EditText) findViewById(R.id.add_recipient_editText_name);
        textView_gender = (TextView) findViewById(R.id.add_recipient_textView_gender);
        textView_disease = (TextView) findViewById(R.id.add_recipient_textView_disease);
        textView_birthday = (TextView) findViewById(R.id.add_recipient_textView_birthday);
        textView_terms_condition = (TextView) findViewById(R.id.add_recipient_textView_terms_and_cond);

        button_done = (Button) findViewById(R.id.add_recipient_button_done);

        recipientModel = new RecipientModel();
        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();
    }


    private void setFonts() {
        editText_name.setTypeface(avenirNextRegular);
        textView_gender.setTypeface(avenirNextRegular);
        textView_disease.setTypeface(avenirNextRegular);
        textView_birthday.setTypeface(avenirNextRegular);
    }

    private void listener() {

        imageView_recipientPic.setOnClickListener(this);
        textView_gender.setOnClickListener(this);
        textView_disease.setOnClickListener(this);
        textView_birthday.setOnClickListener(this);
        textView_terms_condition.setOnClickListener(this);
        button_done.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == imageView_recipientPic.getId()) {
            imagePickHelper = new ImagePickHelper();
            imagePickHelper.pickAnImage(AddRecipientActivity.this, ImageUtils.IMAGE_REQUEST_CODE);
        } else if (view.getId() == textView_gender.getId()) {
            DialogUtil.showGenderDialog(AddRecipientActivity.this, genderDialogCallback);

        } else if (view.getId() == textView_disease.getId()) {
            Intent intent = new Intent(AddRecipientActivity.this, DisplayDiseaseActivity.class);
            startActivityForResult(intent, REQUEST_DISEASE_LIST);

        } else if (view.getId() == textView_birthday.getId()) {
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
                        Date dateOfBirth = DateHelper.getDate(year, monthOfYear, dayOfMonth);
                        Age age = AgeCalculator.calculateAge(dateOfBirth);
                        recipientModel.age = age.getYears();
                        textView_birthday.setText(DateHelper.stringify(dateOfBirth));
                    }
                }
            }, year, month, day);
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();

        } else if (view.getId() == textView_terms_condition.getId()) {
            //TODO show term and condition

        } else if (view.getId() == button_done.getId()) {
            if (isValidate()) {
                updateOnServer();
            } else {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateOnServer() {
        recipientModel.name = editText_name.getText().toString();

        if (isNetworkAvailable()) {
            showProgress();
            if (imageURI != null && !imageURI.isEmpty()) {
                startLoadAttachFile(new File(imageURI));
            } else {
                create(recipientModel);
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }
    }

    protected void startLoadAttachFile(final File file) {
        CloudinaryImageUploadCommand.start(this, file);
    }

    private void create(final RecipientModel recipient) {
        recipientService.createRecipient(recipient, savedRecipient);
    }

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
            DialogUtil.showInviteDialog(AddRecipientActivity.this, inviteDialogCallback);

        }
    };

    AsyncNotify inviteDialogCallback = new AsyncNotify() {
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

    AsyncResult<String> genderDialogCallback = new AsyncResult<String>() {
        @Override
        public void success(String gender) {
            textView_gender.setText(gender);
            recipientModel.gender = gender;
        }

        @Override
        public void error(String error) {

        }
    };

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
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        initMaskedView(bitmap);
        imageURI = file.getAbsolutePath();
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {

    }

    @Override
    public void onImagePickClosed(int requestCode) {

    }

    private void initMaskedView(Bitmap bitmap) {
        imageView_recipientPic.setImageBitmap(bitmap);
    }

    boolean isValidate() {
        String name = editText_name.getText().toString();

        if (name.equals("") || name.trim().length() == 0) {
            errorMessage = "Invalid Name";
            return false;
        } else if (null == recipientModel.getDiseasesId() && recipientModel.getDiseasesId().size() == 0) {
            errorMessage = "Please select a condition";
            return false;
        }
        return true;
    }

    AsyncResult<RecipientModel> savedRecipient = new AsyncResult<RecipientModel>() {
        @Override
        public void success(RecipientModel profileModel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    DialogUtil.showDiscoverDialog(AddRecipientActivity.this, discoverDialogCallback);
                }
            });
        }

        @Override
        public void error(final String error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DISEASE_LIST && null != data) {
            ArrayList<DiseaseModel> diseaseModels = (ArrayList<DiseaseModel>) data.getSerializableExtra(Constants.INTENT_KEY_DISEASE);
            recipientModel.setDiseasesId(getDiseaseIdList(diseaseModels));
            textView_disease.setText(getDiseaseNameList(diseaseModels));
        }
    }

    public class CloudinaryUploadSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            CloudinaryModel model = (CloudinaryModel) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL);
            if (model != null) {
                recipientModel.picUrl = model.url;
            }
            create(recipientModel);
        }
    }

    String getDiseaseNameList(ArrayList<DiseaseModel> diseaseModels) {

        StringBuilder result = new StringBuilder();
        for (DiseaseModel diseaseModel : diseaseModels) {
            if(diseaseModel.isSelected()) {
                result.append(diseaseModel.getName());
                result.append(",");
            }
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1) : "";

    }

    ArrayList<String> getDiseaseIdList(ArrayList<DiseaseModel> diseaseModels) {
        ArrayList<String> diseaseIds = new ArrayList<>();
        for (DiseaseModel diseaseModel : diseaseModels) {
            if(diseaseModel.isSelected()) {
                diseaseIds.add(diseaseModel.getServerId());
            }
        }

        return diseaseIds;
    }

}
