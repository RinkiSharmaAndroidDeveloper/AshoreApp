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
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.impl.entities.Dialog;
import com.trutek.looped.msas.common.commands.ServiceCommand;
import com.trutek.looped.utils.cloudinary.CloudinaryHelper;

import java.io.File;
import java.util.Map;

public class QBUpdateGroupDialogCommand extends ServiceCommand {

    private GroupChatHelper multiChatHelper;
    private CloudinaryHelper cloudinaryHelper;

    public QBUpdateGroupDialogCommand(Context context, GroupChatHelper multiChatHelper, CloudinaryHelper cloudinaryHelper,
                                      String successAction, String failAction) {
        super(context, successAction, failAction);
        this.multiChatHelper = multiChatHelper;
        this.cloudinaryHelper = cloudinaryHelper;
    }

    public static void start(Context context, QBChatDialog dialog, File file) {
        Intent intent = new Intent(QuickBloxServiceConsts.UPDATE_GROUP_DIALOG_ACTION, null, context, QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG, dialog);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_FILE, file);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        QBChatDialog dialog = (QBChatDialog) extras.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);
        File file = (File) extras.getSerializable(QuickBloxServiceConsts.EXTRA_FILE);

        if(file == null) {
            dialog = multiChatHelper.updateDialog(dialog);
        } else {
            CloudinaryModel model;
            Map map = cloudinaryHelper.uploadImage(file);
            if(map != null){
                model = new CloudinaryModel();
                model.public_id = map.get("public_id").toString();
                model.signature = map.get("signature").toString();
                model.width = (Integer) map.get("width");
                model.height = (Integer) map.get("height");
                model.format = map.get("format").toString();
                model.resource_type = map.get("resource_type").toString();
                model.url = map.get("url").toString();
                model.secure_url = map.get("secure_url").toString();

                dialog = multiChatHelper.updateDialog(dialog, model.url);
            }
        }

        if (dialog != null) {
            DialogModel dialogModel = ChatUtils.createLocalDialog(dialog);
            dialogModel.setId(DataManager.getInstance().getDialogRepository().getByServerId(dialog.getDialogId()).getId());

            DataManager.getInstance().getDialogRepository().update(dialogModel.getId(), dialogModel,null);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(QuickBloxServiceConsts.EXTRA_DIALOG, dialog);

        return bundle;
    }
}