package com.trutek.looped.ui.communityDashboard.myCommunities.display;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

/**
 * Created by Rinki on 3/1/2017.
 */
public class AddMemberToCommunityAdapter  extends RecyclerView.Adapter<AddMemberToCommunityAdapter.ViewHolder> {
    ArrayList<ConnectionModel> connectionModels;
    public OnActionListener<ConnectionModel> onActionListener;
    public OnActionListener<ConnectionModel> onDeSelectListener;

    public AddMemberToCommunityAdapter(ArrayList<ConnectionModel> mConnectionModels,OnActionListener<ConnectionModel> mOnActionListener,OnActionListener<ConnectionModel> mOnDeSelectListener) {
        this.connectionModels = mConnectionModels;
        this.onActionListener = mOnActionListener;
        this.onDeSelectListener = mOnDeSelectListener;
    }

    @Override
    public AddMemberToCommunityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_connection_to_community, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddMemberToCommunityAdapter.ViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        final ConnectionModel connection=connectionModels.get(position);

        holder.textView_name.setText(connection.profile.getName());
        if (connection.profile.picUrl != null && !connection.profile.picUrl.isEmpty() && connection.profile.picUrl.contains("http")) {
            displayImageByUrl(connection.profile.picUrl, holder);
        } else {
        }

        holder.unselect_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked =  holder.unselect_member.isChecked();
                if(isChecked){
                    onActionListener.notify(connection);
                }else{
                    onDeSelectListener.notify(connection);
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return connectionModels.size();
    }
    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaskedImageView maskedImageView;
        TextView textView_name;
        CheckBox select_member,unselect_member;
        public ViewHolder(View itemView) {
            super(itemView);
            textView_name = (TextView) itemView.findViewById(R.id.connection_name);
            maskedImageView = (MaskedImageView) itemView.findViewById(R.id.connection_image);
           // select_member = (CheckBox) itemView.findViewById(R.id.imageview_for_select_connection);
            unselect_member = (CheckBox) itemView.findViewById(R.id.imageview_for_unselect_connection);

        }

    }
}
