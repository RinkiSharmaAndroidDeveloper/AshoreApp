package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.chat.model.QBChatDialog;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.commands.ServiceCommand;

public class QBCreatePrivateChatCommand extends ServiceCommand {

    private PrivateChatHelper chatHelper;

    public QBCreatePrivateChatCommand(Context context, PrivateChatHelper chatHelper, String successAction,
                                      String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, ChatUserModel chatUser) {
        Intent intent = new Intent(QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_ACTION, null, context,
                QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_FRIEND, chatUser.getUserId());
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        Integer friendId = (Integer) extras.getSerializable(QuickBloxServiceConsts.EXTRA_FRIEND);

        QBChatDialog privateDialog = chatHelper.createPrivateDialogIfNotExist(friendId);
        extras.putSerializable(QuickBloxServiceConsts.EXTRA_DIALOG, privateDialog);
        return extras;
    }
}