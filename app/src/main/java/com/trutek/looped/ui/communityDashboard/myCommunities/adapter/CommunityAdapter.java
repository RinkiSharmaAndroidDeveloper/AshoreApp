package com.trutek.looped.ui.communityDashboard.myCommunities.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommunityModel> communityList;
    OnActionListener<CommunityModel> joinClickActionListeners,openCommunityClickActionListeners;
    int clickedItemPosition = -1;
    Boolean isJoinButtonActive;

    public CommunityAdapter(Context context, ArrayList<CommunityModel> mCommunityList,OnActionListener<CommunityModel> mjoinClickActionListeners,Boolean joinButtonActive,OnActionListener<CommunityModel> mOpenCommunityClickActionListeners) {
        this.context = context;
        this.communityList=mCommunityList;
        this.joinClickActionListeners=mjoinClickActionListeners;
        this.isJoinButtonActive = joinButtonActive;
        this.openCommunityClickActionListeners=mOpenCommunityClickActionListeners;
    }
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_community_discover,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CommunityModel community = communityList.get(position);
        holder.text_community_name.setText(community.subject);
        String info;
        if (community.getFriendsCount() == 0|| community.getFriendsCount() != 1) {
            if (community.members.size() == 0 || community.members.size() != 1) {
                info = community.getFriendsCount() + " friends - " + community.getMembersCount() + " members";
            } else {
                info = community.getFriendsCount() + " friends - " + community.getMembersCount() + " member";
            }
        } else {

            if (community.members.size() == 0 || community.members.size() != 1) {
                info = community.getFriendsCount() + " friend - " + community.getMembersCount() + " members";
            } else {
                info = community.getFriendsCount() + " friend - " + community.getMembersCount() + " member";
            }

        }
        holder.text_community_type.setText(info);
        holder.textView_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickedItemPosition(position);
                joinClickActionListeners.notify(community);
            }
        });
        holder.linearLayout_openCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickedItemPosition(position);
                openCommunityClickActionListeners.notify(community);
            }
        });

        if (community.picUrl != null && !community.picUrl.isEmpty() && community.picUrl.contains("http")) {
            displayImageByUrl(community.picUrl, holder);
        } else {
            displayImageByUrl("", holder);
            switch (position % 3) {
                case 0:
                    holder.community_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
                case 1:
                    holder.community_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_third));
                    break;
                case 2:
                    holder.community_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_forth));
                    break;
                default:
                    holder.community_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
            /*Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.community_image.setImageBitmap(image);*/
            }
        }
    }
    @Override
    public int getItemCount() {
        return communityList.size();
    }
    public int getClickedItemPosition() {
        return clickedItemPosition;
    }
    public void setClickedItemPosition(int clickedItemPosition) {
        this.clickedItemPosition = clickedItemPosition;
    }

    public void addItem(CommunityModel model){
        communityList.add(model);
    }
    public void removeItem(int position){
      communityList.remove(position);
      //  mDataset.remove(position);
       /* notifyItemRemoved(position);
        notifyItemRangeChanged(position, communityList.size());*/
        notifyItemChanged(position);
    }

    public void setModified() {
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text_community_name,text_community_type;
        ImageView right_icon_image;
        MaskedImageView community_image;
        Button textView_join;

        LinearLayout linearLayout_info, linearLayout_join,linearLayout_openCommunity;

        public ViewHolder(View itemView) {
            super(itemView);
            text_community_name=(TextView)itemView.findViewById(R.id.item_my_community_discover_textView_name);
            text_community_type=(TextView)itemView.findViewById(R.id.item_my_community_discover_textView_info);
            community_image=(MaskedImageView)itemView.findViewById(R.id.item_my_community_discover_imageView_image);
            textView_join = (Button) itemView.findViewById(R.id.item_my_community_discover_textView_join);

            linearLayout_info = (LinearLayout) itemView.findViewById(R.id.item_my_community_discover_linearLayout_info);
            linearLayout_join = (LinearLayout) itemView.findViewById(R.id.item_my_community_discover_linearLayout_join);
            linearLayout_openCommunity = (LinearLayout) itemView.findViewById(R.id.linearlayout_open_community);
//            right_icon_image=(ImageView)itemView.findViewById(R.id.joined_go_next_icon);

            Typeface avenirNextRegular=Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            text_community_type.setTypeface(avenirNextRegular);
            text_community_name.setTypeface(avenirNextRegular);
            if(isJoinButtonActive){
                textView_join.setVisibility(View.GONE);
            }else{
                textView_join.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.community_image,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }
}
