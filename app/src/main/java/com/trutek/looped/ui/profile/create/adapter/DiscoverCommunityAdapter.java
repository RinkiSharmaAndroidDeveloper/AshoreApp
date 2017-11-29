package com.trutek.looped.ui.profile.create.adapter;

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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.chats.adapters.DialogsAdapter;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;


public class DiscoverCommunityAdapter extends RecyclerView.Adapter<DiscoverCommunityAdapter.ViewHolder> {

    Context context;
    private ArrayList<CommunityModel> communities;
    private OnActionListener<CommunityModel> onActionListener;
    private OnActionListener<CommunityModel> onDeSelectListener;

    private ArrayList<CommunityModel> filteredCommunities;
    private DiscoverCommunityFilter mFilter;

    public DiscoverCommunityAdapter(ArrayList<CommunityModel> communities,
                                    ArrayList<CommunityModel> filteredCommunities,
                                    OnActionListener<CommunityModel> onActionListener,
                                    OnActionListener<CommunityModel> onDeSelectListener,Context context) {

        this.context=context;
        this.communities = communities;
        this.filteredCommunities = filteredCommunities;
        this.onActionListener = onActionListener;
        this.onDeSelectListener = onDeSelectListener;

        mFilter = new DiscoverCommunityFilter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_discover_community, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final CommunityModel model = filteredCommunities.get(position);

        holder.name.setText(model.subject);
        holder.desc.setText(model.body);

        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.isSelected){
                    onActionListener.notify(model);
                    setVisibilityJoined(holder);
                    model.isSelected = true;
                }
            }
        });

        if(model.picUrl != null && !model.picUrl.isEmpty() && model.picUrl.contains("http")){
            displayImageByUrl(model.picUrl, holder);
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.communityPic.setImageBitmap(image);
        }

        if(model.isSelected){
            setVisibilityJoined(holder);
        } else {
            setVisibilityJoin(holder);
        }

        holder.communityPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayCommunity.class);
                intent.putExtra("communityModel", model);
                intent.putExtra("OPEN_FROM", 1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private void setVisibilityJoined(ViewHolder holder){
        holder.join.setBackgroundColor(0xff31d7b9);
        holder.join.setTextColor(0xffffffff);
        holder.join.setText(context.getString(R.string.text_joined));
    }


    private void setVisibilityJoin(ViewHolder holder){
        holder.join.setBackgroundResource(R.drawable.border_button);
        holder.join.setTextColor(0xff31d7b9);
        holder.join.setText(context.getString(R.string.text_join));
    }

    @Override
    public int getItemCount() {
        return filteredCommunities.size();
    }

    public void addItem(CommunityModel model){
        filteredCommunities.add(model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaskedImageView communityPic;
        TextView name, join, desc;

        public ViewHolder(View view) {
            super(view);

            communityPic = (MaskedImageView) view.findViewById(R.id.image_view_community_image);
            name = (TextView) view.findViewById(R.id.text_view_community_name);
            desc = (TextView) view.findViewById(R.id.text_view_community_desc);
            join = (TextView) view.findViewById(R.id.text_view_people_action);

            Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), Constants.AvenirNextRegular);
            name.setTypeface(typeface);
            desc.setTypeface(typeface);
            join.setTypeface(typeface);
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

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.communityPic,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }
}
