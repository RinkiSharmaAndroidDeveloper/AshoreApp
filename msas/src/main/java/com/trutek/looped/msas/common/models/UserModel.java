package com.trutek.looped.msas.common.models;

public class UserModel {

    public int id;
    public String name;
    public String gender;
    public String email;
    public String phone;
    public String password;
    public String token;
    public int qbId;
    public String qbPassword;
    public String serverId;
    public String userPicUrl;
    public String userProfileId;
    public String facebookId;
    public String facebookPic;
    public String profileStatus;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public int getQbId() {
        return qbId;
    }

    public String getQbPassword() {
        return qbPassword;
    }

    public String getPhone(){
        return phone;
    }

    public String getServerId() {
        return serverId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getFacebookPic() {
        return facebookPic;
    }

    public String getProfileComplete(){
        return profileStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.name = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setQbId(int qbId) {
        this.qbId = qbId;
    }

    public void setQbPassword(String qbPassword) {
        this.qbPassword = qbPassword;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setProfileComplete(String profileStatus){
        this.profileStatus = profileStatus;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setFacebookPic(String facebookId) {
        this.facebookPic = facebookId;
    }

    public String getUserPicUrl() {
        return userPicUrl;
    }

    public void setUserPicUrl(String userPicUrl) {
        this.userPicUrl = userPicUrl;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }
}
