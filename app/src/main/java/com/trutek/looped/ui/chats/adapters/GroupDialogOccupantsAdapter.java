package com.trutek.looped.ui.chats.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;

import java.util.List;

public class GroupDialogOccupantsAdapter extends RecyclerView.Adapter<GroupDialogOccupantsAdapter.ViewHolder> {

    private Integer userId;
    private Context context;
    private OnActionListener<ChatUserModel> onActionListener;
    private List<ChatUserModel> objectsList;

    public GroupDialogOccupantsAdapter(Context context, OnActionListener<ChatUserModel> onActionListener, List<ChatUserModel> objectsList){

        this.context = context;
        this.onActionListener = onActionListener;
        this.objectsList = objectsList;
    }

    @Override
    public GroupDialogOccupantsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupDialogOccupantsAdapter.ViewHolder holder, int position) {
        final ChatUserModel userModel = objectsList.get(position);

        holder.name.setText(userModel.name);

        if(userId.equals(userModel.getUserId())){
            holder.txt_admin.setVisibility(View.VISIBLE);
        } else {
            holder.txt_admin.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return objectsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, txt_admin;
        ImageView image_icon, select_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.member_name);
            image_icon = (MaskedImageView) itemView.findViewById(R.id.connection_image);
            select_icon = (ImageView)itemView.findViewById(R.id.imageview_for_select_connection);
            txt_admin = (TextView) itemView.findViewById(R.id.txt_admin);
            Typeface avenirNextRegular=Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            name.setTypeface(avenirNextRegular);
            txt_admin.setTypeface(avenirNextRegular);
        }

    }

    public void setNewData(List<ChatUserModel> newData) {
        objectsList = newData;
        notifyDataSetChanged();
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
