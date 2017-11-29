package com.trutek.looped.ui.medicine.MedicineAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.ui.medicine.edit.EditMedicineActivity;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rinki on 12/2/2016.
 */
public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {

    private ArrayList<MedicineModel> mDataset;
    private Context context;
    MedicineModel model;

    public MedicationAdapter(Context context, ArrayList<MedicineModel> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView medicine_name, dosages_count, dosages_time;
        public ImageView medicine_image, edit_medicine_image;
        public RecyclerView recyclerViewChild;

        public ViewHolder(View v) {
            super(v);
            medicine_name = (TextView) v.findViewById(R.id.item_medication_textView_name);
            dosages_count = (TextView) v.findViewById(R.id.item_medication_textView_dosages);
            dosages_time = (TextView) v.findViewById(R.id.item_medication_textView_dosages_time);
            medicine_image = (ImageView) v.findViewById(R.id.item_medication_medicine_image);
            edit_medicine_image = (ImageView) v.findViewById(R.id.item_medication_edit_medicine);
            recyclerViewChild = (RecyclerView) itemView.findViewById(R.id.edit_medicine_recycler_medicine_dosage);
        }
    }


    public void add(int position, MedicineModel item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(MedicineModel item) {
        mDataset.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        model = mDataset.get(position);
        holder.medicine_name.setText(model.getName());
        holder.dosages_count.setText(model.getDose() + " Dosage/day");
        holder.recyclerViewChild.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerViewChild.setAdapter(new ChildAdapter(context, model));
        if (model.getMedicinePicUrl() != null && !model.getMedicinePicUrl().isEmpty() && model.getMedicinePicUrl().contains("http")) {
            displayImageByUrl(model.getMedicinePicUrl(), holder);
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_camera);
            holder.medicine_image.setImageBitmap(image);
        }
        holder.edit_medicine_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditMedicineActivity.class);
                i.putExtra("medicineModel", model);
                context.startActivity(i);
            }
        });
    }


    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.medicine_image,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private Context _inflater;
    MedicineModel model1;

    public ChildAdapter(Context inflater, MedicineModel model) {
        _inflater = inflater;
        model1 = model;

    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_add_medicine_frequency, viewGroup, false);
        ChildViewHolder rvi = new ChildViewHolder(v);
        return rvi;
    }

    @Override
    public void onBindViewHolder(ChildViewHolder childViewHolder, int i) {
        DateFormat dateFormat = new SimpleDateFormat("h:mm");
        String time = dateFormat.format(model1.getSchedules().get(i).getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(model1.getSchedules().get(i).getDate());
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        String AM_PM;
        if (hours < 12) {
            AM_PM = "am";
        } else {
            AM_PM = "pm";

        }
        childViewHolder.txtChildLine.setText(time + AM_PM);
    }

    @Override
    public int getItemCount() {
        return model1.getSchedules().size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView txtChildLine;

        public ChildViewHolder(View itemView) {
            super(itemView);
            txtChildLine = (TextView) itemView.findViewById(R.id.item_medication_textView_dosages_time);
        }
    }
}