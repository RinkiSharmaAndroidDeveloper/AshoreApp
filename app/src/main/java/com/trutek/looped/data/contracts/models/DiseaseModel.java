package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class DiseaseModel implements ISynchronizedModel {

    public Long localId;
    public String name;
    public String id;
    public java.util.Date timeStamp;
    public String syncStatus;

    public String recipientId;
    public boolean isSelected;

    @Override
    public Long getId() {
        return localId;
    }

    @Override
    public void setId(Long id) {
        localId = id;
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
        return ModelState.valueOf(syncStatus);
    }

    @Override
    public void setStatus(Integer status) {
        this.syncStatus = ModelState.fromInt(status).name();
    }

    @Override
    public String getServerId() {
        return id;
    }

    @Override
    public void setServerId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
}
