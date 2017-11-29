package com.trutek.looped.chatmodule.data.impl.mappers;

import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.State;
import com.trutek.looped.data.impl.entities.DialogNotification;
import com.trutek.looped.msas.common.contracts.IModelMapper;

public class DialogNotificationMapper implements IModelMapper<DialogNotification, DialogNotificationModel> {

    @Override
    public DialogNotificationModel Map(DialogNotification dialogNotification) {
        DialogNotificationModel model = new DialogNotificationModel();
        model.setId(dialogNotification.getId());
        model.setNotificationId(dialogNotification.getNotificationId());
        model.setBody(dialogNotification.getBody());
        model.setType(DialogNotificationModel.Type.valueOf(dialogNotification.getType()));
        model.setState(State.valueOf(dialogNotification.getState()));
        model.setCreatedDate(dialogNotification.getCreatedDate());
        model.setDialogId(dialogNotification.getDialogId());
        return model;
    }
}
