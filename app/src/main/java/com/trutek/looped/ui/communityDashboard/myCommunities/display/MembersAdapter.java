package com.trutek.looped.ui.communityDashboard.myCommunities.display;

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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.chats.adapters.BaseMessagesAdapter;
import com.trutek.looped.ui.chats.adapters.DialogsAdapter;
import com.trutek.looped.utils.FileUtils;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

/**
 * Created by dell on 29/9/16.
 */
public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MemberModel> members;
    private FileUtils fileUtils;
    AsyncResult<MemberModel> masyncResult_profile;

    public MembersAdapter(Context context, ArrayList<MemberModel> members,AsyncResult<MemberModel> asyncResult_profile) {
        this.context = context;
        this.members = members;
        this.masyncResult_profile = asyncResult_profile;
        fileUtils = new FileUtils();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_member, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final MemberModel member = members.get(position);
        holder.txtView.setText(member.profile.name);

        if(member.profile.picUrl != null){
            displayImageByUrl(member.profile.picUrl, holder);
        } else {
            displayImageByUrl("",holder);
        }

        holder.memberImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                masyncResult_profile.success(member);
            }
        });
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.memberImage,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        public MaskedImageView memberImage;

        public ViewHolder(View view) {
            super(view);
            memberImage = (MaskedImageView) view.findViewById(R.id.image_view_member);
            txtView = (TextView) view.findViewById(R.id.txtRec);
        }
    }

}
