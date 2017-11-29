package com.trutek.looped.ui.chats.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.CombinationMessage;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.contracts.models.State;
import com.trutek.looped.ui.chats.adapters.base.BaseClickListenerViewHolder;
import com.trutek.looped.utils.DateUtils;

import java.util.List;

public class PrivateMessagesAdapter extends BaseMessagesAdapter {

    private DialogModel dialog;

    public PrivateMessagesAdapter(Activity baseActivity, List<CombinationMessage> objectsList, DialogModel dialog) {
        super(baseActivity, objectsList);
        this.dialog = dialog;
    }

    @Override
    public PrivateMessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_OWN_MESSAGE:
                return new ViewHolder(this, layoutInflater.inflate(R.layout.item_message_own, viewGroup, false));
            case TYPE_OPPONENT_MESSAGE:
                return new ViewHolder(this, layoutInflater.inflate(R.layout.item_message_opponent, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseClickListenerViewHolder<CombinationMessage> holder, int position) {
        CombinationMessage messageModel = getItem(position);
        boolean ownMessage = !messageModel.isIncoming(currentUser.getId());

        ViewHolder viewHolder = (ViewHolder) holder;

        if (messageModel.getAttachment() != null) {
            resetUI(viewHolder);

            setViewVisibility(viewHolder.progressRelativeLayout, View.VISIBLE);
            viewHolder.timeAttachMessageTextView.setText(DateUtils.formatDateSimpleTime(messageModel.getCreatedDate()));

            if (ownMessage && messageModel.getState() != null) {
                setMessageStatus(viewHolder.attachDeliveryStatusImageView, State.DELIVERED.equals(
                        messageModel.getState()), State.READ.equals(messageModel.getState()));
            }

            displayAttachImageById(messageModel.getAttachment().getAttachmentId(), viewHolder);
        } else {
            resetUI(viewHolder);

            setViewVisibility(viewHolder.textMessageView, View.VISIBLE);
            viewHolder.messageTextView.setText(messageModel.getBody());
            viewHolder.timeTextMessageTextView.setText(DateUtils.formatDateSimpleTime(messageModel.getCreatedDate()));

            if (ownMessage && messageModel.getState() != null) {
                setMessageStatus(viewHolder.messageDeliveryStatusImageView, State.DELIVERED.equals(
                        messageModel.getState()), State.READ.equals(messageModel.getState()));
            } else if (ownMessage && messageModel.getState() == null) {
                viewHolder.messageDeliveryStatusImageView.setImageResource(android.R.color.transparent);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(getItem(position));
    }

}
