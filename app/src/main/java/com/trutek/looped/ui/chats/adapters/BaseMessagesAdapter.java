package com.trutek.looped.ui.chats.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.content.QBContent;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.model.QBBaseCustomObject;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.CombinationMessage;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.models.UserModel;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.chats.ImagePreviewActivity;
import com.trutek.looped.ui.chats.adapters.base.BaseClickListenerViewHolder;
import com.trutek.looped.ui.chats.adapters.base.BaseRecyclerViewAdapter;
import com.trutek.looped.ui.chats.adapters.base.BaseViewHolder;
import com.trutek.looped.utils.DateUtils;
import com.trutek.looped.utils.FileUtils;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.List;

import butterknife.BindView;

public abstract class BaseMessagesAdapter extends
        BaseRecyclerViewAdapter<CombinationMessage, BaseClickListenerViewHolder<CombinationMessage>> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    protected static final int TYPE_NOTIFICATION = 0;
    protected static final int TYPE_OWN_MESSAGE = 1;
    protected static final int TYPE_OPPONENT_MESSAGE = 2;

    protected UserModel currentUser;
    private FileUtils fileUtils;

    public BaseMessagesAdapter(Activity baseActivity, List<CombinationMessage> objectsList) {
        super(baseActivity, objectsList);
        currentUser = AppSession.getSession().getUser();
        fileUtils = new FileUtils();
    }

    @Override
    public long getHeaderId(int position) {
        CombinationMessage message = getItem(position);
        return DateUtils.toShortDateLong(message.getCreatedDate());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.item_message_header_date, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;

        TextView headerTextView = (TextView) view.findViewById(R.id.header_date);
        CombinationMessage combinationMessage = getItem(position);
        headerTextView.setText(DateUtils.toTodayYesterdayFullMonthDate(combinationMessage.getCreatedDate()));
    }

    protected int getItemViewType(CombinationMessage messageModel) {
        boolean ownMessage = !messageModel.isIncoming(currentUser.getQbId());
        if (messageModel.getNotificationType() == null) {
            if (ownMessage) {
                return TYPE_OWN_MESSAGE;
            } else {
                return TYPE_OPPONENT_MESSAGE;
            }
        } else {
            return TYPE_NOTIFICATION;
        }
    }

    protected void resetUI(ViewHolder viewHolder) {
        setViewVisibility(viewHolder.attachMessageRelativeLayout, View.GONE);
        setViewVisibility(viewHolder.progressRelativeLayout, View.GONE);
        setViewVisibility(viewHolder.textMessageView, View.GONE);
    }

    protected void setViewVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    protected static class ViewHolder extends BaseViewHolder<CombinationMessage> {

        @Nullable
        @BindView(R.id.name_textview)
        TextView nameTextView;

        @Nullable
        @BindView(R.id.text_message_view)
        View textMessageView;

        @Nullable
        @BindView(R.id.text_message_delivery_status_imageview)
        ImageView messageDeliveryStatusImageView;

        @Nullable
        @BindView(R.id.attach_message_delivery_status_imageview)
        ImageView attachDeliveryStatusImageView;

        @Nullable
        @BindView(R.id.progress_relativelayout)
        RelativeLayout progressRelativeLayout;

        @Nullable
        @BindView(R.id.attach_message_relativelayout)
        RelativeLayout attachMessageRelativeLayout;

        @Nullable
        @BindView(R.id.attach_imageview)
        MaskedImageView attachImageView;

        @Nullable
        @BindView(R.id.message_textview)
        TextView messageTextView;

        @Nullable
        @BindView(R.id.time_text_message_textview)
        TextView timeTextMessageTextView;

        @Nullable
        @BindView(R.id.time_attach_message_textview)
        TextView timeAttachMessageTextView;

        @Nullable
        @BindView(R.id.vertical_progressbar)
        ProgressBar verticalProgressBar;

        @Nullable
        @BindView(R.id.centered_progressbar)
        ProgressBar centeredProgressBar;

        public ViewHolder(BaseMessagesAdapter adapter, View view) {
            super(adapter, view);
        }
    }

    protected void displayAttachImageById(String attachId, final ViewHolder viewHolder) {
        String token;
        String privateUrl;
        try {
            token = QBAuth.getBaseService().getToken();
            privateUrl = String.format("%s/blobs/%s?token=%s", QBSettings.getInstance().getApiEndpoint(), attachId, token);
            displayAttachImage(privateUrl, viewHolder);
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }
//        TODO VT надо переделать на след код после обновления версии SDK до 2.5
//        String privateUrl = QBFile.getPrivateUrlForUID(attachId);
//        displayAttachImage(privateUrl, viewHolder);
    }

    protected void displayAttachImage(String attachUrl, final ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(attachUrl, viewHolder.attachImageView,
                ImageLoaderUtils.UIL_DEFAULT_DISPLAY_OPTIONS, new ImageLoadingListener(viewHolder),
                new SimpleImageLoadingProgressListener(viewHolder));
    }

    protected void setMessageStatus(ImageView imageView, boolean messageDelivered, boolean messageRead) {
        imageView.setImageResource(getMessageStatusIconId(messageDelivered, messageRead));
    }

    protected int getMessageStatusIconId(boolean isDelivered, boolean isRead) {
        int iconResourceId = 0;

        if (isRead) {
            iconResourceId = R.drawable.ic_status_mes_sent_received;
        } else if (isDelivered) {
            iconResourceId = R.drawable.ic_status_mes_sent;
        }

        return iconResourceId;
    }

    public class ImageLoadingListener extends SimpleImageLoadingListener {

        private ViewHolder viewHolder;
        private Bitmap loadedImageBitmap;
        private String imageUrl;

        public ImageLoadingListener(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            super.onLoadingStarted(imageUri, view);
            viewHolder.verticalProgressBar.setProgress(Constants.ZERO_INT_VALUE);
            viewHolder.centeredProgressBar.setProgress(Constants.ZERO_INT_VALUE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            updateUIAfterLoading();
            imageUrl = null;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedBitmap) {
            initMaskedImageView(loadedBitmap);
            fileUtils.checkExistsFile(imageUri, loadedBitmap);
            this.imageUrl = imageUri;
        }

        private void initMaskedImageView(Bitmap loadedBitmap) {
            loadedImageBitmap = loadedBitmap;
            viewHolder.attachImageView.setOnClickListener(receiveImageFileOnClickListener());
            viewHolder.attachImageView.setImageBitmap(loadedImageBitmap);
            setViewVisibility(viewHolder.attachMessageRelativeLayout, View.VISIBLE);
            setViewVisibility(viewHolder.attachImageView, View.VISIBLE);

            updateUIAfterLoading();
        }

        private void updateUIAfterLoading() {
            if (viewHolder.progressRelativeLayout != null) {
                setViewVisibility(viewHolder.progressRelativeLayout, View.GONE);
            }
        }

        private View.OnClickListener receiveImageFileOnClickListener() {
            return new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (imageUrl != null) {
                        view.startAnimation(AnimationUtils.loadAnimation(baseActivity, R.anim.chat_attached_file_click));
                        ImagePreviewActivity.start(baseActivity, imageUrl);
                    }
                }
            };
        }
    }

    public class SimpleImageLoadingProgressListener implements ImageLoadingProgressListener {

        private ViewHolder viewHolder;

        public SimpleImageLoadingProgressListener(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            viewHolder.verticalProgressBar.setProgress(Math.round(100.0f * current / total));
        }
    }

}
