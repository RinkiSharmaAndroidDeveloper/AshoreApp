package com.trutek.looped.ui.communityDashboard.discoverCommunity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

public class DiscoverCommunityAdapter extends RecyclerView.Adapter<DiscoverCommunityAdapter.ViewHolder>{

    Context context;

    private ArrayList<CommunityModel> communities;
    private OnActionListener<CommunityModel> onActionListener;
    private OnActionListener<CommunityModel> onJoinSelectListener;
    private ArrayList<CommunityModel> filteredCommunities;
    private DiscoverCommunityFilter mFilter;
    int clickedItemPosition = -1;

    public DiscoverCommunityAdapter(ArrayList<CommunityModel> communities,
                                    OnActionListener<CommunityModel> communitySelectedActionListeners,
                                    OnActionListener<CommunityModel> communityJoinSelectedActionListeners,
                                    Context context,
                                    ArrayList<CommunityModel> filteredCommunities) {
        this.context=context;
        this.communities = communities;
        this.onActionListener = communitySelectedActionListeners;
        this.onJoinSelectListener = communityJoinSelectedActionListeners;
        this.filteredCommunities = filteredCommunities;
        mFilter = new DiscoverCommunityFilter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_discover_communities, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CommunityModel community = filteredCommunities.get(position);
        holder.name.setText(community.subject);

        if(community.picUrl != null && !community.picUrl.isEmpty() && community.picUrl.contains("http")){
            displayImageByUrl(community.picUrl, holder);
        } else {
            switch (position % 3){
                case 0:
                    holder.communityPic.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
                case 1:
                    holder.communityPic.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_third));
                    break;
                case 2:
                    holder.communityPic.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_forth));
                    break;
                default:
                    holder.communityPic.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
            }
        }

        if(community.isSelected){
            selectedView(holder);
        } else {
            unselectedView(holder);
        }

        holder.communityPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionListener.notify(community);
            }
        });

        holder.imageView_addCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickedItemPosition(position);
                holder.imageView_addCommunity.setVisibility(View.GONE);
                onJoinSelectListener.notify(community);
                }

        });
    }

    private void selectedView (ViewHolder holder){
        /*holder.button.setBackgroundColor(0xff31d7b9);
        holder.button.setTextColor(0xffffffff);
        holder.button.setText(context.getString(R.string.text_joined));*/
    }

    private void unselectedView(ViewHolder holder){
       /* holder.button.setBackgroundResource(R.drawable.border_button);
        holder.button.setTextColor(0xff31d7b9);
        holder.button.setText(context.getString(R.string.text_join));*/
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.communityPic,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    @Override
    public int getItemCount() {
        return filteredCommunities.size();
    }

    public int getClickedItemPosition() {
        return clickedItemPosition;
    }

    public void setClickedItemPosition(int clickedItemPosition) {
        this.clickedItemPosition = clickedItemPosition;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        MaskedImageView communityPic;
        TextView name;
        ImageView imageView_addCommunity;

        public ViewHolder(View view) {
            super(view);

            communityPic = (MaskedImageView) view.findViewById(R.id.dashboard_community_image_people_image);
            imageView_addCommunity = (ImageView) view.findViewById(R.id.item_dashboard_community_imageView_join);
            name = (TextView) view.findViewById(R.id.dashboard_community_text_community_name);
//            description=(TextView)view.findViewById(R.id.dashboard_community_text_community_desc);
//            button = (TextView) view.findViewById(R.id.dashboard_community_text_view_people_action);

            Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), Constants.AvenirNextRegular);
            name.setTypeface(typeface);
//            button.setTypeface(typeface);
//            description.setTypeface(typeface);
        }
    }

    public DiscoverCommunityFilter getFilter() {
        return mFilter;
    }
public class DiscoverCommunityFilter extends Filter {
        private DiscoverCommunityAdapter mAdapter;

        private DiscoverCommunityFilter(DiscoverCommunityAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredCommunities.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredCommunities.addAll(communities);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final CommunityModel model : communities) {
                    if (model.getSubject().toLowerCase().startsWith(filterPattern)) {
                        filteredCommunities.add(model);
                    }
                }
            }
            System.out.println("Count Number " + filteredCommunities.size());
            results.values = filteredCommunities;
            results.count = filteredCommunities.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((ArrayList<CommunityModel>) results.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
