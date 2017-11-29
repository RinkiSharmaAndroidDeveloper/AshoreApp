package com.trutek.looped.chatmodule.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;
import java.util.List;

public class DialogModel implements ISynchronizedModel {

    public Long id;
    public String dialogId;
    public String name;
    public String lastMessage;
    public Long lastMessageDateSent;
    public Integer lastMessageUserId;
    public Integer userId;
    public String xmppRoomJid;
    public Integer unreadMessagesCount;
    public String imageUrl;
    public String type;
    public Integer syncStatus;
    public java.util.Date timeStamp;

    public DialogUserModel dialogUser;
    public List<DialogUserModel> dialogUsers;

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

    public void setType(String type) {
        this.type = type;
    }

    public void setLastMessageUserId(Integer lastMessageUserId) {
        this.lastMessageUserId = lastMessageUserId;
    }

    public void setDialogUsers(List<DialogUserModel> dialogUsers) {
        this.dialogUsers = dialogUsers;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public void setStatus(Integer status) {
        this.syncStatus = status;
    }

    @Override
    public String getServerId() {
        return dialogId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void setServerId(String id) {
        this.dialogId = id;
    }

    public void setLastMessageDateSent(Long lastMessageDateSent) {
        this.lastMessageDateSent = lastMessageDateSent;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public void setXmppRoomJid(String xmppRoomJid) {
        this.xmppRoomJid = xmppRoomJid;
    }

    public String getXmppRoomJid() {
        return xmppRoomJid;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getLastMessageDateSent() {
        return lastMessageDateSent;
    }

    public void setDialogUser(DialogUserModel dialogUser) {
        this.dialogUser = dialogUser;
    }

    public DialogUserModel getDialogUser() {
        return dialogUser;
    }

    public void setType(Type type) {
        this.type = type.name();
    }

    public Type getType() {
        return Type.valueOf(type);
    }

    public Integer getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(Integer unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }

    public Integer getLastMessageUserId() {
        return lastMessageUserId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public enum Type {

        PRIVATE(0),
        GROUP(1);

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
