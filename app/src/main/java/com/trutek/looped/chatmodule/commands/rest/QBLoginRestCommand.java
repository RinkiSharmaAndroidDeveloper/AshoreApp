package com.trutek.looped.chatmodule.commands.rest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.helpers.QBAuthHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.commands.ServiceCommand;

public class QBLoginRestCommand extends ServiceCommand {

    private static final String TAG = QBLoginRestCommand.class.getSimpleName();

    private final QBAuthHelper authHelper;

    public QBLoginRestCommand(Context context, QBAuthHelper authHelper, String successAction,
                              String failAction) {
        super(context, successAction, failAction);
        this.authHelper = authHelper;
    }

    public static void start(Context context, QBUser user) {
        Intent intent = new Intent(QuickBloxServiceConsts.LOGIN_REST_ACTION, null, context, QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_USER, user);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        authHelper.login();
        return extras;
    }
}