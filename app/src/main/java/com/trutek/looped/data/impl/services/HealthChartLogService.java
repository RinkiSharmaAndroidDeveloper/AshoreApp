package com.trutek.looped.data.impl.services;

import android.util.Log;

import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.services.IHealthChartLogService;
import com.trutek.looped.data.impl.repositories.HealthParamLogRepository;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.Date;
import java.util.List;

/**
 * Created by Amrit on 04/12/16.
 */
public class HealthChartLogService extends BaseService<HealthChartLogsModel> implements IHealthChartLogService {

    final String TAG = HealthChartLogService.class.getSimpleName();

    final static String TO_DATE = "toDate", FROM_DATE = "fromDate";

    private IAsyncRemoteApi<HealthChartLogsModel> remoteApi;

    public HealthChartLogService(IRepository<HealthChartLogsModel> local, IAsyncRemoteApi<HealthChartLogsModel> remoteApi) {
        super(local);
        this.remoteApi = remoteApi;
    }

    @Override
    public void createHealthChartLog(final HealthChartLogsModel healthChartLogsModel, final String intentAction) {
        remoteApi.create(healthChartLogsModel, new AsyncResult<HealthChartLogsModel>() {
            @Override
            public void success(HealthChartLogsModel model) {
                model.setHealthChartLocalId(healthChartLogsModel.getHealthChartLocalId());
                saveHealthChartLog(model, intentAction);
            }
            @Override
            public void error(String error) {
                if (null != error) {
                    Log.e(TAG, "Not able to create healthCharts. Reason: " + error);
                }
            }
        });
    }

    @Override
    public void saveHealthChartLog(HealthChartLogsModel healthChartLogsModel, String intentAction) {
        create(healthChartLogsModel, intentAction);
    }

    @Override
    public void getAllLogs(Date fromDate, Date toDate, String healthChartId, final String intentAction) {
        PageInput input = new PageInput();
        input.query.add(FROM_DATE, DateHelper.stringify(fromDate, DateHelper.StringifyAs.Utc));
        input.query.add(TO_DATE, DateHelper.stringify(toDate, DateHelper.StringifyAs.Utc));

        remoteApi.page(input, healthChartId, new AsyncResult<Page<HealthChartLogsModel>>() {
            @Override
            public void success(Page<HealthChartLogsModel> healthChartLogsModelPage) {
                for (HealthChartLogsModel healthChartLogsModel:healthChartLogsModelPage.items) {
                    HealthChartLogsModel model = _local.getByServerId(healthChartLogsModel.getServerId());

                    if(null == model){
                        create(healthChartLogsModel,null);
                    }else{
                        healthChartLogsModel.setId(model.getId());
                        update(healthChartLogsModel,null);
                    }
                }
                _local.notifyBroadCast(intentAction);
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
    public List<HealthChartLogsModel> getAllLogsLocally(Date fromDate,Date toDate, Long healthChartId) {
        PageInput input = new PageInput();
        input.query.add(FROM_DATE,fromDate);
        input.query.add(TO_DATE,toDate);
        input.query.add(HealthParamLogRepository.QUERY_KEY_BY_HEALTH_CHART_ID,healthChartId);
        return search(input).items;
    }

    @Override
    public void updateHealthChartLog(String healthChartAction, HealthChartLogsModel healthChartLogsModel, AsyncResult<HealthChartLogsModel> result) {
                remoteApi.update(healthChartAction,healthChartLogsModel,result);
    }

    @Override
    public void deleteHealthChartLog(String healthchartid, AsyncNotify result) {

    }

}
