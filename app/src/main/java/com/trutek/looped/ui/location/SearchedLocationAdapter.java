package com.trutek.looped.ui.location;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;

import java.util.ArrayList;

/**
 * Created by Amrit on 20/01/17.
 */
public class SearchedLocationAdapter extends RecyclerView.Adapter<SearchedLocationAdapter.ViewHolder> {

    private ArrayList<LocationModel> locationModels;
    private AsyncResult<LocationModel> notify;
    Typeface avenirNextregular;
    public SearchedLocationAdapter(ArrayList<LocationModel> locationModels, AsyncResult<LocationModel> notify) {

        this.locationModels = locationModels;
        this.notify = notify;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_location,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int info1_color_active,info1_color_deactive,info2_color_active,info2_color_auto_active,info2_color_deactive;
        info1_color_active = holder.itemView.getResources().getColor(R.color.sla_info1_active_color);
        info1_color_deactive = holder.itemView.getResources().getColor(R.color.sla_info1_deactive_color);
        info2_color_active = holder.itemView.getResources().getColor(R.color.sla_info2_active_color);
        info2_color_auto_active = holder.itemView.getResources().getColor(R.color.sla_info2_auto_location_active_color);
        info2_color_deactive = holder.itemView.getResources().getColor(R.color.sla_info2_deactive_color);
        final LocationModel locationModel = locationModels.get(position);
        avenirNextregular = Typeface.createFromAsset(holder.itemView.getContext().getAssets(),Constants.AvenirNextRegular);

        holder.textView_info2.setTypeface(avenirNextregular);
        holder.textView_info1.setTypeface(avenirNextregular);
        String locationName;
        if(null != locationModel.getCountry()) {
            locationName = locationModel.getName();
        }else {
            locationName = locationModel.getName();
        }
        holder.textView_info2.setText(locationName);

        if(locationModel.isAutoDetect()){
            holder.imageView_icon.setVisibility(View.VISIBLE);
            holder.textView_info1.setVisibility(View.VISIBLE);

            if(locationModel.getName().equals(holder.itemView.getResources().getString(R.string.sla_locationNotDetected))){
                holder.textView_info1.setTextColor(info1_color_deactive);
                holder.textView_info2.setTextColor(info2_color_deactive);
            }else{
                holder.textView_info1.setTextColor(info1_color_active);
                holder.textView_info2.setTextColor(info2_color_auto_active);
            }

        }else{

            holder.imageView_icon.setVisibility(View.INVISIBLE);
            holder.textView_info1.setVisibility(View.GONE);
            holder.textView_info2.setTextColor(info2_color_active);
        }
        holder.textView_info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify.success(locationModel);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify.success(locationModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView_icon;
        TextView textView_info1,textView_info2;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView_icon = (ImageView) itemView.findViewById(R.id.item_searchLocation_imageView_location);
            textView_info1 = (TextView) itemView.findViewById(R.id.item_searchLocation_textView_location_text);
            textView_info2 = (TextView) itemView.findViewById(R.id.item_searchLocation_textView_location_value);
        }
    }
}
