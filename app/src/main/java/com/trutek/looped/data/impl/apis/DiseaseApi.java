package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IDiseaseApi;
import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

public class DiseaseApi extends AsyncRemoteApi<DiseaseModel> implements IDiseaseApi<DiseaseModel> {

    public DiseaseApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
