package com.trutek.looped.chatmodule.data.impl.mappers;

import com.trutek.looped.chatmodule.data.contracts.models.AttachmentModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.contracts.services.IDialogService;
import com.trutek.looped.data.impl.entities.Message;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.ModelState;

/**
 * Created by msas on 9/15/2016.
 */
public class MessageMapper implements IModelMapper<Message, MessageModel> {

    private IRepository<DialogModel> dialogs;
    private IRepository<DialogUserModel> dialogUsers;
    private IRepository<AttachmentModel> attachments;

    public MessageMapper (IRepository<DialogModel> dialogs , IRepository<DialogUserModel> dialogUsers, IRepository<AttachmentModel> attachments){

        this.dialogs = dialogs;
        this.dialogUsers = dialogUsers;
        this.attachments = attachments;
    }

    @Override
    public MessageModel Map(Message model) {
        MessageModel message = new MessageModel();
        message.setId(model.getId());
        message.setMessageId(model.getMessageId());
        message.setDialogId(model.getDialogId());
        message.setBody(model.getBody());
        message.setDateSent(model.getDateSent());
        message.setRecipientId(model.getRecipientId());
        message.setSenderId(model.getSenderId());
        message.setState(model.getState());
//        message.setStatus(ModelState.model.getStatus());
        message.setTimeStamp(model.getTimeStamp());

        if(model.getDialogId() != null){
            message.setDialog(dialogs.getByServerId(model.getDialogId()));
        }
        if(model.getDialogId() != null && model.getSenderId() != null){
            message.setDialogUserModel(dialogUsers.get(new PageQuery().add("dialogId", model.getDialogId()).add("userId", model.getSenderId())));
        }
        if(model.getAttachmentId() != null){
            message.setAttachment(attachments.getByServerId(model.getAttachmentId()));
        }

        return message;
    }
}
