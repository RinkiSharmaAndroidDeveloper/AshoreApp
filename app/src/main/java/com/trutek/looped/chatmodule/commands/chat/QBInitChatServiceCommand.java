package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.trutek.looped.chatmodule.helpers.QBChatRestHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.commands.ServiceCommand;


public class QBInitChatServiceCommand extends ServiceCommand {

    private QBChatRestHelper chatRestHelper;

    public QBInitChatServiceCommand(Context context, QBChatRestHelper chatRestHelper, String successAction,
                                    String failAction) {
        super(context, successAction, failAction);
        this.chatRestHelper = chatRestHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QuickBloxServiceConsts.INIT_CHAT_SERVICE_ACTION, null, context, QuickBloxService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {

        chatRestHelper.initChatService();

        return extras;
    }
}