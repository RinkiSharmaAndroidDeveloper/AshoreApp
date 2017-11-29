package com.trutek.looped.data.contracts.models;


import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class CommentModel implements ISynchronizedModel{

    public String id;
    public String text;
    public Date date;
    int threadCount;

    public ActivityModel activity;
    public CommunityModel community;
    public ProfileModel profile;
    public String profileId;
    public String activityId;
    public String communityId;
    public String picUrl;
    public String name;
    boolean starred;
    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }


    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public CommunityModel getCommunity() {
        return community;
    }

    public void setCommunity(CommunityModel community) {
        this.community = community;
    }

    public ActivityModel getActivity() {
        return activity;
    }

    public void setActivity(ActivityModel activity) {
        this.activity = activity;
    }

    public ProfileModel getProfile() {
        return profile;
    }

    public void setProfile(ProfileModel profile) {
        this.profile = profile;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {

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

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
