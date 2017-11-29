package com.trutek.looped.ui.communityDashboard.maindashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ActivityModel> activities;

    public RecentActivityAdapter(Context context, ArrayList<ActivityModel> notificationList) {
        this.context = context;
        activities = notificationList;
    }

    @Override
    public RecentActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActivityModel model = activities.get(position);

        holder.text_sender_name.setText(model.subject);
        holder.text_message.setText(model.body);

        if(model.picUrl != null && !model.picUrl.isEmpty() && model.picUrl.contains("http")){
            displayImageByUrl(model.picUrl, holder);
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.sender_image.setImageBitmap(image);
        }

    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.sender_image,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    public void addItem(ActivityModel model){
        activities.add(model);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_sender_name,text_message;
        ImageView image_icon;
        MaskedImageView sender_image;
        public ViewHolder(View itemView) {
            super(itemView);
            text_sender_name=(TextView)itemView.findViewById(R.id.list_community_dashboard_sender_name);
            text_message=(TextView)itemView.findViewById(R.id.list_community_dashboard_message);
            image_icon=(ImageView)itemView.findViewById(R.id.community_dashboard_image_icon);
            sender_image = (MaskedImageView) itemView.findViewById(R.id.list_community_dashboard_dashboard_image);
            Typeface avenirNextRegular=Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            text_sender_name.setTypeface(avenirNextRegular);
            text_message.setTypeface(avenirNextRegular);
        }
    }
}