package com.trutek.looped.data.impl.apis;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IRecipientApi;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

public class RecipientApi extends AsyncRemoteApi<RecipientModel> implements IRecipientApi<RecipientModel> {

    public RecipientApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
