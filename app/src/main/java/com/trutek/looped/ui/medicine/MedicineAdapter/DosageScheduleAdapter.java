package com.trutek.looped.ui.medicine.MedicineAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.models.ScheduleModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rinki on 12/1/2016.
 */
public class DosageScheduleAdapter extends RecyclerView.Adapter<DosageScheduleAdapter.ViewHolder> {
    private ScheduleModel mDataset;
    private Context context;
    Date date;

    public DosageScheduleAdapter(Context context,ScheduleModel mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView selected_time;
        public ImageView dellete_Item;

         public ViewHolder(View v) {
            super(v);
            selected_time = (TextView) v.findViewById(R.id.item_schedule_medicine_textView_time);
            dellete_Item = (ImageView) v.findViewById(R.id.item_schedule_medicine_imageView_deleteItem);
        }
    }

   /*public void add(int position, ScheduleModel item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }*/
  public void remove(Date item) { //removes the row
        mDataset.getTimings().remove(item);
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_medicine, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
         date = mDataset.getTimings().get(position);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String time=dateFormat.format(date);
        holder.selected_time.setText(time);
        holder.dellete_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(date);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.getTimings().size();
    }

}
