package com.trutek.looped.chatmodule.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class MessageModel implements ISynchronizedModel {

    public Long id;
    public String messageId;
    public String dialogId;
    public String body;
    public Long dateSent;
    public Integer recipientId;
    public Integer senderId;
    public String state;
    public Integer syncStatus;
    public java.util.Date timeStamp;

    public DialogModel dialog;
    public AttachmentModel attachment;
    public NotificationType notificationType;
    public DialogUserModel dialogUserModel;

    public String attachmentId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public ModelState getStatus() {
        return ModelState.fromInt(syncStatus);
    }

    @Override
    public void setStatus(Integer status) {
        this.syncStatus = status;
    }

    @Override
    public String getServerId() {
        return messageId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    @Override
    public void setServerId(String id) {
        this.messageId = id;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setAttachment(AttachmentModel attachment) {
        this.attachment = attachment;
    }

    public AttachmentModel getAttachment() {
        return attachment;
    }

    public void setState(State state) {
        this.state = state.name();
    }

    public State getState() {
        return State.valueOf(state);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public Long getDateSent() {
        return dateSent;
    }

    public void setDateSent(Long dateSent) {
        this.dateSent = dateSent;
    }

    public void setDialogUsers(DialogUserModel dialogUsers){
        DialogModel model = new DialogModel();
        model.setDialogUser(dialogUsers);
    }

    public DialogUserModel getDialogUserModel() {
        return dialogUserModel;
    }

    public void setDialogUserModel(DialogUserModel dialogUserModel) {
        this.dialogUserModel = dialogUserModel;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public DialogModel getDialog() {
        return dialog;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public String getDialogId() {
        return dialogId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setDialog(DialogModel dialog) {
        this.dialog = dialog;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isIncoming(int currentUserId) {
        return senderId != null && senderId != currentUserId;
    }
}
