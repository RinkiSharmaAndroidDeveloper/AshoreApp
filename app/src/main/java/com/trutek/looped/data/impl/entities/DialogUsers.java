package com.trutek.looped.data.impl.entities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "DIALOG_USERS".
 */
public class DialogUsers {

    private Long id;
    /** Not-null value. */
    private String userStatus;
    /** Not-null value. */
    private String dialogId;
    private int userId;

    public DialogUsers() {
    }

    public DialogUsers(Long id) {
        this.id = id;
    }

    public DialogUsers(Long id, String userStatus, String dialogId, int userId) {
        this.id = id;
        this.userStatus = userStatus;
        this.dialogId = dialogId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUserStatus() {
        return userStatus;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    /** Not-null value. */
    public String getDialogId() {
        return dialogId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
