package com.trutek.looped.ui.communityDashboard.myConnections;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.chat.QBCreatePrivateChatCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.data.impl.entities.Activity;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.chats.PrivateDialogActivity;
import com.trutek.looped.ui.communityDashboard.discoverPeople.DiscoverPeopleAdapter;
import com.trutek.looped.ui.profile.display.DisplayProfile;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

public class MyConnectionAdapter extends RecyclerView.Adapter<MyConnectionAdapter.ViewHolder> {

    private final ConnectionFilter mFilter;
    private Context context;
    private List<ConnectionModel> connections;
    private OnActionListener<ConnectionModel> onActionListener;
    private List<ConnectionModel> filteredConnections;
    private AsyncResult<Integer> asyncResult_popUpView;
    private AsyncResult<ConnectionModel> asyncResult_openConnection;

    public MyConnectionAdapter(Context context, List<ConnectionModel> connections
            , OnActionListener<ConnectionModel> onActionListener
            , List<ConnectionModel> filteredConnections
            , AsyncResult<Integer> asyncResult_popUpView
            , AsyncResult<ConnectionModel> asyncResult_openConnection) {
        this.context = context;
        this.connections = connections;
        this.onActionListener = onActionListener;
        this.filteredConnections = filteredConnections;
        this.asyncResult_popUpView = asyncResult_popUpView;
        this.asyncResult_openConnection = asyncResult_openConnection;
        mFilter = new ConnectionFilter(this);
    }

    @Override
    public MyConnectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_connections, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ConnectionModel connection = filteredConnections.get(position);
        holder.name.setText(connection.profile.name);
        final int pos = holder.getAdapterPosition();

        if(connection.profile.picUrl != null && !connection.profile.picUrl.isEmpty() && connection.profile.picUrl.contains("http")){
            displayImageByUrl(connection.profile.picUrl, holder);
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_placeholder);
            holder.profilePic.setImageBitmap(image);
        }

        holder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               asyncResult_openConnection.success(connection);
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionListener.notify(connection);
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredConnections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaskedImageView profilePic;
        TextView name, chat;

        public ViewHolder(View view) {
            super(view);
            profilePic = (MaskedImageView) view.findViewById(R.id.image_view_my_connection);
            name = (TextView) view.findViewById(R.id.text_view_my_connection_name);
            chat = (TextView) view.findViewById(R.id.text_view_chat);

            Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), Constants.AvenirNextRegular);
            name.setTypeface(typeface);
        }
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.profilePic,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    public ConnectionFilter getFilter() {
        return mFilter;
    }


    public class ConnectionFilter extends Filter {
        private MyConnectionAdapter mAdapter;

        private ConnectionFilter(MyConnectionAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredConnections.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredConnections.addAll(connections);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final ConnectionModel model : connections) {
                    if (model.getProfile().getName().toLowerCase().startsWith(filterPattern)) {
                        filteredConnections.add(model);
                    }
                }
            }
            System.out.println("Count Number " + filteredConnections.size());
            results.values = filteredConnections;
            results.count = filteredConnections.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

}
