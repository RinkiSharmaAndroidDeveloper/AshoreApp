package com.trutek.looped.chatmodule.data.contracts.models;


import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class ChatUserModel implements ISynchronizedModel{

    public Long id;
    public Integer userId;
    public String name;
    public String number;
    public String email;
    public String login;
    public java.util.Date lastRequestAt;
    public String role;
    public java.util.Date timeStamp;
    public Integer syncStatus;

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
        return null;
    }

    @Override
    public void setServerId(String id) {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setLastRequestAt(Date lastRequestAt) {
        this.lastRequestAt = lastRequestAt;
    }

    public Date getLastRequestAt() {
        return lastRequestAt;
    }

    public void setRole(Role role) {
        this.role = role.name();
    }

    public Role getRole() {
        return Role.valueOf(role);
    }

    public enum Role {

        OWNER(0),
        SIMPLE_ROLE(1);

        private int code;

        Role(int code) {
            this.code = code;
        }

        public static Role parseByCode(int code) {
            Role[] valuesArray = Role.values();
            Role result = null;
            for (Role value : valuesArray) {
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
