package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IConnectionApi;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by msas on 9/29/2016.
 */
public class ConnectionApi<TModel> extends AsyncRemoteApi<ConnectionModel> implements IConnectionApi<TModel> {

    public ConnectionApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
