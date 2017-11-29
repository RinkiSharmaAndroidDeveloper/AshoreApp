package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IHealthChartLogApi;
import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by Amrit on 04/12/16.
 */
public class HealthChartLogApi extends AsyncRemoteApi<HealthChartLogsModel> implements IHealthChartLogApi<HealthChartLogsModel> {

    public HealthChartLogApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
