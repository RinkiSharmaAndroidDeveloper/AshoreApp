package com.trutek.looped.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.ViewHolder>  {

    private static final int TYPE_CONNECTION = 0;
    private static final int TYPE_OTHER = 1;
    private static final int TYPE_RECIPIENT = 2;

    private Context context;
    ArrayList<NotificationModel> notifications;
    private OnActionListener<NotificationModel> onAcceptActionListener;
    private OnActionListener<NotificationModel> onRejectActionListener;
    private OnActionListener<NotificationModel> onActionListener;

    NotificationAdapter(Context context, ArrayList<NotificationModel> notifications,
                        OnActionListener<NotificationModel> onAcceptActionListener,
                        OnActionListener<NotificationModel> onRejectActionListener,
                        OnActionListener<NotificationModel> onActionListener){
        this.context = context;
        this.notifications = notifications;
        this.onAcceptActionListener = onAcceptActionListener;
        this.onRejectActionListener = onRejectActionListener;
        this.onActionListener = onActionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_CONNECTION:
                return new NotificationAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification_connection, viewGroup, false));
            case TYPE_RECIPIENT:
                return new NotificationAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification_connection, viewGroup, false));
            case TYPE_OTHER:
                return new NotificationAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification_other, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NotificationModel notification = notifications.get(position);

        holder.txt_notification_name.setText(notification.message);

      if(notification.data.entity.picUrl != null && !notification.data.entity.picUrl.isEmpty()){
            displayImageByUrl(notification.data.entity.picUrl, holder);
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.imv_pic_notification.setImageBitmap(image);
       }

        if(holder.txt_notification_accept != null){
            holder.txt_notification_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAcceptActionListener.notify(notification);
                }
            });
        }

        if(holder.txt_notification_cancel != null){
            holder.txt_notification_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRejectActionListener.notify(notification);
                }
            });
        }

        holder.txt_notification_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onActionListener.notify(notification);
            }
        });

    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.imv_pic_notification,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(getItem(position));
    }

    public NotificationModel getItem(int position) {
        return notifications.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_notification_name, txt_notification_accept, txt_notification_cancel;
        ImageView imv_pic_notification;

        public ViewHolder(View view) {
            super(view);
            txt_notification_name = (TextView) view.findViewById(R.id.notification_txt_title);
            txt_notification_accept= (TextView) view.findViewById(R.id.notification_txt_accept);
            txt_notification_cancel=(TextView)view.findViewById(R.id.notification_txt_cancel);
            imv_pic_notification=(ImageView)view.findViewById(R.id.profile_pic_icon_notification);

        }
    }

    protected int getItemViewType(NotificationModel notification) {
        boolean isConnection = notification.data.api.equalsIgnoreCase("connections") && notification.data.action.equalsIgnoreCase("inComming");
        boolean isRecipient = notification.data.api.equalsIgnoreCase("profile/recipient") && notification.data.action.equalsIgnoreCase("invited");
        if (isConnection) {
            return TYPE_CONNECTION;
        } else if(isRecipient){
            return TYPE_RECIPIENT;
        } else {
            return TYPE_OTHER;
        }
    }
}
