package com.trutek.looped.ui.home.dashboardadapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder> {

    private List<ActivityModel> mList;
    private OnActionListener<ActivityModel> activitySelectedActionListeners;

    public AgendaAdapter(List<ActivityModel> agendaList) {
        mList=agendaList;
    }

    public void SelectActivityOnClickImage(OnActionListener<ActivityModel> activitySelectedActionListeners) {
        this.activitySelectedActionListeners = activitySelectedActionListeners;
    }

    @Override
    public AgendaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_my_agenda,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ActivityModel model = mList.get(position);
        holder.textView_agenda_time.setText(model.subject);
        holder.textView_agenda_loation.setText(model.getLocation().name);
        if (model.picUrl != null && !model.picUrl.isEmpty() && model.picUrl.contains("http")) {
            displayImageByUrl(model.picUrl, holder);
        } else {
            switch (position % 3){
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
        }
//        holder.textView_agenda_time.setText(DateHelper.stringify(model.dueDate, "hh:mm"));
        if(model.dueDate!=null){
        holder.textView_agenda_name.setText(DateHelper.stringify(model.dueDate, "EEEE MMM, dd, hh:mm aa"));}
//        holder.text_agenda_convention.setText(DateHelper.stringify(model.dueDate, "aa"));
        holder.maskedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activitySelectedActionListeners.notify(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView_agenda_name,textView_agenda_loation,textView_agenda_time;
        ImageView image_icon;
        MaskedImageView maskedImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_agenda_name=(TextView)itemView.findViewById(R.id.list_agenda_textView_agenda_name);
            textView_agenda_loation=(TextView)itemView.findViewById(R.id.list_agenda_location);
            textView_agenda_time=(TextView)itemView.findViewById(R.id.list_agenda_timing);
            image_icon=(ImageView)itemView.findViewById(R.id.image_icon_agenda);
            maskedImageView=(MaskedImageView) itemView.findViewById(R.id.list_agenda_imageView_agendaPic);
            Typeface avenirNextRegular=Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            Typeface avenirNextBold=Typeface.createFromAsset(itemView.getContext().getAssets(),Constants.AvenirNextBold);
            textView_agenda_name.setTypeface(avenirNextRegular);
            textView_agenda_loation.setTypeface(avenirNextRegular);
            textView_agenda_time.setTypeface(avenirNextRegular);
        }
    }
}
