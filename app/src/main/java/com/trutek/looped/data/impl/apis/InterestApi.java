package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IInterestApi;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by msas on 9/28/2016.
 */
public class InterestApi<TModel> extends AsyncRemoteApi<InterestModel> implements IInterestApi<TModel> {

    public InterestApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
