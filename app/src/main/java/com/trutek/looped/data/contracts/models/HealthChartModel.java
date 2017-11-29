package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Amrit on 02/12/16.
 */
public class HealthChartModel implements ISynchronizedModel {

    String id;
    Long localId;
    RecipientModel recipient;
    HealthParameterModel healthParam;
    List<HealthChartLogsModel> logs = new ArrayList<>();


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

    public RecipientModel getRecipient() {
        return recipient;
    }

    public void setRecipient(RecipientModel recipient) {
        this.recipient = recipient;
    }

    public HealthParameterModel getHealthParam() {
        return healthParam;
    }

    public void setHealthParam(HealthParameterModel healthParam) {
        this.healthParam = healthParam;
    }

    public List<HealthChartLogsModel> getLogs() {
        return logs;
    }

    public void setLogs(List<HealthChartLogsModel> logs) {
        this.logs = logs;
    }
}
