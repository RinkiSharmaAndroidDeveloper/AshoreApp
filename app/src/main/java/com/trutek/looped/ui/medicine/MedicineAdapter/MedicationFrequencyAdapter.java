package com.trutek.looped.ui.medicine.MedicineAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.models.ScheduleModel;
import com.trutek.looped.ui.medicine.edit.EditMedicineActivity;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rinki on 12/7/2016.
 */
public class MedicationFrequencyAdapter extends RecyclerView.Adapter<MedicationFrequencyAdapter.ViewHolder> {

    private ScheduleModel mDataset;
    private Context context;
    Date date;

    public MedicationFrequencyAdapter(Context context,ScheduleModel mDataset) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_medicine_frequency, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        date = mDataset.getTimings().get(position);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String time=dateFormat.format(date);
        holder.dosages_time.setText(time);

    }

    @Override
    public int getItemCount() {
        return mDataset.getTimings().size();
    }

}
