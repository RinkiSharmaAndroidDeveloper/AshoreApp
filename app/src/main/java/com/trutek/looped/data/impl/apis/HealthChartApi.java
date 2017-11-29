package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IHealthChartApi;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by Amrit on 02/12/16.
 */
public class HealthChartApi extends AsyncRemoteApi<HealthChartModel> implements IHealthChartApi<HealthChartModel> {

    public HealthChartApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
