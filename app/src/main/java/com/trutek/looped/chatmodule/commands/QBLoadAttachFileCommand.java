package com.trutek.looped.chatmodule.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.content.model.QBFile;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.msas.common.commands.ServiceCommand;

import java.io.File;

public class QBLoadAttachFileCommand extends ServiceCommand {

    private static final String TAG = QBLoadAttachFileCommand.class.getSimpleName();

    private final PrivateChatHelper privateChatHelper;

    public QBLoadAttachFileCommand(Context context, PrivateChatHelper privateChatHelper,
                                   String successAction, String failAction) {
        super(context, successAction, failAction);
        this.privateChatHelper = privateChatHelper;
    }

    public static void start(Context context, File file) {
        Intent intent = new Intent(QuickBloxServiceConsts.LOAD_ATTACH_FILE_ACTION, null, context, QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_FILE, file);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        File file = (File) extras.getSerializable(QuickBloxServiceConsts.EXTRA_FILE);

        if(null == file){
            Log.e(TAG,"Attach File: Chat image is null");
        }else{
            Log.e(TAG,"Attach File: File path" + file.getAbsolutePath());
        }

        QBFile qbFile = privateChatHelper.loadAttachFile(file);

        Bundle result = new Bundle();
        result.putSerializable(QuickBloxServiceConsts.EXTRA_ATTACH_FILE, qbFile);

        return result;
    }
}