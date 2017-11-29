package com.trutek.looped.ui.medicine.edit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.models.ScheduleModel;
import com.trutek.looped.data.contracts.services.IMedicineService;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.medicine.MedicineAdapter.EditFreqencyMedication;
import com.trutek.looped.ui.medicine.MedicineAdapter.MedicationAdapter;
import com.trutek.looped.ui.medicine.MedicineAdapter.MedicationFrequencyAdapter;
import com.trutek.looped.ui.medicine.create.AddMedicineActivity;
import com.trutek.looped.ui.medicine.create.CreateMedicineActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Rinki on 11/29/2016.
 */
public class EditMedicineActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private static int RESULT_LOAD_IMAGE = 2;
    private static final int REQUEST_CODE_SCHEDULE = 1;
    /*EditMedicine*/
    @Inject
    IMedicineService medicineService;
    ImageView backArrow, edit_icon, delete_icon,dosage_frequency,dosage_duration;
    MaskedImageView medicine_image;
    private AddMedicineActivity.CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    EditText medicine_name_edit;
    TextView medicine_name_text, unselect_dosages;
    Spinner dosage;
    Button done_button;
    RecyclerView recyclerView;
    MedicineModel medicineModel;
    EditFreqencyMedication mAdapter;
    private RecyclerView.Adapter mAdapter1;
    String picturePath;
    RecipientModel mRecipientModel;
    MedicineModel medModel;
    String text;
    CheckBox sun, mon, tue, wed, thu, fri, sat;

    @Override
    protected int getContentResId() {
        return R.layout.activity_edit_medicine;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backArrow = (ImageView) findViewById(R.id.edit_medicine_back_arrow);
        dosage_frequency = (ImageView) findViewById(R.id.add_medicine_imageView);
        dosage_duration = (ImageView) findViewById(R.id.edit_imageView_duration);
        delete_icon = (ImageView) findViewById(R.id.delete_medicine_image);
        edit_icon = (ImageView) findViewById(R.id.Edit_medicine_image);
        medicine_image = (MaskedImageView) findViewById(R.id.edit_medicine_medicine_image);
        medicine_name_edit = (EditText) findViewById(R.id.edit_medicine_editText_medicinename);
        medicine_name_text = (TextView) findViewById(R.id.edit_medicine_textView_medicinename);
        unselect_dosages = (TextView) findViewById(R.id.edit_medicine_textView_dosage);
        dosage = (Spinner) findViewById(R.id.edit_medicine_spinner_dosage);
        done_button = (Button) findViewById(R.id.edit_medicine_nutton_done);
        recyclerView = (RecyclerView) findViewById(R.id.edit_medicine_recycler_medicine_dosage);
        sun = (CheckBox) findViewById(R.id.schedule_frequency_sun);
        mon = (CheckBox) findViewById(R.id.schedule_frequency_mon);
        tue = (CheckBox) findViewById(R.id.schedule_frequency_tue);
        wed = (CheckBox) findViewById(R.id.schedule_frequency_wed);
        thu = (CheckBox) findViewById(R.id.schedule_frequency_thu);
        fri = (CheckBox) findViewById(R.id.schedule_frequency_fri);
        sat = (CheckBox) findViewById(R.id.schedule_frequency_sat);
        medicineModel = (MedicineModel) getIntent().getSerializableExtra("medicineModel");
        setDataOnUi();
        mRecipientModel = (RecipientModel) getIntent().getSerializableExtra(Constants.MODEL_RECIPIENT);

        edit_icon.setOnClickListener(this);
        delete_icon.setOnClickListener(this);
        dosage_frequency.setOnClickListener(this);
        dosage_duration.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        done_button.setOnClickListener(this);
        medicine_image.setOnClickListener(this);
    }
    private void initAdapter() {
        mAdapter = new EditFreqencyMedication(this, medicineModel);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
    void initializeRecyclerView(ScheduleModel scheduleModel) {
        mAdapter1 = new MedicationFrequencyAdapter(this, scheduleModel);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter1);
    }

    private void setDataOnUi() {
        if (medicineModel.getMedicinePicUrl() != null && !medicineModel.getMedicinePicUrl().isEmpty() && medicineModel.getMedicinePicUrl().contains("http")) {
            displayImageByUrl(medicineModel.getMedicinePicUrl(), medicine_image);
        } else {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.default_camera);
            medicine_image.setImageBitmap(image);
        }
        medModel =new MedicineModel();
        checkBoxVisibilityDuration();
        initAdapter();

        medicine_name_text.setText(medicineModel.getName());
        medicine_name_edit.setText(medicineModel.getName());
        medicine_name_edit.setVisibility(View.GONE);
        unselect_dosages.setText(medicineModel.getDose());
        dosage.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        edit_icon.setVisibility(View.VISIBLE);
        //dosage.setAdapter(medicineModel.getDose());
    }
    void displayUiOnEdit() {

        medicine_name_text.setVisibility(View.GONE);
        medicine_name_edit.setVisibility(View.VISIBLE);
        unselect_dosages.setVisibility(View.GONE);
        dosage.setVisibility(View.VISIBLE);
        done_button.setVisibility(View.VISIBLE);
        delete_icon.setVisibility(View.VISIBLE);
        edit_icon.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getApplicationContext(),
                R.array.Medicine_Dosage_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dosage.setAdapter(adapter);
        dosage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 text = parent.getItemAtPosition(position).toString();
                ((TextView) dosage.getSelectedView()).setTextColor(getResources().getColor(R.color.dark_gray));

            }
            // model.setDose(text);
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void displayImageByUrl(String publicUrl, MaskedImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }
    public void loadImagefromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

    }


    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.edit_medicine_back_arrow):
                Intent i = new Intent(this, CreateMedicineActivity.class);
                startActivity(i);
                break;
            case (R.id.Edit_medicine_image):
                displayUiOnEdit();

                break;
            case (R.id.add_medicine_image):
                loadImagefromGallery(view);
                break;
            case (R.id.delete_medicine_image):
                getMedicine();
                Intent intent=new Intent(this,CreateMedicineActivity.class);
                startActivity(intent);
                break;
            case (R.id.edit_medicine_nutton_done):
               String med_name = medicine_name_edit.getText().toString();
                medModel.name = med_name;
                medModel.dose = text;
