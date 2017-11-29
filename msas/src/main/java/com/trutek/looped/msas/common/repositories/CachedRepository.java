package com.trutek.looped.msas.common.repositories;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.lang.reflect.Type;


public class CachedRepository<TModel> {

    private final String _key;
    private final Type _modelType;
    private Gson _gson = new Gson();
    private Type _pageType;
    private SQLiteDatabase _database;

    public CachedRepository(String key, Type modelType, Type pageType, SQLiteDatabase database) {
        _modelType = modelType;
        _pageType = pageType;
        _database = database;
        _key = key + "/";
    }

    public TModel get(String id) {
        String key = _key + String.valueOf(id);

        String json = "";

        Cursor cr = _database.rawQuery("SELECT VALUE FROM CACHE WHERE KEY = '" + key + "'", null);
        if (cr.moveToFirst()) {
            json = cr.getString(cr.getColumnIndex("VALUE"));
        }
        cr.close();

        if (json.isEmpty()) {
            return null;
        }

        return _gson.fromJson(json, _modelType);
    }

    public Page<TModel> page(PageInput input) {


        String key = _key + input.toString();
        String json = "";
      /*
        Cursor cr = _database.rawQuery("SELECT VALUE FROM CACHE WHERE KEY = '" + key + "'", null);
        if (cr.moveToFirst()) {
            json = cr.getString(cr.getColumnIndex("VALUE"));
        }
        cr.close();*/

        if (json.isEmpty()) {
            return null;
        }

        return _gson.fromJson(json, _pageType);

    }

    public void update(String id, TModel model) {
        String key = _key + id;
        String json = _gson.toJson(model);

        _database.execSQL("insert or replace into CACHE ( _id, KEY , VALUE) values" +
                "((select _id from CACHE where KEY = '" + key + "'), '" + key + "', '" + json + "')");

    }

    public void update(PageInput input, Page<TModel> page) {
        String key = _key + input.toString();        String json = _gson.toJson(page);

        _database.execSQL("insert or replace into CACHE ( _id, KEY , VALUE) values" +
                "((select _id from CACHE where KEY = '" + key + "'), '" + key + "', '" + json + "')");

    }

}
