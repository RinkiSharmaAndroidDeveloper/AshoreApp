package com.trutek.looped.ui.activity.display.discussion;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.NestedRecyclerViewModel;
import com.trutek.looped.data.contracts.services.ICommentService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.DateUtils;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Amrit on 23/02/17.
 */

public class SubDiscussionAdapter extends RecyclerView.Adapter<SubDiscussionAdapter.ViewHolder> {

    private List<CommentModel> subComments;
    private OnActionListener<Integer> subDiscussionListener;

    public SubDiscussionAdapter(List<CommentModel> subComments, OnActionListener<Integer> subDiscussionListener) {
        this.subComments = subComments;
        this.subDiscussionListener = subDiscussionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_discussion,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        CommentModel commentModel = subComments.get(position);

        if(commentModel.profile != null && commentModel.profile.picUrl != null && commentModel.profile.picUrl.contains("http")){
            displayImageByUrl(commentModel.profile.picUrl, holder);
        } else {

            displayImageByUrl("drawable://" + R.drawable.default_placeholder,holder);
        }

        holder.textView_name.setText(commentModel.profile.getName());
        holder.textView_time.setText(DateUtils.toTimeYesterdayMonthDate(commentModel.date.getTime()/1000));
        holder.textView_message.setText(commentModel.text);

        holder.linearLayout_commentInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                subDiscussionListener.notify(position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return subComments.size();
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.imageView_pic,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }


    public class ViewHolder extends  RecyclerView.ViewHolder{

        MaskedImageView imageView_pic;
        TextView textView_name,textView_time,textView_message;
        LinearLayout linearLayout_commentInfo;


        public ViewHolder(View itemView) {
            super(itemView);
            Typeface avenirRegular = Typeface.createFromAsset(itemView.getResources().getAssets(), Constants.AvenirNextRegular);
            imageView_pic = (MaskedImageView) itemView.findViewById(R.id.item_sub_discussion_imageView_pic);
            textView_name = (TextView) itemView.findViewById(R.id.item_sub_discussion_textView_name);
            textView_time = (TextView) itemView.findViewById(R.id.item_sub_discussion_textView_time);
            textView_message = (TextView) itemView.findViewById(R.id.item_sub_discussion_textView_message);

            linearLayout_commentInfo = (LinearLayout) itemView.findViewById(R.id.item_discussion_linearLayout_info);

            textView_name.setTypeface(avenirRegular);
            textView_time.setTypeface(avenirRegular);
            textView_message.setTypeface(avenirRegular);

        }
    }
}
