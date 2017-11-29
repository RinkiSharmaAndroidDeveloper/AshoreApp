package com.trutek.looped.chatmodule.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.chat.Consts;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.trutek.looped.chatmodule.helpers.BaseChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.commands.ServiceCommand;

import java.util.List;

public class QBLoadDialogMessagesCommand extends ServiceCommand {

    private BaseChatHelper baseChatHelper;

    public QBLoadDialogMessagesCommand(Context context, BaseChatHelper baseChatHelper, String successAction,
                                       String failAction) {
        super(context, successAction, failAction);
        this.baseChatHelper = baseChatHelper;
    }

    public static void start(Context context, QBChatDialog dialog, long lastDateLoad, boolean loadMore) {
        Intent intent = new Intent(QuickBloxServiceConsts.LOAD_DIALOG_MESSAGES_ACTION, null, context,
                QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG, dialog);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DATE_LAST_UPDATE_HISTORY, lastDateLoad);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_LOAD_MORE, loadMore);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        QBChatDialog dialog = (QBChatDialog) extras.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);
        long lastDateLoad = extras.getLong(QuickBloxServiceConsts.EXTRA_DATE_LAST_UPDATE_HISTORY);
        boolean loadMore = extras.getBoolean(QuickBloxServiceConsts.EXTRA_LOAD_MORE);

        Bundle returnedBundle = new Bundle();
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(Constants.DIALOG_MESSAGES_PER_PAGE);

        if (loadMore) {
            customObjectRequestBuilder.lt(Consts.MESSAGE_DATE_SENT, lastDateLoad);
            customObjectRequestBuilder.sortDesc(QuickBloxServiceConsts.EXTRA_DATE_SENT);
        } else {
            customObjectRequestBuilder.gt(Consts.MESSAGE_DATE_SENT, lastDateLoad);
            if (lastDateLoad > 0) {
                customObjectRequestBuilder.sortAsc(QuickBloxServiceConsts.EXTRA_DATE_SENT);
            } else {
                customObjectRequestBuilder.sortDesc(QuickBloxServiceConsts.EXTRA_DATE_SENT);
            }
        }

        List<QBChatMessage> dialogMessagesList = baseChatHelper.getDialogMessages(customObjectRequestBuilder,
                returnedBundle, dialog, lastDateLoad);

        Bundle bundleResult = new Bundle();
        bundleResult.putSerializable(QuickBloxServiceConsts.EXTRA_DIALOG_MESSAGES, (java.io.Serializable) dialogMessagesList);
        bundleResult.putInt(QuickBloxServiceConsts.EXTRA_TOTAL_ENTRIES, dialogMessagesList != null
                ?  dialogMessagesList.size() : Constants.ZERO_INT_VALUE);

        return bundleResult;
    }
}