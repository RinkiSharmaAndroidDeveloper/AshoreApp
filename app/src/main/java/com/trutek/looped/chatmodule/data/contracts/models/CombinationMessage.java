package com.trutek.looped.chatmodule.data.contracts.models;

import com.trutek.looped.data.contracts.models.ActivityModel;

import java.io.Serializable;
import java.util.Comparator;

public class CombinationMessage implements Serializable {

    private String messageId;
    private DialogUserModel dialogOccupant;
    private AttachmentModel attachment;
    private State state;
    private String body;
    private long createdDate;
    private DialogNotificationModel.Type notificationType;
    private Integer senderId;

    public CombinationMessage(DialogNotificationModel dialogNotification) {
        this.messageId = dialogNotification.getNotificationId();
        this.dialogOccupant = dialogNotification.getDialogOccupant();
//        this.state = dialogNotification.getState();
        this.createdDate = dialogNotification.getCreatedDate();
        this.notificationType = dialogNotification.getType();
        this.body = dialogNotification.getBody();
    }

    public CombinationMessage(MessageModel message) {
        this.messageId = message.getMessageId();
        this.dialogOccupant = message.getDialogUserModel();
        this.attachment = message.getAttachment();
//        this.state = message.getState();
        this.body = message.getBody();
        this.createdDate = message.getDateSent();
        this.senderId = message.getSenderId();
    }

    public MessageModel toMessage() {
        MessageModel message = new MessageModel();
        message.setMessageId(messageId);
        message.setDialogUserModel(dialogOccupant);
        message.setAttachment(attachment);
//        message.setState(state);
        message.setBody(body);
        message.setDateSent(createdDate);
        return message;
    }

    public DialogNotificationModel toDialogNotification() {
        DialogNotificationModel dialogNotification = new DialogNotificationModel();
        dialogNotification.setNotificationId(messageId);
        dialogNotification.setDialogOccupant(dialogOccupant);
//        dialogNotification.setState(state);
        dialogNotification.setType(notificationType);
        dialogNotification.setBody(body);
        dialogNotification.setCreatedDate(createdDate);
        return dialogNotification;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public DialogNotificationModel.Type getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(DialogNotificationModel.Type notificationType) {
        this.notificationType = notificationType;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public AttachmentModel getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentModel attachment) {
        this.attachment = attachment;
    }

    public DialogUserModel getDialogOccupant() {
        return dialogOccupant;
    }

    public void setDialogOccupant(DialogUserModel dialogOccupant) {
        this.dialogOccupant = dialogOccupant;
    }

    public boolean isIncoming(int currentUserId) {
        return senderId != null && senderId != currentUserId;
    }

    public static class DateComparator implements Comparator<CombinationMessage> {

        @Override
        public int compare(CombinationMessage combinationMessage1, CombinationMessage combinationMessage2) {
            return ((Long) combinationMessage1.getCreatedDate()).compareTo(
                    ((Long) combinationMessage2.getCreatedDate()));
        }
    }

}
