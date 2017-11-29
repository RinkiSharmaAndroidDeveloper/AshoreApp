package com.trutek.looped.ui.communityDashboard.myCommunities.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

/**
 * Created by Amrit on 30/01/17.
 */
public class MyCommunityAdapter extends RecyclerView.Adapter<MyCommunityAdapter.ViewHolder> {

    private ArrayList<CommunityModel> communityModels;
    private AsyncResult<CommunityModel> result;

    public MyCommunityAdapter(ArrayList<CommunityModel> communityModels, AsyncResult<CommunityModel> result) {
        this.communityModels = communityModels;
        this.result = result;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joined_community,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String activitesCount;
        CommunityModel communityModel = communityModels.get(position);
        if(communityModel.activities.size()>0) {
            activitesCount = String.valueOf(communityModel.activities.size());
            holder.button_count.setVisibility(View.VISIBLE);
            holder.button_count.setText(activitesCount);
        }else{
          //  holder.button_count.setVisibility(View.VISIBLE);
          //  holder.button_count.setText("10");
        }
        holder.textView_name.setText(communityModel.getSubject());
        if(null!= communityModel.getLocation()) {
            holder.textView_location.setText(communityModel.getLocation().getName());
        }

        if(null!=communityModel.getPicUrl() && communityModel.getPicUrl().contains("http")){
            displayImageByUrl(communityModel.picUrl,holder);
        }else{
            switch (position % 3){
                case 0:
                    holder.imageView_communityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
                case 1:
                    holder.imageView_communityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_third));
                    break;
                case 2:
                    holder.imageView_communityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_forth));
                    break;
                default:
                    holder.imageView_communityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return communityModels.size();
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.imageView_communityImage,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView_communityImage;
        TextView textView_name,textView_location,button_count;
        Typeface avenirNextRegular;
        public ViewHolder(View itemView) {
            super(itemView);
            button_count = (TextView) itemView.findViewById(R.id.joined_community_count_button_text);
            avenirNextRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            imageView_communityImage = (ImageView) itemView.findViewById(R.id.item_joined_community_imageView_image);
            textView_name = (TextView) itemView.findViewById(R.id.item_joined_community_textView_name);
            textView_location = (TextView) itemView.findViewById(R.id.item_joined_community_textView_location);

            textView_name.setTypeface(avenirNextRegular);
            textView_location.setTypeface(avenirNextRegular);

            textView_location.setText("");
        }
    }
}
