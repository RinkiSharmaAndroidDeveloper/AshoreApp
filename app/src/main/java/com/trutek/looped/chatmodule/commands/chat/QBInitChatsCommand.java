package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.commands.ServiceCommand;

public class QBInitChatsCommand extends ServiceCommand {

    private PrivateChatHelper privateChatHelper;
    private GroupChatHelper multiChatHelper;

    public QBInitChatsCommand(Context context, PrivateChatHelper privateChatHelper, GroupChatHelper multiChatHelper,
                              String successAction, String failAction) {
        super(context, successAction, failAction);
        this.privateChatHelper = privateChatHelper;
        this.multiChatHelper = multiChatHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QuickBloxServiceConsts.INIT_CHATS_ACTION, null, context, QuickBloxService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        QBUser user;

        if (extras == null) {
            user = AppSession.getSession().getQbUser();
        } else {
            user = (QBUser) extras.getSerializable(QuickBloxServiceConsts.EXTRA_USER);
        }

        privateChatHelper.init(user);
        multiChatHelper.init(user);

        return extras;
    }
}