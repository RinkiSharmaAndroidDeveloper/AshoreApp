package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;
import com.trutek.looped.data.contracts.apis.IProfileApi;
import com.trutek.looped.data.contracts.models.ProfileModel;

import java.lang.reflect.Type;

public class ProfileApi<TModel> extends AsyncRemoteApi<ProfileModel> implements IProfileApi<TModel> {

    public ProfileApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }


    @Override
    public void sendInvitation(InviteModel model, final AsyncResult<InviteModel> result) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                result.success(new InviteModel());
            }
        });
    }
}
