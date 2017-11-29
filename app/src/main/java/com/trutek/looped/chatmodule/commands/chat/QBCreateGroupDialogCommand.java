package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.chat.model.QBChatDialog;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.UserFriendUtils;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.msas.common.commands.ServiceCommand;

import java.util.ArrayList;

public class QBCreateGroupDialogCommand extends ServiceCommand {

    private GroupChatHelper multiChatHelper;

    public QBCreateGroupDialogCommand(Context context, GroupChatHelper multiChatHelper,
                                      String successAction, String failAction) {
        super(context, successAction, failAction);
        this.multiChatHelper = multiChatHelper;
    }

    public static void start(Context context, String roomName, ArrayList<ConnectionModel> friendList, String photoUrl) {
        Intent intent = new Intent(QuickBloxServiceConsts.CREATE_GROUP_CHAT_ACTION, null, context, QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_ROOM_NAME, roomName);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_FRIENDS, friendList);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_ROOM_PHOTO_URL, photoUrl);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        ArrayList<ConnectionModel> friendList = (ArrayList<ConnectionModel>) extras.getSerializable(QuickBloxServiceConsts.EXTRA_FRIENDS);
        String roomName = (String) extras.getSerializable(QuickBloxServiceConsts.EXTRA_ROOM_NAME);
        String photoUrl = (String) extras.getSerializable(QuickBloxServiceConsts.EXTRA_ROOM_PHOTO_URL);

        QBChatDialog dialog = multiChatHelper.createGroupChat(roomName, UserFriendUtils.getFriendIdsFromUsersList(friendList), photoUrl);
        extras.putSerializable(QuickBloxServiceConsts.EXTRA_DIALOG, dialog);
        return extras;
    }
}