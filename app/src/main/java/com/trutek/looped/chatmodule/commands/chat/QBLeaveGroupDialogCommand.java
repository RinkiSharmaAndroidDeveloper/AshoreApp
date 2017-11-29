package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.commands.ServiceCommand;

public class QBLeaveGroupDialogCommand extends ServiceCommand {

    private GroupChatHelper multiChatHelper;

    public QBLeaveGroupDialogCommand(Context context, GroupChatHelper multiChatHelper, String successAction,
                                     String failAction) {
        super(context, successAction, failAction);
        this.multiChatHelper = multiChatHelper;
    }

    public static void start(Context context, DialogModel dialog) {
        Intent intent = new Intent(QuickBloxServiceConsts.LEAVE_GROUP_DIALOG_ACTION, null, context, QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG, dialog);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        DialogModel dialog = (DialogModel) extras.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);
        multiChatHelper.leaveRoomChat(dialog);

        return extras;
    }
}