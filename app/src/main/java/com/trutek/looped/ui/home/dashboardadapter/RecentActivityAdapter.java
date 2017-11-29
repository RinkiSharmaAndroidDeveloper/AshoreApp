package com.trutek.looped.ui.home.dashboardadapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private OnActionListener<ActivityModel> openCommunityListener;
    private OnActionListener<ActivityModel> openActivityListener;
    private ArrayList<ActivityModel> activityModels;

    public RecentActivityAdapter(ArrayList<ActivityModel> activityModels, OnActionListener<ActivityModel> openCommunityListener,
                                 OnActionListener<ActivityModel> openActivityListener) {
        this.activityModels = activityModels;
        this.openCommunityListener = openCommunityListener;
        this.openActivityListener = openActivityListener;
    }

    @Override
    public RecentActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_recent_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ActivityModel model = activityModels.get(position);
        String recentActivityTime;
        holder.layout.setVisibility(View.VISIBLE);
        holder.activity.setText(model.subject);
        if(model.lastUpdate != null){
            holder.description.setText(model.lastUpdate.content);
        }
        recentActivityTime=DateHelper.stringifyTime(model.timeStamp);
        holder.recentActivityTime.setText(recentActivityTime);
        if(model.picUrl != null && !model.picUrl.isEmpty() && model.picUrl.contains("http")){
            displayImageByUrl(model.picUrl, holder);
        } else {
            switch (position % 3){
                case 0:
                    holder.recentActivityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
                case 1:
                    holder.recentActivityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_third));
                    break;
                case 2:
                    holder.recentActivityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_forth));
                    break;
                default:
                    holder.recentActivityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
            }
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityListener.notify(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityModels.size();
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.recentActivityImage,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView activity, activityTwo, activityThree, description, descriptionTwo, descriptionThree,recentActivityTime;
        LinearLayout headerLayout, layout, layoutTwo, layoutThree;
        MaskedImageView recentActivityImage;

        public ViewHolder(View itemView) {
            super(itemView);
            activity = (TextView)itemView.findViewById(R.id.txt_activity_name);
            description = (TextView)itemView.findViewById(R.id.txt_activity_description);
            activityTwo = (TextView)itemView.findViewById(R.id.txt_activity_name_two);
            descriptionTwo = (TextView)itemView.findViewById(R.id.txt_activity_description_two);
            activityThree = (TextView)itemView.findViewById(R.id.txt_activity_name_three);
            descriptionThree = (TextView)itemView.findViewById(R.id.txt_activity_description_three);
            recentActivityTime = (TextView)itemView.findViewById(R.id.textView_recent_time);
            recentActivityImage = (MaskedImageView) itemView.findViewById(R.id.recent_activities_image);

            headerLayout = (LinearLayout) itemView.findViewById(R.id.header_layout);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_item_first);
            layoutTwo = (LinearLayout) itemView.findViewById(R.id.layout_item_second);
            layoutThree = (LinearLayout) itemView.findViewById(R.id.layout_item_third);

            Typeface avenirNextRegular=Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            activity.setTypeface(avenirNextRegular);
            activityTwo.setTypeface(avenirNextRegular);
            activityThree.setTypeface(avenirNextRegular);
            description.setTypeface(avenirNextRegular);
            descriptionTwo.setTypeface(avenirNextRegular);
            descriptionThree.setTypeface(avenirNextRegular);

        }
    }
}
