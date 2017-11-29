package com.trutek.looped.chatmodule.data.contracts.models;

import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.DialogNotification;
import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class DialogNotificationModel implements ISynchronizedModel{

    public Long localId;
    public String notificationId;
    public String dialogId;
    public String state;
    public String body;
    public Long createdDate;
    public String type;
    public DialogUserModel dialogUser;

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedDate(Long created_date) {
        this.createdDate = created_date;
    }

    public void setState(State state) {
        this.state = state.name();
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public void setType(Type type) {
        this.type = type.name();
    }

    public String getBody() {
        return body;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public State getState() {
        return State.valueOf(state);
    }

    public Type getType() {
        return Type.valueOf(type);
    }

    public void setDialogOccupant(DialogUserModel dialogOccupant) {
        this.dialogUser = dialogOccupant;
    }

    public DialogUserModel getDialogOccupant() {
        return dialogUser;
    }

    @Override
    public Long getId() {
        return localId;
    }

    @Override
    public void setId(Long id) {
        this.localId = id;
    }

    @Override
    public Date getTimeStamp() {
        return null;
    }

    @Override
    public void setTimeStamp(Date timeStamp) {

    }

    @Override
    public ModelState getStatus() {
        return null;
    }

    @Override
    public void setStatus(Integer status) {

    }

    @Override
    public String getServerId() {
        return notificationId;
    }

    @Override
    public void setServerId(String id) {
        this.notificationId = id;
    }

    public enum Type {

        CREATE_DIALOG(25), ADDED_DIALOG(21), NAME_DIALOG(22), PHOTO_DIALOG(23), OCCUPANTS_DIALOG(24);

        private int code;

        Type(int code) {
            this.code = code;
        }

        public static Type parseByCode(int code) {
            Type[] valuesArray = Type.values();
            Type result = null;
            for (Type value : valuesArray) {
                if (value.getCode() == code) {
                    result = value;
                    break;
                }
            }
            return result;
        }

        public int getCode() {
            return code;
        }
    }
}



