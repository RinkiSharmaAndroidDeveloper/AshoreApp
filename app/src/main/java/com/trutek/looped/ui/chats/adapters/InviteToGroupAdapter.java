package com.trutek.looped.ui.chats.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.DateUtils;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.List;

/**
 * Created by Rinki on 3/24/2017.
 */
public class InviteToGroupAdapter extends RecyclerView.Adapter<InviteToGroupAdapter.ViewHolder> {


    private Context context;
    private List<DialogModel> dialogs;
    private OnActionListener<DialogModel> onLongPress;
    private List<ConnectionModel> connections;


    public InviteToGroupAdapter(Context context, List<DialogModel> dialogs,List<ConnectionModel> connections, OnActionListener<DialogModel> onLongPress) {
        this.context = context;
        this.dialogs = dialogs;
        this.connections = connections;
        this.onLongPress = onLongPress;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_dialogs, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final DialogModel model = dialogs.get(position);
        boolean is_Connection = false;
        int i;
        ConnectionModel user_model;
        holder.name.setText(model.name);
        holder.lastMessage.setText(model.getLastMessage());

        for (i=0; i < connections.size(); i ++) {
            user_model = connections.get(i);
            if ((model.getUserId()!=null)&& (model.getUserId().equals(user_model.profile.chat.id))) {
                is_Connection = true;
                break;
            }
        }
        if(is_Connection && model.type == "PRIVATE") {
            if (connections.get(i).profile.picUrl != null && !connections.get(i).profile.picUrl.isEmpty() && connections.get(i).profile.picUrl.contains("http")) {
                ImageLoader.getInstance().displayImage(connections.get(i).profile.picUrl, holder.profilePic, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
            } }else
        if(model.type == "GROUP") {
            if (model.getImageUrl() != null && !model.getImageUrl().isEmpty() && model.getImageUrl().contains("http")) {
                ImageLoader.getInstance().displayImage(model.getImageUrl(), holder.profilePic, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
            } }else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.profilePic.setImageBitmap(image);
        }


        if(model.getLastMessageDateSent() == 0){
            holder.sendTime.setText(Constants.EMPTY_STRING);
        } else {
            holder.sendTime.setText(DateUtils.toTimeYesterdayFullMonthDate(model.getLastMessageDateSent()));
        }
        if(model.getUnreadMessagesCount() > 0){
            holder.notification.setVisibility(View.VISIBLE);
            holder.notification.setText(String.valueOf(model.getUnreadMessagesCount()));
        } else
            holder.notification.setVisibility(View.GONE);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongPress.notify(model);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dialogs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaskedImageView profilePic;
        TextView name, lastMessage, sendTime, notification;

        public ViewHolder(View view) {
            super(view);

            profilePic = (MaskedImageView) view.findViewById(R.id.image_user);
            name = (TextView) view.findViewById(R.id.txt_name);
            lastMessage = (TextView) view.findViewById(R.id.txt_last_message);
            sendTime = (TextView) view.findViewById(R.id.text_send_time);
            notification = (TextView) view.findViewById(R.id.text_notification);
        }
    }

    public void setNewData(List<DialogModel> newData) {
        dialogs = newData;
        notifyDataSetChanged();
    }

    public DialogModel getItem(int position) {
        return dialogs.get(position);
    }
}
