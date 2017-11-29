package com.trutek.looped.chatmodule.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

/**
 * Created by msas on 9/15/2016.
 */
public class DialogUserModel implements ISynchronizedModel{

    public Long id;
    public String dialogId;
    public int userId;
    public String userStatus = Status.ACTUAL.name();

    public ChatUserModel chatUser;
    public DialogModel dialog;

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
        return null;
    }

    @Override
    public void setServerId(String id) {

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public void setChatUser(ChatUserModel chatUser) {
        this.chatUser = chatUser;
    }

    public ChatUserModel getChatUser() {
        return chatUser;
    }

    public void setDialog(DialogModel dialog) {
        this.dialog = dialog;
    }

    public DialogModel getDialog() {
        return dialog;
    }

    public enum Status {

        ACTUAL(0),
        DELETED(1);

        private int code;

        Status(int code) {
            this.code = code;
        }

        public static Status parseByCode(int code) {
            Status[] valuesArray = Status.values();
            Status result = null;
            for (Status value : valuesArray) {
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


    public Status getUserStatus() {
        return Status.valueOf(userStatus);
    }

    public void setUserStatus(Status userStatus) {
        this.userStatus = userStatus.name();
    }
}
