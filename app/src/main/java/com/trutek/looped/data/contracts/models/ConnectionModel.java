package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by msas on 9/29/2016.
 */
public class ConnectionModel implements ISynchronizedModel{
    public static final String KEY_MINE="isMine";

    public Long localId;
    public String status;
    public ProfileModel profile;
    public Date date;
    public String id;

    public boolean isSelected;
    public boolean isMine;
    public String picUrl;
    public String  name;

    public boolean isSelected() {
        return isSelected;
    }

    public boolean setSelected(boolean selected) {
        isSelected = selected;
        return selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMine() {

        return isMine;
    }

    public void setProfile(ProfileModel profile) {
        this.profile = profile;
    }

    public void setMine(boolean mine) {
        isMine = mine;
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
        return id;
    }

    @Override
    public void setServerId(String id) {
        this.id = id;
    }

    public ProfileModel getProfile() {
        return profile;
    }
}
