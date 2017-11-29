package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.ITagApi;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by msas on 9/28/2016.
 */
public class TagApi <TModel> extends AsyncRemoteApi<TagModel> implements ITagApi<TModel> {

    public TagApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }

}