//                medModel.setRecipientId(mRecipientModel.getServerId());
                medModel.setMedID(medicineModel.getSchedules().get(0).medicationId);
                createMedicine();
                break;
            case (R.id.add_medicine_imageView):
                Intent intent1=new Intent(this,UpdateFrequencyDuration.class);
                startActivityForResult(intent1, REQUEST_CODE_SCHEDULE);
                break;
            case (R.id.edit_imageView_duration):
                Intent intent2=new Intent(this,UpdateFrequencyDuration.class);
                startActivityForResult(intent2, REQUEST_CODE_SCHEDULE);
                break;
        }

    }

    public void getMedicine() {
        medicineService.deleteMedicine(medicineModel, new AsyncNotify() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("Delete Success");
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

    private void checkBoxVisibilityDuration() {
        int i=0,sizeSchedule;
        sizeSchedule=medicineModel.getSchedules().size();
        if(i<sizeSchedule) {
            String scheduleDay=medicineModel.getSchedules().get(i).scheduleType;
            if (scheduleDay.contains("Sun")) {
                sun.setVisibility(View.VISIBLE);
                sun.setChecked(true);
            } else {
                sun.setVisibility(View.GONE);
            }
            if (scheduleDay.contains("Mon")) {
                mon.setVisibility(View.VISIBLE);
                mon.setChecked(true);
            } else {
                mon.setVisibility(View.GONE);
            }
            if (scheduleDay.contains("Tue")) {
                tue.setVisibility(View.VISIBLE);
                tue.setChecked(true);
            } else {
                tue.setVisibility(View.GONE);
            }
            if (scheduleDay.contains("Wed")) {
                wed.setVisibility(View.VISIBLE);
                wed.setChecked(true);
            } else {
                wed.setVisibility(View.GONE);
            }
            if (scheduleDay.contains("Thu")) {
                thu.setVisibility(View.VISIBLE);
                thu.setChecked(true);
            } else {
                thu.setVisibility(View.GONE);
            }
            if (scheduleDay.contains("Fri")) {
                fri.setVisibility(View.VISIBLE);
                fri.setChecked(true);
            } else {
                fri.setVisibility(View.GONE);
            }
            if (scheduleDay.contains("Sat")) {
                sat.setVisibility(View.VISIBLE);
                sat.setChecked(true);
            } else {
                sat.setVisibility(View.GONE);
            }
        }
        i++;
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

            }
        }
    }

    void showDurationAndFrquency(ScheduleModel scheduleModel) {
        if (null == scheduleModel) {
            checkBoxVisibilityDuration();
            initAdapter();
        } else {
            checkBoxVisibility(scheduleModel);
            initializeRecyclerView(scheduleModel);

        }

    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    public void createMedicine() {
        if (isNetworkAvailable()) {
            showProgress();
            if (picturePath == null) {
                update();
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

    private void update() {
        medicineService.updateMedicine(medModel, new AsyncResult<MedicineModel>() {
            @Override
            public void success(MedicineModel communityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Intent intent = new Intent(getApplicationContext(),CreateMedicineActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Medicine updated", Toast.LENGTH_SHORT).show();

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
    public class CloudinaryUploadSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            CloudinaryModel model = (CloudinaryModel) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL);
            if (model != null) {
                medModel.medicinePicUrl = model.url;
            }
            update();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        addActions();
    }

    protected void addActions() {
        addAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_SUCCESS_ACTION, cloudinaryUploadSuccessAction);
        addAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

}
