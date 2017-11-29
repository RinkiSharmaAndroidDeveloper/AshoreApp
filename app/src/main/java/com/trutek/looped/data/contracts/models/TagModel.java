package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

/**
 * Created by msas on 9/28/2016.
 */
public class TagModel implements ISynchronizedModel {

    public Long localId;
    public String name;
    public boolean isSelected;
    public String id;
    public Long profileId;

    public Long getProfileId() {
        return profileId;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setProfileId(Long profileId) {

        this.profileId = profileId;
    }

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
        return null;
    }

    @Override
    public void setTimeStamp(Date timeStamp) {

    }

    public String getName() {
        return name;
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
}
