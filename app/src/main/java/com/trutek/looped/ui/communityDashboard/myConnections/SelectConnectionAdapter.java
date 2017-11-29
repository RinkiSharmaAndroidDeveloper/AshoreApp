package com.trutek.looped.ui.communityDashboard.myConnections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectConnectionAdapter extends RecyclerView.Adapter<SelectConnectionAdapter.ViewHolder> {

    private Context context;
    private List<ConnectionModel> communities;
    private OnActionListener<ConnectionModel> connectionSelectedActionListeners;
    private OnActionListener<ConnectionModel> connectionUnSelectedActionListeners;

    public SelectConnectionAdapter(Context context, List<ConnectionModel> connections,
                                   OnActionListener<ConnectionModel> connectionSelectedActionListeners,
                                   OnActionListener<ConnectionModel> connectionUnSelectedActionListeners) {
        this.context = context;
        this.communities = connections;
        this.connectionSelectedActionListeners = connectionSelectedActionListeners;
        this.connectionUnSelectedActionListeners = connectionUnSelectedActionListeners;
    }

    @Override
    public SelectConnectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_connection, parent, false);
        return new ViewHolder(view);
    }

    public void addItem(ConnectionModel model){
        communities.add(model);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ConnectionModel connection = communities.get(position);

        holder.name.setText(connection.profile.name);

        if(connection.profile.picUrl != null && !connection.profile.picUrl.isEmpty() && connection.profile.picUrl.contains("http")){
            displayImageByUrl(connection.profile.picUrl, holder);
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.image_icon.setImageBitmap(image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connection.isSelected){
                    connectionUnSelectedActionListeners.notify(connection);
                    holder.select_icon.setImageResource(R.drawable.unselect_people);
                    connection.isSelected = false;
                } else{
                    connectionSelectedActionListeners.notify(connection);
                    holder.select_icon.setImageResource(R.drawable.invite_people);
                    connection.isSelected = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView image_icon, select_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.connection_name);
            image_icon = (MaskedImageView) itemView.findViewById(R.id.connection_image);
            select_icon = (ImageView)itemView.findViewById(R.id.imageview_for_select_connection);

            Typeface avenirNextRegular=Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            name.setTypeface(avenirNextRegular);
        }
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.image_icon,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

}
