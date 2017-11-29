package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISyncService;
import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rinki on 12/3/2016.
 */
public class ScheduleModel implements ISynchronizedModel {
    public String medicationId;
    public String scheduleType;
    public Date date;
    public List<String> types = new ArrayList<>();
    public List<Date> timings =new ArrayList<>();

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<Date> getTimings() {
        return timings;
    }

    public void setTimings(List<Date> timings) {
        this.timings = timings;
    }

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public Date getDate() {
        return date;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public void setDate(Date date) {
        this.date = date;
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
        return null;
    }

    @Override
    public void setServerId(String id) {

    }
    public class ScheduleType implements Serializable {

        public String ScheduleType;
    }

}
