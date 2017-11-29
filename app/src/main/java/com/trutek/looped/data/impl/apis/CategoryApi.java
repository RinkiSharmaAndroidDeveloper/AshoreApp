package com.trutek.looped.data.impl.apis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.ICategoryApi;
import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import java.lang.reflect.Type;

/**
 * Created by Rinki on 1/19/2017.
 */
public class CategoryApi extends AsyncRemoteApi<CategoryModel> implements ICategoryApi<CategoryModel>{
    public CategoryApi(Context context, String key, Type modelType, Type pageType, Type dataType, SQLiteDatabase database) {
        super(context, key, modelType, pageType, dataType, database);
    }
}
