package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IMedicineApi;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;
import java.lang.reflect.Type;

/**
 * Created by Rinki on 12/3/2016.
 */
public class MedicineApi extends AsyncRemoteApi<MedicineModel> implements IMedicineApi<MedicineModel> {

    public MedicineApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}