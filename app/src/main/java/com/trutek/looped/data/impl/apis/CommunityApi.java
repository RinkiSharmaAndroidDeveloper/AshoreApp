package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.ICommunityApi;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.DataModel;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by msas on 9/28/2016.
 */
public class CommunityApi<TModel> extends AsyncRemoteApi<CommunityModel> implements ICommunityApi<TModel> {

    public CommunityApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }

    @Override
    public void joinCommunity(final String action, final CommunityModel model, final AsyncResult<CommunityModel> result) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CommunityModel remoteModel = _remoteRepository.create(action, model);
                    result.success(remoteModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

    @Override
    public void joinCommunityMember(final String action, final CommunityModel model, final AsyncResult<Page<CommunityModel>> result) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Page<CommunityModel> remoteModel = _remoteRepository.createPage(action, model);
                    result.success(remoteModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

   /* @Override
    public void leaveCommunity(final CommunityModel action, final AsyncNotify notify) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CommunityModel remoteModel = _remoteRepository.deleteData(action);
                    notify.success();
                } catch (Exception ex) {
                    ex.printStackTrace();

                }
            }
        });
    }*/
    }





