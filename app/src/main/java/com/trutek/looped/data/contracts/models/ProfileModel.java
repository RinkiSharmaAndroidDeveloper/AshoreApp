package com.trutek.looped.data.contracts.models;


import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileModel implements ISynchronizedModel{
    public static final String KEY_MINE="isMine";
    public static final String KEY_PROFILE_ID="profileId";
    public Long localId;
    public String name;
    public java.util.Date dateOfBirth;
    public int age;
    public String birthday;
    public String gender;
    public String about;
    public Boolean isMine;
    public String picUrl;
    public String myConnectionStatus;

    public Boolean getMine() {
        return isMine;
    }

    public void setMine(Boolean mine) {
        isMine = mine;
    }

    public String id;
    public String facebookId;
    public java.util.Date timeStamp;
    public Integer syncStatus;
    public Long locationId;

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public LocationModel location;
    public ChatModel chat;
    public String status;

    public boolean isSelected;
    public ArrayList<RecipientModel> recipients;

    public List<InterestModel> interests=new ArrayList<>();
    public List<TagModel> tags=new ArrayList<>();
    List<CategoryModel> categories = new ArrayList<>();

    public ProfileModel(){

    }

    public String getName() {
        return name;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getage() {
        return age;
    }

    public void setAge(int  age) {
        this.age = age;
    }

    @Override
    public Long getId() {
        return localId;
    }

    @Override
    public void setId(Long id) {
        this.localId = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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
        return id;
    }

    @Override
    public void setServerId(String id) {
        this.id = id;
    }

    public ArrayList<RecipientModel> getRecipients() {
        return recipients;
    }

    public void setRecipients(ArrayList<RecipientModel> recipients) {
        this.recipients = recipients;
    }

    public ChatModel getChat() {
        return chat;
    }
    public List<InterestModel> getInterests() {
        return interests;
    }

    public void setInterests(List<InterestModel> interests) {
        if(null == interests){
            this.interests=new ArrayList<>();
        }else {
            this.interests = interests;
        }
    }
    public List<TagModel> getTags() {
        return tags;
    }

    public void setTags(List<TagModel> tags) {
        if(null == tags){
            this.tags =new ArrayList<>();
        }else {
            this.tags = tags;
        }
    }

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryModel> categories) {
        this.categories = categories;
    }

    public enum Status{

        Active,
        Incomplete,
        InComming
    }
}
