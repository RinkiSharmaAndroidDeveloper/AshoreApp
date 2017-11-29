package com.trutek.looped.ui.communityDashboard.myCommunities.display;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    public ArrayList<ActivityModel> activities;

    public ActivityAdapter(ArrayList<ActivityModel> activities) {
        this.activities = activities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_activity, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ActivityModel activity = activities.get(position);

        if(null != activity.dueDate) {
            holder.textView_timing.setText(DateHelper.stringify(activity.dueDate, DateHelper.CUSTOM_FORMAT_MONTH));
        }
        holder.textView_name.setText(activity.subject);
        if (activity.picUrl != null && !activity.picUrl.isEmpty() && activity.picUrl.contains("http")) {
            displayImageByUrl(activity.picUrl, holder);
        } else {

            switch (position % 4) {
                case 0:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_first));
                    break;
                case 1:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
                case 2:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_third));
                    break;
                case 3:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_forth));
                    break;
                default:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_first));
                    break;
            }
        }
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_timing, textView_name;
        MaskedImageView maskedImageView;

        public ViewHolder(View view) {
            super(view);
            Typeface avenirRegular = Typeface.createFromAsset(view.getResources().getAssets(), Constants.AvenirNextRegular);
            textView_timing = (TextView) view.findViewById(R.id.item_community_timing);
            textView_name = (TextView) view.findViewById(R.id.item_community_textView_agenda_name);
            maskedImageView = (MaskedImageView) view.findViewById(R.id.item_community_imageView_agendaPic);

            textView_timing.setTypeface(avenirRegular);
            textView_name.setTypeface(avenirRegular);

        }
    }

}
