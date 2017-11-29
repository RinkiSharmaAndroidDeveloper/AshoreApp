package com.trutek.looped.chatmodule.commands.rest;

import android.content.Context;
import android.content.Intent;

import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.commands.CompositeServiceCommand;

public class QBLoginCompositeCommand extends CompositeServiceCommand {

    public QBLoginCompositeCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, QBUser user) {
        Intent intent = new Intent(QuickBloxServiceConsts.LOGIN_ACTION, null, context, QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_USER, user);
        context.startService(intent);
    }
}