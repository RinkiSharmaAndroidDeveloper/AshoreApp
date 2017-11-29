package com.trutek.looped.ui.chats.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.CombinationMessage;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.ui.chats.adapters.base.BaseClickListenerViewHolder;
import com.trutek.looped.utils.DateUtils;

import java.util.List;

public class GroupMessagesAdapter extends BaseMessagesAdapter {

    public GroupMessagesAdapter(Activity baseActivity, List<CombinationMessage> objectsList,
                                DialogModel dialog) {
        super(baseActivity, objectsList);
    }

    @Override
    public PrivateMessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_NOTIFICATION:
                return new ViewHolder(this, layoutInflater.inflate(R.layout.item_notification_message, viewGroup, false));
            case TYPE_OWN_MESSAGE:
                return new ViewHolder(this, layoutInflater.inflate(R.layout.item_message_own, viewGroup, false));
            case TYPE_OPPONENT_MESSAGE:
                return new ViewHolder(this, layoutInflater.inflate(R.layout.item_group_message_opponent, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(getItem(position));
    }

    @Override
    public void onBindViewHolder(BaseClickListenerViewHolder<CombinationMessage> baseClickListenerViewHolder, int position) {
        ViewHolder viewHolder = (ViewHolder) baseClickListenerViewHolder;

        CombinationMessage combinationMessage = getItem(position);
        boolean ownMessage = !combinationMessage.isIncoming(currentUser.getQbId());
        boolean notificationMessage = combinationMessage.getNotificationType() != null;

        String senderName;

//        if (viewHolder.verticalProgressBar != null) {
//            viewHolder.verticalProgressBar.setProgressDrawable(baseActivity.getResources().getDrawable(R.drawable.vertical_progressbar));
//        }

        if (notificationMessage) {
            viewHolder.messageTextView.setText(combinationMessage.getBody());
            viewHolder.timeTextMessageTextView.setText(DateUtils.formatDateSimpleTime(combinationMessage.getCreatedDate()));
        } else {

            resetUI(viewHolder);

            if(!ownMessage){
                senderName = combinationMessage.getDialogOccupant().getChatUser().getName();
//            viewHolder.nameTextView.setTextColor(combinationMessage.getDialogOccupant().getUserId());
                viewHolder.nameTextView.setText(senderName);
            }

            if (combinationMessage.getAttachment() != null) {
                viewHolder.timeAttachMessageTextView.setText(DateUtils.formatDateSimpleTime(combinationMessage.getCreatedDate()));
                setViewVisibility(viewHolder.progressRelativeLayout, View.VISIBLE);
                displayAttachImageById(combinationMessage.getAttachment().getAttachmentId(), viewHolder);
            } else {
                setViewVisibility(viewHolder.textMessageView, View.VISIBLE);
                viewHolder.timeTextMessageTextView.setText(
                        DateUtils.formatDateSimpleTime(combinationMessage.getCreatedDate()));
                viewHolder.messageTextView.setText(combinationMessage.getBody());
            }
        }
    }
}
