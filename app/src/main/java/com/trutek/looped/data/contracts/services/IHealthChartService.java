package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;

import java.util.List;

/**
 * Created by Amrit on 02/12/16.
 */
public interface IHealthChartService extends ICRUDService<HealthChartModel> {

    void getAll(String recipientId, String intentAction);

    List<HealthChartModel> getAllFromLocal();

    boolean saveHealthCharts(List<HealthChartModel> healthChartModels);

    HealthChartModel saveHealthChart(HealthChartModel healthChartModel);

    void createHealthChart(HealthChartModel healthChartModel, String intentAction);

}
