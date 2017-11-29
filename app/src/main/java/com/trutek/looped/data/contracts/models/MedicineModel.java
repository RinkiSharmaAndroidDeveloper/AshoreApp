package com.trutek.looped.data.contracts.models;
import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rinki on 12/1/2016.
 */
public class MedicineModel implements ISynchronizedModel {
    public String schedule_time;
    public Long localId;
    public String id;
    public String subject;
    public String name;
    public String medicinePicUrl;
    public String dose;
    String recipientId,medID;
    public List<ScheduleModel> schedules;
    ScheduleModel schedule;

    public ScheduleModel getScheduleModel() {
        return schedule;
    }

    public void setScheduleModel(ScheduleModel scheduleModel) {
        this.schedule = scheduleModel;
    }

    public List<ScheduleModel> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleModel> schedules) {
        this.schedules = schedules;
    }

    public String server_Id ="58491ab5b19f6960317d920c"; //TODO for the time being


    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedicinePicUrl() {
        return medicinePicUrl;
    }

    public void setMedicinePicUrl(String medicinePicUrl) {
        this.medicinePicUrl = medicinePicUrl;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
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

    public String getRecipientId() {
        return recipientId;
    }

    public String getMedID() {
        return medID;
    }

    public void setMedID(String medID) {
        this.medID = medID;
    }

    public void setRecipientId(String recipientId) {

        this.recipientId = recipientId;
    }
}
