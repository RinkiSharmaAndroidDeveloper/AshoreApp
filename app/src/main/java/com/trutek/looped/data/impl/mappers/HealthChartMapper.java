package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.data.impl.entities.HealthChart;
import com.trutek.looped.data.impl.entities.HealthParam;
import com.trutek.looped.data.impl.repositories.HealthParamLogRepository;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.List;

/**
 * Created by Amrit on 03/12/16.
 */
public class HealthChartMapper implements IModelMapper<HealthChart,HealthChartModel> {


    private IRepository<HealthParameterModel> localHealthParameterRepo;
    private IRepository<HealthChartLogsModel> localHealthParamLogRepo;

    public HealthChartMapper(IRepository<HealthParameterModel> localHealthParameterRepo,
                             IRepository<HealthChartLogsModel> localHealthParamLogRepo) {

        this.localHealthParameterRepo = localHealthParameterRepo;
        this.localHealthParamLogRepo = localHealthParamLogRepo;
    }

    @Override
    public HealthChartModel Map(HealthChart healthChart) {
        HealthChartModel healthChartModel =new HealthChartModel();
        healthChartModel.setId(healthChart.getId());
        healthChartModel.setServerId(healthChart.getServerId());
        healthChartModel.setHealthParam(localHealthParameterRepo.get(healthChart.getHealthParamId()));
        healthChartModel.setLogs(getLogs(healthChart.getId()));
        return healthChartModel;
    }

    List<HealthChartLogsModel> getLogs(Long healthChartId){
        PageQuery query = new PageQuery(HealthParamLogRepository.QUERY_KEY_BY_HEALTH_CHART_ID,healthChartId);
        return localHealthParamLogRepo.page(new PageInput(query)).items;
    }
}
