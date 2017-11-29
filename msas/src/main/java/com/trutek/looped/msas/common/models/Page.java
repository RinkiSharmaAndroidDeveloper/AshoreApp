package com.trutek.looped.msas.common.models;


import java.util.ArrayList;
import java.util.List;

public class Page<TModel> extends RemoteData {
    public Long PageNo;
    public Long pageSize;
    public Long Total = 0L;
    public List<TModel> items;

    private Boolean _isCached = false;
    private Boolean _isFinal = true;

    public Boolean isFinal() {
        return _isFinal;
    }

    public void setFinal(){
        _isFinal = true;
    }

    public Boolean isCached() {
        return _isCached;
    }

    public void setCached() {
        _isCached = true;
        _isFinal = false;
    }

    public Page() {
        items = new ArrayList<>();
    }
}


