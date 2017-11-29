package com.trutek.looped.ui.communityDashboard.myCommunities.display;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

/**
 * Created by Rinki on 2/22/2017.
 */
public class DisplayMemberAdapter extends RecyclerView.Adapter<DisplayMemberAdapter.ViewHolder>{
    private Context context;
    ArrayList<MemberModel> memberList;
    public DisplayMemberAdapter(Context context, ArrayList<MemberModel> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @Override
    public DisplayMemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_members, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DisplayMemberAdapter.ViewHolder holder, int position) {
        MemberModel memberModel = memberList.get(position);
        holder.textView_name.setText(memberModel.profile.getName());
        if (memberModel.profile.picUrl != null && !memberModel.profile.picUrl.isEmpty() && memberModel.profile.picUrl.contains("http")) {
            displayImageByUrl(memberModel.profile.picUrl, holder);
        } else {
        }
    }
        @Override
        public int getItemCount() {
            return memberList.size();
        }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

        public class ViewHolder extends RecyclerView.ViewHolder {
            MaskedImageView maskedImageView;
            TextView textView_name;
            public ViewHolder(View itemView) {
                super(itemView);
                textView_name = (TextView) itemView.findViewById(R.id.textView_title);
                maskedImageView = (MaskedImageView) itemView.findViewById(R.id.profile_pic_icon);
            }
        }
    }



