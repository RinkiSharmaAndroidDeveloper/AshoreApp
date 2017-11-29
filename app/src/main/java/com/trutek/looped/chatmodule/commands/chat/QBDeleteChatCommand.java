package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.helpers.BaseChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.commands.ServiceCommand;

public class QBDeleteChatCommand extends ServiceCommand {

    private BaseChatHelper baseChatHelper;

    public QBDeleteChatCommand(Context context, BaseChatHelper baseChatHelper, String successAction,
                               String failAction) {
        super(context, successAction, failAction);
        this.baseChatHelper = baseChatHelper;
    }

    public static void start(Context context, String dialogId, DialogModel.Type dialogType) {
        Intent intent = new Intent(QuickBloxServiceConsts.DELETE_DIALOG_ACTION, null, context, QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG_TYPE, dialogType);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        String dialogId = extras.getString(QuickBloxServiceConsts.EXTRA_DIALOG_ID);
        DialogModel.Type dialogType = (DialogModel.Type) extras.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG_TYPE);
        baseChatHelper.deleteDialog(dialogId, dialogType);
        return extras;
    }
}