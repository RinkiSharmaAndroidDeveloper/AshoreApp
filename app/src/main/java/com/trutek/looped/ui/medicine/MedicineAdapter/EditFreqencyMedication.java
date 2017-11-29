package com.trutek.looped.ui.medicine.MedicineAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.models.ScheduleModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rinki on 12/12/2016.
 */
public class EditFreqencyMedication extends RecyclerView.Adapter<EditFreqencyMedication.ViewHolder> {

    private MedicineModel mDataset;
    private Context context;


    public EditFreqencyMedication(Context context,MedicineModel mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dosages_time;


        public ViewHolder(View v) {
            super(v);
            dosages_time = (TextView) v.findViewById(R.id.item_medication_textView_dosages_time);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_add_medicine_frequency, viewGroup, false);
        ViewHolder rvi = new ViewHolder(v);
        return rvi;
    }

    @Override
    public void onBindViewHolder(ViewHolder childViewHolder, int i) {
        DateFormat dateFormat = new SimpleDateFormat("h:mm");
        String time = dateFormat.format(mDataset.getSchedules().get(i).getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDataset.getSchedules().get(i).getDate());
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        String AM_PM;
        if(hours<12){
            AM_PM ="am";
        }else{
            AM_PM ="pm";

        }
        childViewHolder.dosages_time.setText(time+AM_PM);
    }

    @Override
    public int getItemCount() {
        return mDataset.getSchedules().size();
    }


}
