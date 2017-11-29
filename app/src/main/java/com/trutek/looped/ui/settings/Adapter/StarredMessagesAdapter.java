package com.trutek.looped.ui.settings.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.List;

/**
 * Created by Rinki on 2/19/2017.
 */
public class StarredMessagesAdapter  extends RecyclerView.Adapter<StarredMessagesAdapter.ViewHolder>{
    List<CommentModel> mCommentModelList;
    CommentModel commentModel;

    public StarredMessagesAdapter(List<CommentModel> commentModelList) {
       mCommentModelList =commentModelList;
    }

    @Override
    public StarredMessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_starred_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StarredMessagesAdapter.ViewHolder holder, int position) {
        commentModel = mCommentModelList.get(position);
        holder.textView_name.setText(commentModel.name);
        holder.textView_comment.setText(commentModel.text);
       holder.textView_date.setText(DateHelper.stringify(commentModel.date, DateHelper.StringifyAs.FullMonthDate));
     //   holder.textView_Title.setText(commentModel.);

        if(commentModel.picUrl !=null) {
            if (commentModel.picUrl != null && !commentModel.picUrl.isEmpty() && commentModel.picUrl.contains("http")) {
                ImageLoader.getInstance().displayImage(commentModel.picUrl, holder.imageView_profile, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
            } }else {

        }


    }

    @Override
    public int getItemCount() {
        return mCommentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name, textView_comment,textView_date,textView_Title;
        MaskedImageView imageView_profile;
        public ViewHolder(View itemView) {
            super(itemView);
            textView_name =(TextView)itemView.findViewById(R.id.textView_name);
            textView_comment = (TextView) itemView.findViewById(R.id.textView_commment);
            textView_date = (TextView) itemView.findViewById(R.id.textView_data);
            textView_Title = (TextView) itemView.findViewById(R.id.textView_title);
            imageView_profile = (MaskedImageView) itemView.findViewById(R.id.profile_pic_icon);
        }
    }
}
