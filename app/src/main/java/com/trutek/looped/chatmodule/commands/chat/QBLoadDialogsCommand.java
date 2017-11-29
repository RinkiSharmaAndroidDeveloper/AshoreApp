package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.trutek.looped.chatmodule.data.contracts.models.ParcelableQBDialog;
import com.trutek.looped.chatmodule.helpers.GroupChatHelper;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.commands.ServiceCommand;

import java.util.ArrayList;
import java.util.List;

public class QBLoadDialogsCommand extends ServiceCommand {

    private PrivateChatHelper privateChatHelper;
    private GroupChatHelper groupChatHelper;

    private final String FIELD_DIALOG_TYPE = "type";
    private final String OPERATOR_EQ = "eq";

    // TODO: HACK!
    // This is fetchMyCommunities value,
    // by default MAX count of Dialogs should be !> (DIALOGS_PARTS * ConstsCore.CHATS_DIALOGS_PER_PAGE)
    // it is 200 Dialogs
    private final static int DIALOGS_PARTS = 10; // TODO: need to fix in the second release.

    public QBLoadDialogsCommand(Context context, PrivateChatHelper privateChatHelper, GroupChatHelper groupChatHelper,
                                String successAction,
                                String failAction) {
        super(context, successAction, failAction);
        this.privateChatHelper = privateChatHelper;
        this.groupChatHelper = groupChatHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QuickBloxServiceConsts.LOAD_CHATS_DIALOGS_ACTION, null, context, QuickBloxService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        ArrayList<ParcelableQBDialog> parcelableQBDialog = new ArrayList<>();

        Bundle returnedBundle = new Bundle();
        QBRequestGetBuilder qbRequestGetBuilder = new QBRequestGetBuilder();

        qbRequestGetBuilder.setPagesLimit(Constants.CHATS_DIALOGS_PER_PAGE);
        qbRequestGetBuilder.sortDesc(QuickBloxServiceConsts.EXTRA_LAST_MESSAGE_DATE_SENT);

        parcelableQBDialog.addAll(ChatUtils.qbDialogsToParcelableQBDialogs(
                loadAllDialogsByType(QBDialogType.PRIVATE, returnedBundle, qbRequestGetBuilder)));

        parcelableQBDialog.addAll(ChatUtils.qbDialogsToParcelableQBDialogs(
                loadAllDialogsByType(QBDialogType.GROUP, returnedBundle, qbRequestGetBuilder)));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(QuickBloxServiceConsts.EXTRA_CHATS_DIALOGS, parcelableQBDialog);

        return bundle;
    }

    private List<QBChatDialog> loadAllDialogsByType(QBDialogType dialogsType,
                                                    Bundle returnedBundle, QBRequestGetBuilder qbRequestGetBuilder) throws QBResponseException {
        List<QBChatDialog> allDialogsList = new ArrayList<>();
        boolean needToLoadMore;

        do {
            qbRequestGetBuilder.setPagesSkip(allDialogsList.size());
            qbRequestGetBuilder.addRule(FIELD_DIALOG_TYPE, OPERATOR_EQ, dialogsType.getCode());

            List<QBChatDialog> newDialogsList = dialogsType == QBDialogType.PRIVATE
                    ? getPrivateDialogs(qbRequestGetBuilder, returnedBundle)
                    : getGroupDialogs(qbRequestGetBuilder, returnedBundle);

            allDialogsList.addAll(newDialogsList);
            needToLoadMore = newDialogsList.size() == Constants.CHATS_DIALOGS_PER_PAGE;
            Log.d("QBLoadDialogsCommand", "needToLoadMore = " + needToLoadMore  + "newDialogsList.size() = " + newDialogsList.size());
        } while (needToLoadMore);

        if (dialogsType == QBDialogType.GROUP) {
            tryJoinRoomChats(allDialogsList);
        }

        return allDialogsList;
    }

    private List<QBChatDialog> getPrivateDialogs(QBRequestGetBuilder qbRequestGetBuilder, Bundle returnedBundle) throws QBResponseException {
        return privateChatHelper.getDialogs(qbRequestGetBuilder, returnedBundle);
    }

    private List<QBChatDialog> getGroupDialogs(QBRequestGetBuilder qbRequestGetBuilder, Bundle returnedBundle) throws QBResponseException {
        return groupChatHelper.getDialogs(qbRequestGetBuilder, returnedBundle);
    }

    private void tryJoinRoomChats(List<QBChatDialog> allDialogsList){
        groupChatHelper.tryJoinRoomChats(allDialogsList);
    }
}