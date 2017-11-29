package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class LoopModel implements ISynchronizedModel{

    public Long localId;
    public String name;
    public String status;
    public String role = "contributor";
    public String profileId;
    public String picUrl;

    public String id;
    public String recipientId;
    public java.util.Date timeStamp;
    public String syncStatus;

    public ProfileModel profile;

    @Override
    public Long getId() {
        return localId;
    }

    @Override
    public void setId(Long id) {
        this.localId = id;
    }

    public String getRole() {
        return role;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getName() {
        return name;
    }

    public ProfileModel getProfile() {
        return profile;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setProfile(ProfileModel profile) {
        this.profile = profile;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
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

    public String getLoopStatus() {
        return status;
    }

    public void setLoopStatus(String loopStatus) {
        this.status = loopStatus;
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
