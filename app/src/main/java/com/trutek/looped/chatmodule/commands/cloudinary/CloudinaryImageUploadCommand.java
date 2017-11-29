package com.trutek.looped.chatmodule.commands.cloudinary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.content.model.QBFile;
import com.trutek.looped.chatmodule.helpers.PrivateChatHelper;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.msas.common.commands.ServiceCommand;
import com.trutek.looped.utils.cloudinary.CloudinaryHelper;

import java.io.File;
import java.util.Map;

public class CloudinaryImageUploadCommand extends ServiceCommand {

    private static final String TAG = CloudinaryImageUploadCommand.class.getSimpleName();
    private CloudinaryHelper cloudinaryHelper;

    public CloudinaryImageUploadCommand(Context context, CloudinaryHelper cloudinaryHelper,
                                        String successAction, String failAction) {
        super(context, successAction, failAction);
        this.cloudinaryHelper = cloudinaryHelper;
    }

    public static void start(Context context, File file) {
        Intent intent = new Intent(QuickBloxServiceConsts.CLOUDINARY_LOAD_ACTION, null, context, QuickBloxService.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_FILE, file);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        File file = (File) extras.getSerializable(QuickBloxServiceConsts.EXTRA_FILE);
        CloudinaryModel model = null;

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
        }

        Bundle result = new Bundle();
        result.putSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL, model);

        return result;
    }
}