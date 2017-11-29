package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;

import java.util.Date;
import java.util.List;

/**
 * Created by Amrit on 04/12/16.
 */
public interface IHealthChartLogService extends ICRUDService<HealthChartLogsModel> {

    void createHealthChartLog(HealthChartLogsModel healthChartLogsModel, String intentAction);

    void saveHealthChartLog(HealthChartLogsModel healthChartLogsModel, String intentAction);

    void getAllLogs(Date fromDate, Date toDate, String healthChartId, String intentAction);

    List<HealthChartLogsModel> getAllLogsLocally(Date fromDate,Date toDate, Long healthChartId);

    void updateHealthChartLog(String healthChartAction, HealthChartLogsModel healthChartLogsModel, AsyncResult<HealthChartLogsModel> result);

    void deleteHealthChartLog(String healthchartid, AsyncNotify result);

}
