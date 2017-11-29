package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class UserModel implements ISynchronizedModel {

    public Long localId;
    public String phone;
    public String token;
    public String id;
    public String status;
    public ChatModel chat;

    public ProfileModel profile;
    DeviceModel device;

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
        return id;
    }

    @Override
    public void setServerId(String id) {
        this.id = id;
    }

    public enum Status {

        Active,
        Inactive
    }

    public DeviceModel getDevice() {
        return device;
    }

    public void setDevice(DeviceModel device) {
        this.device = device;
    }
}
