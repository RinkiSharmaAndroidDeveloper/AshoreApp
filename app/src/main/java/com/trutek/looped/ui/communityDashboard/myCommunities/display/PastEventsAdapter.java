package com.trutek.looped.ui.communityDashboard.myCommunities.display;

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
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.List;

/**
 * Created by Rinki on 3/23/2017.
 */
public class PastEventsAdapter extends RecyclerView.Adapter<PastEventsAdapter.ViewHolder> {
    List<ActivityModel> pastEventList;
    ActivityModel activityModel;
    OnActionListener<ActivityModel> activitySelectedActionListeners;

    public PastEventsAdapter(List<ActivityModel> mPastEventList,OnActionListener<ActivityModel> mActivitySelectedActionListeners) {
        this.pastEventList = mPastEventList;
        this.activitySelectedActionListeners=mActivitySelectedActionListeners;
    }


    @Override
    public PastEventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_past_events, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PastEventsAdapter.ViewHolder holder, int position) {
        activityModel = pastEventList.get(position);
        holder.textView_agenda_name.setText(activityModel.getSubject());
        holder.textView_agenda_loation.setText(activityModel.location.name);
        if (null != activityModel.getPicUrl() && activityModel.getPicUrl().contains("http")) {
            displayImageByUrl(activityModel.picUrl, holder);
        } else {
            switch (position % 3) {
                case 0:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
                case 1:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_third));
                    break;
                case 2:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_forth));
                    break;
                default:
                    holder.maskedImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
            }
            holder.maskedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activitySelectedActionListeners.notify(activityModel);
                }
            });

        }
    }


        @Override
        public int getItemCount () {
            return pastEventList.size();
        }


    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_agenda_name, textView_agenda_loation, textView_agenda_time;
        ImageView image_icon;
        MaskedImageView maskedImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_agenda_name = (TextView) itemView.findViewById(R.id.list_agenda_textView_agenda_name);
            textView_agenda_loation = (TextView) itemView.findViewById(R.id.list_agenda_location);
            textView_agenda_time = (TextView) itemView.findViewById(R.id.list_agenda_timing);
            image_icon = (ImageView) itemView.findViewById(R.id.image_icon_agenda);
            maskedImageView = (MaskedImageView) itemView.findViewById(R.id.list_agenda_imageView_agendaPic);
            Typeface avenirNextRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            Typeface avenirNextBold = Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextBold);
            textView_agenda_name.setTypeface(avenirNextRegular);
            textView_agenda_loation.setTypeface(avenirNextRegular);
            textView_agenda_time.setTypeface(avenirNextRegular);
        }
    }
}
