package com.trutek.looped.data.contracts.models;


import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityModel implements ISynchronizedModel{

    public Long localId;
    public String subject;
    public String type;
    public Date dueDate;
    Date updated_At;
    public String body;
    public String picUrl;
    public String picData;
    public boolean isPrivate;
    public boolean isSelected;
    public boolean isMine;
    public ArrayList<MemberModel> participants;
    public ArrayList<MemberModel> participantIds = new ArrayList<>();
    public LocationModel location;
    public CommunityModel community;
    public ProfileModel admin;
    public LastUpdated lastUpdate;
    public String id;
    public Date timeStamp;
    public Integer syncStatus;

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
        syncStatus = status;
    }

    @Override
    public  String getServerId() {
        return id;
    }

    @Override
    public void setServerId(String id) {
        this.id = id;
    }

    public class LastUpdated implements Serializable{

        public String content;
        public ProfileModel profile;
    }

    public enum Type{

        post,
        event
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicData() {
        return picData;
    }

    public void setPicData(String picData) {
        this.picData = picData;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ArrayList<MemberModel> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<MemberModel> participants) {
        this.participants = participants;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public CommunityModel getCommunity() {
        return community;
    }

    public void setCommunity(CommunityModel community) {
        this.community = community;
    }

    public ProfileModel getAdmin() {
        return admin;
    }

    public void setAdmin(ProfileModel admin) {
        this.admin = admin;
    }

    public Date getUpdated_At() {
        return updated_At;
    }

    public void setUpdated_At(Date updated_At) {
        this.updated_At = updated_At;
    }
}
