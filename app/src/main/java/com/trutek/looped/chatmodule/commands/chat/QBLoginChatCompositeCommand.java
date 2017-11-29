package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;

import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.commands.CompositeServiceCommand;

public class QBLoginChatCompositeCommand extends CompositeServiceCommand {

    public QBLoginChatCompositeCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context) {
        Intent intent = new Intent(QuickBloxServiceConsts.LOGIN_CHAT_COMPOSITE_ACTION, null, context, QuickBloxService.class);
        context.startService(intent);
    }
}