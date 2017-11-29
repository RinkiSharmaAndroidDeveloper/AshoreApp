package com.trutek.looped.ui.communityDashboard.StepThree;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;

import java.util.ArrayList;
import java.util.List;

public class InviteFromLoopAdapter extends RecyclerView.Adapter<InviteFromLoopAdapter.ViewHolder> {

    private List<ConnectionModel> loopContactlist;


    private OnActionListener<ConnectionModel> onSelectedActionListener;
    private OnActionListener<ConnectionModel> onUnSelectedActionListener;

    public InviteFromLoopAdapter(List<ConnectionModel> mLoopedContactList, OnActionListener<ConnectionModel> onSelectedActionListener,
                                 OnActionListener<ConnectionModel> onUnSelectedActionListener) {
        this.loopContactlist = mLoopedContactList;
        this.onSelectedActionListener = onSelectedActionListener;
        this.onUnSelectedActionListener = onUnSelectedActionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite_from_loop,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ConnectionModel connection = loopContactlist.get(position);
        holder.text_contact_name.setText(connection.profile.name);

        holder.checkBox.setChecked(connection.isSelected());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    onSelectedActionListener.notify(connection);
                    connection.isSelected = true;

                }else {
                    onUnSelectedActionListener.notify(connection);
                    connection.isSelected = false;
                }
            }
        });

    }


    public void addList(List<ConnectionModel> list){
        loopContactlist.addAll(list);
    }

    @Override
    public int getItemCount() {
        return loopContactlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text_contact_name;
        ImageView contact_image_icon;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            text_contact_name=(TextView)itemView.findViewById(R.id.list_community_dashboard_sender_name);
            contact_image_icon=(ImageView)itemView.findViewById(R.id.loop_contact_image);

            Typeface avenirNextRegular=Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            text_contact_name.setTypeface(avenirNextRegular);
            checkBox = (CheckBox) itemView.findViewById(R.id.invite_from_loop_checkBox);
        }
    }
}
