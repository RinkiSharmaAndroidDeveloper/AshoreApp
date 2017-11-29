package com.trutek.looped.data.impl.entities;

/**
 * Created by Rinki on 12/3/2016.
 */
public class Medicine {
   // public String schedule_time;
    public Long localId;
    public Long id;
    public String subject;
    public String name;
    public String medicinePicUrl;
    public String dose;
    Schedules schedules;

    public Schedules getSchedules() {
        return schedules;
    }

    public void setSchedules(Schedules schedules) {
        this.schedules = schedules;
    }

    private Medicine(){

    }
    public Medicine(Long id) {
        this.id = id;
    }
    public Medicine(String dose, Long localId, Long id, String subject, String name, String medicinePicUrl) {
        this.dose = dose;
        this.localId = localId;
        this.id = id;
        this.subject = subject;
        this.name = name;
        this.medicinePicUrl = medicinePicUrl;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }
}
