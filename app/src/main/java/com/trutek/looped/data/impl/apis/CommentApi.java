package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.ICommentApi;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by msas on 10/21/2016.
 */
public class CommentApi<TModel> extends AsyncRemoteApi<CommentModel> implements ICommentApi<TModel> {

    public CommentApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
