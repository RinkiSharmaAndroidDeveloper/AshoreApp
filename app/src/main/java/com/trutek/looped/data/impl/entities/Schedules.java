package com.trutek.looped.data.impl.entities;

import java.util.Date;

/**
 * Created by Rinki on 12/13/2016.
 */
public class Schedules {
    private Long id;
    private String medicationId;
    private String scheduleType;
    private java.util.Date startDate;
    private java.util.Date date;


    public Schedules(Long id, String medicationId, String scheduleType, Date startDate, Date date) {
        this.id = id;
        this.medicationId = medicationId;
        this.scheduleType = scheduleType;
        this.startDate = startDate;
        this.date = date;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
