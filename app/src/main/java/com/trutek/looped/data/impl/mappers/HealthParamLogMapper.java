package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.impl.entities.HealthParamLog;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by Amrit on 10/12/16.
 */
public class HealthParamLogMapper implements IModelMapper<HealthParamLog,HealthChartLogsModel> {
    @Override
    public HealthChartLogsModel Map(HealthParamLog healthParamLog) {
        HealthChartLogsModel healthChartLogsModel = new HealthChartLogsModel();
        healthChartLogsModel.setId(healthParamLog.getId());
        healthChartLogsModel.setServerId(healthParamLog.getServerId());
        healthChartLogsModel.setCreate_At(healthParamLog.getCreatedAt());
        healthChartLogsModel.setValue(healthParamLog.getValue());
        healthChartLogsModel.setUnit(healthParamLog.getUnit());
        healthChartLogsModel.setHealthChartLocalId(healthParamLog.getHealthChartId());
        return healthChartLogsModel;
    }
}
