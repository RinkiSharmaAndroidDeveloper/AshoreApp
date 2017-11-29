package com.trutek.looped.ui.medicine.create;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.models.ScheduleModel;
import com.trutek.looped.data.contracts.services.IMedicineService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.medicine.MedicineAdapter.MedicationFrequencyAdapter;
import com.trutek.looped.ui.medicine.edit.DosagesScheduleActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Rinki on 11/29/2016.
 */
public class AddMedicineActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private static int RESULT_LOAD_IMAGE = 2;
    private static final int REQUEST_CODE_SCHEDULE = 1;
    static final String TAG = AddMedicineActivity.class.getSimpleName();

    @Inject
    IMedicineService medicineService;

    ImageView frequency_dosage_add_arrow, back_arrow;
    MaskedImageView medicine_image;
    TextView medicine_name;
    Spinner dosage_spinner;
    Button create_medicine;
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    String isVisible;
    String result, dosge;
    MedicineModel medModel;
    RecipientModel mRecipientModel;
    private RecyclerView.Adapter mAdapter;
    RecyclerView recyclerView;
    CheckBox sun, mon, tue, wed, thu, fri, sat;
    String picturePath, med_name;
    private ImagePickHelper imagePickHelper;

    @Override
    protected int getContentResId() {
        return R.layout.activity_add_medicine;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frequency_dosage_add_arrow = (ImageView) findViewById(R.id.add_medicine_schedule_duration_arrow);
        back_arrow = (ImageView) findViewById(R.id.add_medicine_back_);
        recyclerView = (RecyclerView) findViewById(R.id.add_medicine_recycler_medicine_dosage);
        medicine_image = (MaskedImageView) findViewById(R.id.add_medicine_image);
        medicine_name = (TextView) findViewById(R.id.add_medicine_name);
        dosage_spinner = (Spinner) findViewById(R.id.add_medicine_dosage);
        create_medicine = (Button) findViewById(R.id.add_medicine_button_create);
        linearLayout = (LinearLayout) findViewById(R.id.add_medicine_duration_frequency);
        relativeLayout = (RelativeLayout) findViewById(R.id.add_medicine_duration);
        sun = (CheckBox) findViewById(R.id.schedule_frequency_sun);
        mon = (CheckBox) findViewById(R.id.schedule_frequency_mon);
        tue = (CheckBox) findViewById(R.id.schedule_frequency_tue);
        wed = (CheckBox) findViewById(R.id.schedule_frequency_wed);
        thu = (CheckBox) findViewById(R.id.schedule_frequency_thu);
        fri = (CheckBox) findViewById(R.id.schedule_frequency_fri);
        sat = (CheckBox) findViewById(R.id.schedule_frequency_sat);
        imagePickHelper = new ImagePickHelper();
        mRecipientModel = (RecipientModel) getIntent().getSerializableExtra(Constants.MODEL_RECIPIENT);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getApplicationContext(),
                R.array.Medicine_Dosage_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dosage_spinner.setAdapter(adapter);
        dosage_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dosge = parent.getItemAtPosition(position).toString();
                ((TextView) dosage_spinner.getSelectedView()).setTextColor(getResources().getColor(R.color.dark_gray));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();
        medModel = new MedicineModel();
        frequency_dosage_add_arrow.setOnClickListener(this);
        medicine_image.setOnClickListener(this);
        create_medicine.setOnClickListener(this);
        back_arrow.setOnClickListener(this);
    }

    void initializeRecyclerView(ScheduleModel scheduleModel) {
        mAdapter = new MedicationFrequencyAdapter(this, scheduleModel);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    public void loadImagefromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.add_medicine_schedule_duration_arrow):
                Intent intent = new Intent(getApplicationContext(), DosagesScheduleActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCHEDULE);
                break;
            case (R.id.add_medicine_image):
                loadImagefromGallery(view);
                break;
            case (R.id.add_medicine_back_):
                Intent i_back = new Intent(getApplicationContext(), CreateMedicineActivity.class);
                startActivity(i_back);
                break;
            case (R.id.add_medicine_button_create):
                med_name = medicine_name.getText().toString();
                medModel.name = med_name;
                medModel.dose = dosge;
           // medModel.setRecipientId(mRecipientModel.getServerId());
              medModel.setRecipientId("58491ab5b19f6960317d920c");
                createMedicine();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            medModel.medicinePicUrl = picturePath;
            cursor.close();

            MaskedImageView imageView = (MaskedImageView) findViewById(R.id.add_medicine_image);

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bmp);

        } else if (requestCode == REQUEST_CODE_SCHEDULE && resultCode == RESULT_OK) {
            if (null != data) {
                ScheduleModel scheduleModel = (ScheduleModel) data.getSerializableExtra(Constants.MODEL_SCHEDULE);
                medModel.setScheduleModel(scheduleModel);
                showDurationAndFrquency(scheduleModel);
            } else {
                Log.e(TAG, "Data cant be null");
            }
        }

        // community.picUrl = file.getAbsolutePath();

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void checkBoxVisibility(ScheduleModel scheduleModel) {
        if (scheduleModel.types.contains("Sun")) {
            sun.setVisibility(View.VISIBLE);
            sun.setChecked(true);
        } else {
            sun.setVisibility(View.GONE);
        }
        if (scheduleModel.types.contains("Mon")) {
            mon.setVisibility(View.VISIBLE);
            mon.setChecked(true);
        } else {
            mon.setVisibility(View.GONE);
        }
        if (scheduleModel.types.contains("Tue")) {
            tue.setVisibility(View.VISIBLE);
            tue.setChecked(true);
        } else {
            tue.setVisibility(View.GONE);
        }
        if (scheduleModel.types.contains("Wed")) {
            wed.setVisibility(View.VISIBLE);
            wed.setChecked(true);
        } else {
            wed.setVisibility(View.GONE);
        }
        if (scheduleModel.types.contains("Thu")) {
            thu.setVisibility(View.VISIBLE);
            thu.setChecked(true);
        } else {
            thu.setVisibility(View.GONE);
        }
        if (scheduleModel.types.contains("Fri")) {
            fri.setVisibility(View.VISIBLE);
            fri.setChecked(true);
        } else {
            fri.setVisibility(View.GONE);
        }
        if (scheduleModel.types.contains("Sat")) {
            sat.setVisibility(View.VISIBLE);
            sat.setChecked(true);
        } else {
            sat.setVisibility(View.GONE);
        }
    }

    public void createMedicine() {
        if (isNetworkAvailable()) {
            showProgress();
            if (picturePath == null) {
                create();
            } else {
                startLoadAttachFile(new File(medModel.medicinePicUrl));
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }
    }

    protected void startLoadAttachFile(final File file) {
        CloudinaryImageUploadCommand.start(this, file);
    }

    private void create() {
        medicineService.createMedicine(medModel, new AsyncResult<MedicineModel>() {
            @Override
            public void success(MedicineModel communityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Intent intent = new Intent(getApplicationContext(),CreateMedicineActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Medicine saved", Toast.LENGTH_SHORT).show();

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


    public class CloudinaryUploadSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            CloudinaryModel model = (CloudinaryModel) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL);
            if (model != null) {
                medModel.medicinePicUrl = model.url;
            }
            create();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        addActions();
    }

    void showDurationAndFrquency(ScheduleModel scheduleModel) {
        if (null == scheduleModel) {
            linearLayout.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            checkBoxVisibility(scheduleModel);
            initializeRecyclerView(scheduleModel);
        }

    }
    protected void addActions() {
        addAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_SUCCESS_ACTION, cloudinaryUploadSuccessAction);
        addAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }
}
