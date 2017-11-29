package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.chat.errors.QBChatErrorsConstants;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.helpers.QBChatRestHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.Utils.ConnectivityUtils;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.commands.ServiceCommand;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.models.UserModel;

import org.jivesoftware.smack.SmackException;

import java.util.Date;

public class QBLoginChatCommand extends ServiceCommand {

    private static final String TAG = QBLoginChatCommand.class.getSimpleName();

    private QBChatRestHelper chatRestHelper;

    public QBLoginChatCommand(Context context, QBChatRestHelper chatRestHelper, String successAction,
                              String failAction) {
        super(context, successAction, failAction);
        this.chatRestHelper = chatRestHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QuickBloxServiceConsts.LOGIN_CHAT_ACTION, null, context, QuickBloxService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        final UserModel user = AppSession.getSession().getUser();

        QBUser currentUser = new QBUser();
        currentUser.setId(user.getQbId());
        currentUser.setPassword(user.getQbPassword());
        currentUser.setLogin(user.getPhone());

        tryLogin(currentUser);

        if (!chatRestHelper.isLoggedIn()) {
            throw new Exception(QBChatErrorsConstants.AUTHENTICATION_FAILED);
        }

        return extras;
    }

    private void tryLogin(QBUser currentUser) throws Exception {
        long startTime = new Date().getTime();
        long currentTime = startTime;

        while (!chatRestHelper.isLoggedIn() && (currentTime - startTime) < Constants.LOGIN_TIMEOUT) {
            currentTime = new Date().getTime();
            try {
                if (ConnectivityUtils.isNetworkAvailable(context)) {
                    chatRestHelper.login(currentUser);
                }
            } catch (SmackException ignore) {
                ignore.printStackTrace();
            }
        }
    }
}