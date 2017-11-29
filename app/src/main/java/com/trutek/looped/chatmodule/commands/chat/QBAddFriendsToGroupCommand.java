package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.quickblox.chat.model.QBChatDialog;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.chatmodule.utils.DbUtils;
import com.trutek.looped.msas.common.commands.ServiceCommand;

import java.util.ArrayList;

public class QBAddFriendsToGroupCommand extends ServiceCommand {

    private GroupChatHelper multiChatHelper;

    public QBAddFriendsToGroupCommand(Context context, GroupChatHelper chatHelper, String successAction,
                                      String failAction) {
        super(context, successAction, failAction);
        this.multiChatHelper = chatHelper;
    }

    public static void start(Context context, String dialogId, ArrayList<Integer> friendIdsList) {
        Intent intent = new Intent(QuickBloxServiceConsts.ADD_FRIENDS_TO_GROUP_ACTION, null, context,
                QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_FRIENDS, friendIdsList);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        String dialogId = extras.getString(QuickBloxServiceConsts.EXTRA_DIALOG_ID);
        ArrayList<Integer> friendIdsList = (ArrayList<Integer>) extras.getSerializable(QuickBloxServiceConsts.EXTRA_FRIENDS);

        QBChatDialog qbDialog = multiChatHelper.addUsersToDialog(dialogId, friendIdsList);

        if (qbDialog != null) {
            DialogModel dialog = ChatUtils.createLocalDialog(qbDialog);
            dialog.setId(DataManager.getInstance().getDialogRepository().getByServerId(dialogId).getId());
            DataManager.getInstance().getDialogRepository().update(dialog.getId(), dialog,null);
            DbUtils.saveDialogsOccupants(DataManager.getInstance(), qbDialog, true);
        }

        Bundle returnedBundle = new Bundle();
        returnedBundle.putSerializable(QuickBloxServiceConsts.EXTRA_DIALOG, qbDialog);

        return returnedBundle;
    }
}