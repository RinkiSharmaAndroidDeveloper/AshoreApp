package com.trutek.looped.ui.communityDashboard.discoverPeople;

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
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.profile.display.DisplayProfile;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

public class DiscoverPeopleAdapter extends RecyclerView.Adapter<DiscoverPeopleAdapter.ViewHolder> {
    Context context;
    private ArrayList<ProfileModel> filteredProfiles;
    private ArrayList<ProfileModel> profiles;
    private OnActionListener<ProfileModel> onActionListener;
    private OnActionListener<ProfileModel> onDeSelectListener;

    private DiscoverPeopleFilter mFilter;

    public DiscoverPeopleAdapter(ArrayList<ProfileModel> discoverPeopleList,
                                 OnActionListener<ProfileModel> peopleSelectedActionListeners,
                                 OnActionListener<ProfileModel> peopleUnSelectedActionListeners,
                                 Context context,
                                 ArrayList<ProfileModel> filteredProfiles) {
        this.profiles = discoverPeopleList;
        this.onActionListener = peopleSelectedActionListeners;
        this.onDeSelectListener = peopleUnSelectedActionListeners;
        this.context = context;
        this.filteredProfiles = filteredProfiles;
        mFilter = new DiscoverPeopleFilter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_dashboard_discover_people, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ProfileModel profile = filteredProfiles.get(position);
        if (null != profile.name) {
            holder.name.setText(profile.name);
        }

        if (profile.picUrl != null && !profile.picUrl.isEmpty() && profile.picUrl.contains("http")) {
            displayImageByUrl(profile.picUrl, holder);
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.profilePic.setImageBitmap(image);
        }
        holder.imageView_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionListener.notify();
            }
        });
        /*if(profile.isSelected){
            selectedView(holder);
        } else {
            unselectedView(holder);
        }*/

      /*  holder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!profile.isSelected){
                    onActionListener.notify(profile);
                    selectedView(holder);
                    profile.isSelected = true;
                }
            }
        });*/

        holder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayProfile.class);
                intent.putExtra("profileModel", profile);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.imageView_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageView_join.setVisibility(View.GONE);
                onActionListener.notify(profile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredProfiles.size();
    }

    public void addItem(ProfileModel model) {
        filteredProfiles.add(model);
    }

    public void addFilteredData(ArrayList<ProfileModel> filteredData) {
       /* if (filteredProfiles != null) {
            filteredProfiles.clear();
            filteredProfiles.addAll(filteredData);
        }*/
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaskedImageView profilePic;
        ImageView imageView_join;
        TextView name;

        public ViewHolder(View view) {
            super(view);
            profilePic = (MaskedImageView) view.findViewById(R.id.discover_people_imageView_people_image);
            imageView_join = (ImageView) view.findViewById(R.id.discover_people_imageView_join);
            name = (TextView) view.findViewById(R.id.discover_people_textView_person_name);

            Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), Constants.AvenirNextRegular);
            name.setTypeface(typeface);
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
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.profilePic,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }
}