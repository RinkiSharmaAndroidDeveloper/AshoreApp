package com.trutek.looped.utils.helpers;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.chats.BaseDialogActivity;
import com.trutek.looped.utils.helpers.notification.ChatNotificationHelper;

public class ActivityUIHelper {

    private BaseAppCompatActivity baseActivity;
    private ChatUserModel senderUser;
    private DialogModel messagesDialog;
    private String message;
    private boolean isPrivateMessage;
    ChatNotificationHelper chatNotificationHelper;

    public ActivityUIHelper(BaseAppCompatActivity baseActivity) {
        this.baseActivity = baseActivity;
        chatNotificationHelper = new ChatNotificationHelper(baseActivity);
    }

    public void showChatMessageNotification(Bundle extras) {
        senderUser = (ChatUserModel) extras.getSerializable(QuickBloxServiceConsts.EXTRA_USER);
        message = extras.getString(QuickBloxServiceConsts.EXTRA_CHAT_MESSAGE);
        String dialogId = extras.getString(QuickBloxServiceConsts.EXTRA_DIALOG_ID);
        isPrivateMessage = extras.getBoolean(QuickBloxServiceConsts.EXTRA_IS_PRIVATE_MESSAGE);
        if (isMessagesDialogCorrect(dialogId) && senderUser != null) {
            message = baseActivity.getString(R.string.snackbar_new_message_title, senderUser.getName(), message);
            if (!TextUtils.isEmpty(message)) {
                showNewNotification(senderUser.getUserId(), dialogId);
            }
        }
    }

    private boolean isMessagesDialogCorrect(String dialogId) {
        messagesDialog = DataManager.getInstance().getDialogRepository().getByServerId(dialogId);
        return messagesDialog != null;
    }

    public void showNewNotification(Integer userId, String dialogId) {
        chatNotificationHelper.saveOpeningDialogData(userId, dialogId);
        chatNotificationHelper.saveOpeningDialog(true);
        chatNotificationHelper.sendNotification(message, null);

//        baseActivity.hideSnackBar();
//        baseActivity.showSnackbar(message, Snackbar.LENGTH_LONG, R.string.dialog_reply,
//                new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        showDialog();
//                    }
//                });
    }

    private void showDialog() {
        if (baseActivity instanceof BaseDialogActivity) {
            baseActivity.finish();
        }

        if (isPrivateMessage) {
            baseActivity.startPrivateChatActivity(senderUser, messagesDialog);
        } else {
            baseActivity.startGroupChatActivity(messagesDialog);
        }
    }
}