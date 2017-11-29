package com.trutek.looped.data.impl.services;

import android.util.Log;

import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.data.contracts.services.IHealthChartService;
import com.trutek.looped.data.impl.entities.HealthChart;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.List;

/**
 * Created by Amrit on 02/12/16.
 */
public class HealthChartService extends BaseService<HealthChartModel> implements IHealthChartService {

    static final String TAG = HealthChartService.class.getSimpleName();

    private IRepository<HealthParameterModel> localHealthParam;
    private IRepository<HealthChartLogsModel> localHealthParamLog;
    IAsyncRemoteApi<HealthChartModel> mRemoteApi;
    String actionGetAll = "recipientId/";

    public HealthChartService(IRepository<HealthChartModel> local, IRepository<HealthParameterModel> localHealthParam,
                              IRepository<HealthChartLogsModel> localHealthParamLog, IAsyncRemoteApi<HealthChartModel> remoteApi) {
        super(local);
        this.localHealthParam = localHealthParam;
        this.localHealthParamLog = localHealthParamLog;
        mRemoteApi = remoteApi;
    }

    @Override
    public void getAll(String recipientId, final String intentAction) {
        mRemoteApi.page(new PageInput(), actionGetAll + recipientId, new AsyncResult<Page<HealthChartModel>>() {
            @Override
            public void success(Page<HealthChartModel> healthChartModelPage) {
                for (HealthChartModel healthChartModel : healthChartModelPage.items) {

                    saveHealthParam(healthChartModel);


                    HealthChartModel chartModel = _local.getByServerId(healthChartModel.getServerId());
                    if (null == chartModel) {
                        healthChartModel.setId(_local.create(healthChartModel).getId());
                    } else {
                        healthChartModel.setId(chartModel.getId());
                        update(healthChartModel,null);
                    }

                    saveHealthChartLogs(healthChartModel);
                }
                _local.notifyBroadCast(intentAction);

                Log.d(TAG, "HealthChart fetching success.");
            }

            @Override
            public void error(String error) {
                if (null != error) {
                    Log.e(TAG, "Not able to fetch healthCharts. Reason: " + error);
                }
            }
        });
    }

    @Override
    public List<HealthChartModel> getAllFromLocal() {
        return search(new PageInput()).items;
    }

    @Override
    public boolean saveHealthCharts(List<HealthChartModel> healthChartModels) {
        int savedRecords = 0;
        for (HealthChartModel healthChartModel : healthChartModels) {
            create(healthChartModel, null);
        }

        return savedRecords == healthChartModels.size();
    }

    @Override
    public HealthChartModel saveHealthChart(HealthChartModel healthChartModel) {
        return create(healthChartModel, null);
    }

    @Override
    public void createHealthChart(final HealthChartModel healthChartModel, final String intentAction) {
        mRemoteApi.create(healthChartModel, new AsyncResult<HealthChartModel>() {
            @Override
            public void success(HealthChartModel healthChartModel) {

                saveHealthParam(healthChartModel);
                saveHealthChartLogs(healthChartModel);

                _local.create(healthChartModel, intentAction);
                Log.d(TAG, "HealthChart successfully created.");
            }

            @Override
            public void error(String error) {
                if (null != error) {
                    Log.e(TAG, "Not able to create healthCharts. Reason: " + error);
                }
            }
        });
    }

    void saveHealthParam(HealthChartModel healthChartModel) {
        HealthParameterModel model = localHealthParam.getByServerId(healthChartModel.getHealthParam().getServerId());
        if (null == model) {
            healthChartModel.setHealthParam(localHealthParam.create(healthChartModel.getHealthParam()));
        } else {
            healthChartModel.getHealthParam().setId(model.getId());
            localHealthParam.update(model.getId(), healthChartModel.getHealthParam());
        }
    }
    
    void saveHealthChartLogs(HealthChartModel healthChartModel){
        for (HealthChartLogsModel healthChartLogsModel:healthChartModel.getLogs()) {
            healthChartLogsModel.setHealthChartLocalId(healthChartModel.getId());
            HealthChartLogsModel model = localHealthParamLog.getByServerId(healthChartLogsModel.getServerId());

            if(null ==  model){
                healthChartLogsModel.setId(localHealthParamLog.create(healthChartLogsModel).getId());
            }else{
                healthChartLogsModel.setId(model.getId());
                localHealthParamLog.update(model.getId(),healthChartLogsModel);
            }
        }
    }
}
