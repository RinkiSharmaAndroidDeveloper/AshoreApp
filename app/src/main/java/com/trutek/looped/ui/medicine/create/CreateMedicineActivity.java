package com.trutek.looped.ui.medicine.create;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.services.IMedicineService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.medicine.MedicineAdapter.MedicationAdapter;
import com.trutek.looped.ui.medicine.MedicineAdapter.MedicationFrequencyAdapter;
import com.trutek.looped.ui.medicine.edit.EditMedicineActivity;

import com.trutek.looped.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;


/**
 * Created by Rinki on 11/29/2016.
 */
public class CreateMedicineActivity extends BaseAppCompatActivity implements View.OnClickListener {
    /*CreateMedicine*/
    @Inject
    IMedicineService medicineService;
    FloatingActionButton floatingActionButton;
    ImageView backword_arrow;
    TextView calendar_date;
    String formattedDate;
    Calendar c;
    SimpleDateFormat df;
    MedicineModel model;
    String id ="58491ab5b19f6960317d920c";
    MedicationAdapter mAdapter;
    RecyclerView recyclerView;
    boolean found = false;
    private ArrayList<MedicineModel> medicine,medicineToday;

    @Override
    protected int getContentResId() {
        return R.layout.activity_medication;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        backword_arrow =(ImageView)findViewById(R.id.medication_backword_image);
        calendar_date =(TextView)findViewById(R.id.medication_calendar_date);
        recyclerView =(RecyclerView) findViewById(R.id.medication_recycler_medicine_dosage);
        medicine = new ArrayList<>();
        medicineToday = new ArrayList<>();
        c = Calendar.getInstance();
        df = new SimpleDateFormat("MMM dd");
        formattedDate = df.format(c.getTime());
        calendar_date.setText("Today,"+formattedDate);

        initAdapter();
        getMedicine();
        floatingActionButton.setOnClickListener(this);
        backword_arrow.setOnClickListener(this);

    }
    public void removeDupicateEleemnt(ArrayList<MedicineModel> modelList) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        String weekDayName = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        for (int j = 0; j < modelList.size(); j++) {
            MedicineModel model = modelList.get(j);
            found = false;
            for (int i = 0; i < model.getSchedules().size(); i++) {
                if (weekDayName.equals(model.getSchedules().get(i).scheduleType)) {
                    found = true;
                    break;
                }else{
                    found = false;
                }
            }
            if (found==true) {
                medicineToday.add(modelList.get(j));
            }
        }
    }
    private void initAdapter() {
        mAdapter = new MedicationAdapter(this, medicineToday);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.fab):
                Intent intent= new Intent(getApplicationContext(), AddMedicineActivity.class);
                intent.putExtra("layoutVisible","0");
                startActivity(intent);
                break;
            case (R.id.medication_backword_image):
                /*Intent i= new Intent(getApplicationContext(), DisplayRecipientActivity.class);
                startActivity(i);*/
                break;


        }

    }
    public void getMedicine(){
        medicineService.getAllMedicins(id, new AsyncResult<Page<MedicineModel>>() {
            @Override
            public void success( final Page<MedicineModel> medicineModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        medicine.addAll(medicineModel.items);
                        removeDupicateEleemnt(medicine);
                        mAdapter.notifyDataSetChanged();
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
}
