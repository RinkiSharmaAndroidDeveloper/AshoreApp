package com.trutek.looped.ui.activity.display.discussion;

import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.NestedRecyclerViewModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.DateUtils;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

public class DiscussionAdapter  extends  RecyclerView.Adapter<DiscussionAdapter.ViewHolder> {

    static final String TAG = DiscussionAdapter.class.getSimpleName();

    public ArrayList<CommentModel> comments;
    ArrayList<ArrayList<CommentModel>> subComments;
    OnActionListener<CommentModel> interestSelectedActionListeners;

    private OnActionListener<Integer> replyListener;
    private OnActionListener<Integer> subCommentListener;
    private OnActionListener<Integer> commentOptionListener;
    private OnActionListener<NestedRecyclerViewModel> subDiscussionOptionListener;
    ViewHolder viewHolder;

    Boolean isStarredMessagesSelected =false;
    int visibleRepliesFor = -1;

    public DiscussionAdapter(ArrayList<CommentModel> comments,
                             ArrayList<ArrayList<CommentModel>> subComments,
                             OnActionListener<CommentModel> minterestSelectedActionListeners,
                             OnActionListener<Integer> replyListener,
                             OnActionListener<Integer> subCommentListener,
                             OnActionListener<Integer> commentOptionListener,
                             OnActionListener<NestedRecyclerViewModel> subDiscussionOptionListener) {

        this.comments = comments;
        this.subComments = subComments;
        interestSelectedActionListeners=minterestSelectedActionListeners;
        this.replyListener = replyListener;
        this.subCommentListener = subCommentListener;
        this.commentOptionListener = commentOptionListener;
        this.subDiscussionOptionListener = subDiscussionOptionListener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discussion_recycler, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        viewHolder = holder;
        final CommentModel comment = comments.get(position);

        holder.comment.setText(comment.text);
        holder.name.setText(comment.profile.name);
        holder.txt_time.setText(DateUtils.toTimeYesterdayFullMonthDate(comment.date.getTime()/1000));

        if(comment.profile != null && comment.profile.picUrl != null && comment.profile.picUrl.contains("http")){
            displayImageByUrl(comment.profile.picUrl, holder);
        } else {
            displayImageByUrl("drawable://" + R.drawable.default_placeholder, holder);
        }

        if(comment.isStarred()){
            holder.select_starred_messages.setImageDrawable(holder.itemView.getResources().getDrawable(R.mipmap.starred_yellow_icon));
        }else{
            holder.select_starred_messages.setImageDrawable(holder.itemView.getResources().getDrawable(R.mipmap.star_icon));
        }

        if(visibleRepliesFor == position){
            holder.linearLayout_subComments.setVisibility(View.VISIBLE);
            getSubComments(position,holder);
        }else{
            holder.linearLayout_subComments.setVisibility(View.GONE);
        }

        holder.select_starred_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment.isStarred()){
                    comment.setStarred(false);
                }else{
                    comment.setStarred(true);
                }
//                interestSelectedActionListeners.notify(comment);
                notifyItemChanged(position);
            }
        });

        if(comment.getThreadCount()>0) {
            String reply_text = String.format(holder.itemView.getResources().getString(R.string.comment_text_replies)
                    , comment.getThreadCount());

            holder.textView_replies.setText(reply_text);
            holder.textView_replies.setVisibility(View.VISIBLE);
        }else {
            holder.textView_replies.setVisibility(View.GONE);
        }


        holder.textView_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != replyListener) {
                    replyListener.notify(position);
                }
            }
        });

        holder.textView_replies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.linearLayout_subComments.getVisibility() == View.GONE) {
                    visibleRepliesFor = position;
                    holder.linearLayout_subComments.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.recyclerView_subComments.setVisibility(View.GONE);

                    if(null != subCommentListener) {
                        subCommentListener.notify(position);
                    }

                }else{
                    holder.linearLayout_subComments.setVisibility(View.GONE);
                    visibleRepliesFor = -1;
                }
            }
        });

        holder.linearLayout_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment.getThreadCount()>0) {
                    holder.textView_replies.performClick();
                }
            }
        });

        holder.linearLayout_comment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(null != commentOptionListener) {
                    commentOptionListener.notify(position);
                    return true;
                }else {
                    return false;
                }
            }
        });

    }

    void getSubComments(int position, final ViewHolder holder){

        SubDiscussionAdapter subDiscussionAdapter = new SubDiscussionAdapter(subComments.get(position)
                , subDiscussionListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext());
        holder.recyclerView_subComments.setLayoutManager(linearLayoutManager);
        holder.recyclerView_subComments.setAdapter(subDiscussionAdapter);
        holder.recyclerView_subComments.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
    }

    OnActionListener<Integer> subDiscussionListener = new OnActionListener<Integer>() {
        @Override
        public void notify(Integer position) {
            NestedRecyclerViewModel viewModel = new NestedRecyclerViewModel();
            viewModel.setParentPosition(viewHolder.getAdapterPosition());
            viewModel.setChildPosition(position);

            Log.d(TAG,"Child position: " + position +" - Parent position: " + viewHolder.getAdapterPosition());
            subDiscussionOptionListener.notify(viewModel);

        }
    };

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaskedImageView imv_profie;
        TextView name, comment, txt_time, textView_reply, textView_replies;
        ImageView select_starred_messages;
        LinearLayout linearLayout_subComments,linearLayout_comment;
        RecyclerView recyclerView_subComments;
        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            Typeface avenirRegular = Typeface.createFromAsset(view.getResources().getAssets(), Constants.AvenirNextRegular);
            imv_profie = (MaskedImageView) view.findViewById(R.id.layout_discussion_image_profile_photo);
            name = (TextView) view.findViewById(R.id.layout_discussion_txt_name);
            comment = (TextView) view.findViewById(R.id.layout_discussion_txt_description);
            txt_time = (TextView) view.findViewById(R.id.layout_discussion_txt_time);
            textView_reply = (TextView) view.findViewById(R.id.item_discussion_textView_reply);
            textView_replies = (TextView) view.findViewById(R.id.item_discussion_textView_replies);
            recyclerView_subComments = (RecyclerView) view.findViewById(R.id.item_discussion_recyclerView);
            progressBar = (ProgressBar) view.findViewById(R.id.item_discussion_progressBar);
            linearLayout_subComments = (LinearLayout) view.findViewById(R.id.item_discussion_linearLayout_sunComment);
            linearLayout_comment = (LinearLayout) view.findViewById(R.id.discussion_activity_linear_layout);

            select_starred_messages = (ImageView) view.findViewById(R.id.discussion_activity_select_starred_message);

            name.setTypeface(avenirRegular);
            comment.setTypeface(avenirRegular);
            txt_time.setTypeface(avenirRegular);
            textView_reply.setTypeface(avenirRegular);
            textView_replies.setTypeface(avenirRegular);
        }
    }

    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.imv_profie,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }
}