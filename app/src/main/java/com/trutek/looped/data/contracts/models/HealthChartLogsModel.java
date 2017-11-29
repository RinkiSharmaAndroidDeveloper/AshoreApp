package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

/**
 * Created by Amrit on 02/12/16.
 */
public class HealthChartLogsModel implements ISynchronizedModel {

    String id;
    Long localId;
    int value;
    String unit;
    String healthChartId;
    Long healthChartLocalId;
    Date created_At;

    @Override
    public Long getId() {
        return localId;
    }

    @Override
    public void setId(Long id) {
        localId = id;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getCreate_At() {
        return created_At;
    }

    public void setCreate_At(Date created_At) {
        this.created_At = created_At;
    }

    public String getHealthChartId() {
        return healthChartId;
    }

    public void setHealthChartId(String healthChartId) {
        this.healthChartId = healthChartId;
    }

    public Long getHealthChartLocalId() {
        return healthChartLocalId;
    }

    public void setHealthChartLocalId(Long healthChartLocalId) {
        this.healthChartLocalId = healthChartLocalId;
    }
}
