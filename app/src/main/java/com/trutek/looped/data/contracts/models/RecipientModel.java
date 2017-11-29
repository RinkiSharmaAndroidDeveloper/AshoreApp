package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecipientModel implements ISynchronizedModel {

    public Long localId;
    public String name;
    public String gender;
    public int age;
    public String picUrl;
    public Long jabberId;
    public String id;
    public java.util.Date timeStamp;
    public String syncStatus;
    public ChatModel chat;
    ArrayList<ProviderModel> providers = new ArrayList<>();

    public ArrayList<String> diseaseIds;
    public List<DiseaseModel> diseases;
    public List<LoopModel> loops;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ArrayList<String> getDiseasesId() {
        return diseaseIds;
    }

    public void setDiseasesId(ArrayList<String> diseaseIds) {
        this.diseaseIds = diseaseIds;
    }

    public List<DiseaseModel> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<DiseaseModel> diseaseIds) {
        this.diseases = diseaseIds;

        if(this.diseases != null && this.diseases.size() > 0){
            this.diseaseIds = new ArrayList<>();
            for (DiseaseModel model : this.diseases) {
                this.diseaseIds.add(model.getServerId());
            }
        }

    }

    public void setLoops(List<LoopModel> loops) {
        this.loops = loops;
    }

    public List<LoopModel> getLoops() {
        return loops;
    }

    public ArrayList<ProviderModel> getProviders() {
        return providers;
    }

    public void setProviders(ArrayList<ProviderModel> providers) {
        this.providers = providers;
    }
}
