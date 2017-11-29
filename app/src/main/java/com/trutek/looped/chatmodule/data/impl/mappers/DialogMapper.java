package com.trutek.looped.chatmodule.data.impl.mappers;

import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.data.impl.entities.Dialog;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by msas on 9/15/2016.
 */
public class DialogMapper implements IModelMapper<Dialog, DialogModel> {

    @Override
    public DialogModel Map(Dialog dialog) {
        DialogModel model = new DialogModel();
        model.setId(dialog.getId());
        model.setDialogId(dialog.getDialogId());
        model.setLastMessage(dialog.getLastMessage());
        model.setLastMessageDateSent(dialog.getLastMessageDateSent());
        model.setImageUrl(dialog.getImageUrl());
        model.setName(dialog.getName());
        model.setUnreadMessagesCount(dialog.getUnreadMessagesCount());
        model.setType(DialogModel.Type.valueOf(dialog.getType()));
        model.setStatus(dialog.getStatus());
        model.setUserId(dialog.getUserId());
        model.setLastMessageUserId(dialog.getLastMessageUserId());
        model.setXmppRoomJid(dialog.getXmppRoomJid());

        model.setTimeStamp(dialog.getTimeStamp());
        return model;
    }
}
