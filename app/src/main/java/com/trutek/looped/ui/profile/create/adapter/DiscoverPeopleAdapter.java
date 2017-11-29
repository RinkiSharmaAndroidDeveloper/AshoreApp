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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.profile.display.DisplayProfile;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

public class DiscoverPeopleAdapter extends RecyclerView.Adapter<DiscoverPeopleAdapter.ViewHolder> {
   Context context;
    private ArrayList<ProfileModel> profiles;
    private ArrayList<ProfileModel> filteredProfiles;
    private OnActionListener<ProfileModel> onActionListener;
    private OnActionListener<ProfileModel> onDeSelectListener;

    private DiscoverPeopleFilter mFilter;

    public DiscoverPeopleAdapter(ArrayList<ProfileModel> profiles,
                                 ArrayList<ProfileModel> filteredProfiles,
                                 OnActionListener<ProfileModel> onActionListener,
                                 OnActionListener<ProfileModel> onDeSelectListener, Context context) {

        this.profiles = profiles;
        this.filteredProfiles = filteredProfiles;
        this.onActionListener = onActionListener;
        this.onDeSelectListener = onDeSelectListener;
        this.context=context;
        mFilter = new DiscoverPeopleFilter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_discover_people, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ProfileModel profile = filteredProfiles.get(position);

        holder.name.setText(profile.name);

        if(profile.picUrl != null && !profile.picUrl.isEmpty() && profile.picUrl.contains("http")){
            displayImageByUrl(profile.picUrl, holder);
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.profilePic.setImageBitmap(image);
        }

        if(profile.isSelected){
            setVisibilityConnected(holder);
        } else {
            setVisibilityConnect(holder);
        }

        holder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!profile.isSelected){
                    onActionListener.notify(profile);
                    setVisibilityConnected(holder);
                    profile.isSelected = true;
                }
            }
        });

        holder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("profileModel", profile);
                context.startActivity(intent);
            }
        });
    }

    private void setVisibilityConnected(ViewHolder holder){
        holder.connect.setBackgroundColor(0xff31d7b9);
        holder.connect.setTextColor(0xffffffff);
        holder.connect.setText(context.getString(R.string.text_connected));
    }

    private void setVisibilityConnect(ViewHolder holder){
        holder.connect.setBackgroundResource(R.drawable.border_button);
        holder.connect.setTextColor(0xff31d7b9);
        holder.connect.setText(context.getString(R.string.text_connect));
    }


    @Override
    public int getItemCount() {
        return filteredProfiles.size();
    }

    public void addItem(ProfileModel model){
        filteredProfiles.add(model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaskedImageView profilePic;
        TextView name, connect;

        public ViewHolder(View view) {
            super(view);

            profilePic = (MaskedImageView) view.findViewById(R.id.image_view_people_image);
            name = (TextView) view.findViewById(R.id.text_view_people_name);
            connect = (TextView) view.findViewById(R.id.text_view_people_action);

            Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), Constants.AvenirNextRegular);
            name.setTypeface(typeface);
            connect.setTypeface(typeface);
        }
    }

    public DiscoverPeopleFilter getFilter() {
        return mFilter;
    }

    public class DiscoverPeopleFilter extends Filter {
        private DiscoverPeopleAdapter mAdapter;

        private DiscoverPeopleFilter(DiscoverPeopleAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredProfiles.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredProfiles.addAll(profiles);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final ProfileModel model : profiles) {
                    if (model.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredProfiles.add(model);
                    }
                }
            }
            System.out.println("Count Number " + filteredProfiles.size());
            results.values = filteredProfiles;
            results.count = filteredProfiles.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((ArrayList<ProfileModel>) results.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.profilePic,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }
}
