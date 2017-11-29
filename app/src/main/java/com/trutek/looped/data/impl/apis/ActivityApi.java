package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IActivityApi;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by msas on 10/10/2016.
 */
public class ActivityApi<TModel> extends AsyncRemoteApi<ActivityModel> implements IActivityApi<TModel> {

    public ActivityApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }

    @Override
    public void joinActivity(final String action, final ActivityModel model, final AsyncResult<ActivityModel> result) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ActivityModel remoteModel = _remoteRepository.create(action, model);
                    result.success(remoteModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

}
