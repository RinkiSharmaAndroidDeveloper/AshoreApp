package com.trutek.looped.msas.common.models;

import android.net.Uri;


import com.trutek.looped.msas.common.contracts.PageQuery;

import java.util.Date;
import java.util.Hashtable;

public class PageInput {
    public Integer pageNo = 1;
    public Integer pageSize;
    public Boolean noPaging = true;
    public PageQuery query;

    public PageInput() {
        query = new PageQuery();
    }

    public PageInput(PageQuery query) {
        this.query = query;
    }

    public PageInput(String key, String value) {
        this.query = new PageQuery(key, value);
    }

    public PageInput(String key, Long value) {
        this.query = new PageQuery(key, value);
    }

    public PageInput(String key, Date value) {
        this.query = new PageQuery(key, value);
    }

    public PageInput reset() {
        query.reset();
        //   pageNo = 1;
        return this;
    }

    @Override
    public String toString() {
        Hashtable<String, String> params = query.getParams();
        if (params == null) {
            return "";
        }

        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.appendQueryParameter("page", pageNo.toString());

        for (String key : params.keySet()) {
            String value = params.get(key);
            if (!value.equals(""))
                uriBuilder.appendQueryParameter(key, value);
        }

        return uriBuilder.build().getEncodedQuery();
    }

    public Hashtable<String, String> getParams() {
        Hashtable<String, String> params = query.getParams();
        params.put("page", pageNo.toString());
        return params;
    }
}
