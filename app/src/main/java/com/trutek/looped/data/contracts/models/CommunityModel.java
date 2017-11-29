package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommunityModel implements ISynchronizedModel {

    public Long localId;

    public String subject;
    public String body;
    public String picUrl;
    public String picData;
    public boolean isPrivate;
    public boolean isMine;
    public boolean canSeePost;
    ProfileModel admin;
    Integer friendsCount;
    String membersCount;
    public LocationModel location;

    public ArrayList<InterestModel> interests;
    public ArrayList<TagModel> tags;
    public ArrayList<MemberModel> members =new ArrayList<>();
    public ArrayList<ActivityModel> activities;
    public ArrayList<String> profileIds = new ArrayList<>();
    public boolean isSelected;
    public String id;
    public java.util.Date timeStamp;
    public String syncStatus;

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
        return ModelState.valueOf(syncStatus);
    }

    @Override
    public void setStatus(Integer status) {
        this.syncStatus = syncStatus;
    }

    @Override
    public  String getServerId() {
        return id;
    }

    @Override
    public void setServerId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public List<TagModel> getTags() {
        return tags;
    }

    public String getBody() {
        return body;
    }

    public String getPicData() {
        return picData;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public List<InterestModel> getInterests() {
        return interests;
    }

    public LocationModel getLocation() {
        return location;
    }

    public boolean getIsPrivate(){
        return isPrivate;
    }


    public void setBody(String body) {
        this.body = body;
    }

    public void setPicData(String picData) {
        this.picData = picData;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean getMine(){
        return isMine;
    }

    public ArrayList<MemberModel> getMembers() {
        return members;
    }

    public ProfileModel getAdmin() {
        return admin;
    }

    public void setAdmin(ProfileModel admin) {
        this.admin = admin;
    }

    public Integer getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(Integer friendsCount) {
        this.friendsCount = friendsCount;
    }
    public String getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(String membersCount) {
        this.membersCount = membersCount;
    }

    public ArrayList<String> getProfileIds() {
        return profileIds;
    }

    public void setProfileIds(ArrayList<String> profileIds) {
        this.profileIds = profileIds;
    }
}
