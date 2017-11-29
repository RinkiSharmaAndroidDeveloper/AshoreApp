package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IReportBugApi;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by msas on 10/20/2016.
 */
public class ReportBugApi<TModel> extends AsyncRemoteApi<ReportBugModel> implements IReportBugApi<TModel> {

    public ReportBugApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
