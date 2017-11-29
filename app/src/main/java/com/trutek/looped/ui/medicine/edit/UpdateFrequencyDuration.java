package com.trutek.looped.ui.medicine.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.models.ScheduleModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.medicine.MedicineAdapter.DosageScheduleAdapter;
import com.trutek.looped.ui.medicine.create.AddMedicineActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rinki on 12/13/2016.
 */
public class UpdateFrequencyDuration extends BaseAppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private static int RESULT_LOAD_FRAGMENT = 2;
    public static final String EXTRA_DATA = "EXTRA_DATA";
    ImageView submit, back_arrow;
    TimePicker simpleTimePicker;
    String AM_PM;
    private RecyclerView frequency_recycler;
    private RecyclerView.Adapter mAdapter;
    ArrayList<MedicineModel> item_list;
    String time;
    TextView duration_ok, duration_cancel;
    ScheduleModel duration;
    ScheduleModel mScheduleModel;
    List<String> duplicateElement = new ArrayList<>();
    List<String> selectedElement = new ArrayList<>();
    CheckBox sun, mon, tue, wed, thu, fri, sat;
    DateHelper dateHelper;
    Date date;
    int Hours, Minutes;

    @Override
    protected int getContentResId() {
        return R.layout.activity_schedule;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mScheduleModel = new ScheduleModel();
        simpleTimePicker = (TimePicker) findViewById(R.id.activity_schedule_simpleTimePicker);
        duration_ok = (TextView) findViewById(R.id.activity_schedule_ok);
        submit = (ImageView) findViewById(R.id.scedule_image_done);
        back_arrow = (ImageView) findViewById(R.id.schedule_back_arrow);
        duration_cancel = (TextView) findViewById(R.id.activity_schedule_cancel);
        frequency_recycler = (RecyclerView) findViewById(R.id.activity_schedule_recycler_medicine_dosage);
        sun = (CheckBox) findViewById(R.id.schedule_frequency_sun);
        mon = (CheckBox) findViewById(R.id.schedule_frequency_mon);
        tue = (CheckBox) findViewById(R.id.schedule_frequency_tue);
        wed = (CheckBox) findViewById(R.id.schedule_frequency_wed);
        thu = (CheckBox) findViewById(R.id.schedule_frequency_thu);
        fri = (CheckBox) findViewById(R.id.schedule_frequency_fri);
        sat = (CheckBox) findViewById(R.id.schedule_frequency_sat);

        item_list = new ArrayList<>();
        duration = new ScheduleModel();
        dateHelper = new DateHelper();
        simpleTimePicker.setIs24HourView(false);
        simpleTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Hours = hourOfDay;
                Minutes = minute;
                if (hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
                date = dateHelper.convertToDate(Hours, Minutes);
            }

        });

        // loadFragment();

        duration_ok.setOnClickListener(this);
        duration_cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
        back_arrow.setOnClickListener(this);
    }




    void initializeRecyclerView() {
        duration.timings.add(date);
        mAdapter = new DosageScheduleAdapter(this, duration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        frequency_recycler.setLayoutManager(layoutManager);
        frequency_recycler.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // frequency_recycler.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.activity_schedule_ok):
                initializeRecyclerView();
                mAdapter.notifyDataSetChanged();
                break;
            case (R.id.activity_schedule_cancel):
                Intent i_cancel = new Intent(getApplicationContext(), EditMedicineActivity.class);
                i_cancel.putExtra("layoutVisible", "0");
                startActivity(i_cancel);
                break;
            case (R.id.scedule_image_done):
                Intent intent = new Intent();
                intent.putExtra(Constants.MODEL_SCHEDULE, duration);
                setResult(Activity.RESULT_OK, intent);
                removeDupicateEleemnt();
                finish();

                break;
            case (R.id.schedule_back_arrow):
                Intent i = new Intent(getApplicationContext(), EditMedicineActivity.class);
                i.putExtra("layoutVisible", "0");
                startActivity(i);
                break;
        }
    }

    public void onCheckboxClicked(View view) {

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.schedule_frequency_sun:
                if (checked) {

                    selectedElement.add("Sun");

                } else {
                    duplicateElement.add("Sun");
                }
                break;
            case R.id.schedule_frequency_mon:
                if (checked) {

                    selectedElement.add("Mon");

                } else {
                    duplicateElement.add("Sun");
                }
                break;
            case R.id.schedule_frequency_tue:
                if (checked) {
                    selectedElement.add("Tue");

                } else {
                    duplicateElement.add("Tue");
                }
                break;
            case R.id.schedule_frequency_wed:
                if (checked) {
                    selectedElement.add("Wed");

                } else {
                    duplicateElement.add("Wed");
                }
                break;
            case R.id.schedule_frequency_thu:
                if (checked) {
                    selectedElement.add("Thu");
                } else {
                    duplicateElement.add("Thu");
                }
                break;
            case R.id.schedule_frequency_fri:
                if (checked) {
                    selectedElement.add("Fri");

                } else {
                    duplicateElement.add("Fri");
                }
                break;
            case R.id.schedule_frequency_sat:
                if (checked) {
                    selectedElement.add("Sat");
                } else {
                    duplicateElement.add("Sat");
                }
                break;
        }
    }

    public void removeDupicateEleemnt() {
        for (String bigList : selectedElement) {
            boolean found = false;
            for (String smallList : duplicateElement) {
                if ((bigList.equals(smallList))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                duration.types.add(bigList);
            }
        }
    }


}