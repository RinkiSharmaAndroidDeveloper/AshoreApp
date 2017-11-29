package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.INotificationApi;
import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

public class NotificationApi<TModel> extends AsyncRemoteApi<NotificationModel> implements INotificationApi<TModel> {

    public NotificationApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
