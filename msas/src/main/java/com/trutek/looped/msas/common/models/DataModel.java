package com.trutek.looped.msas.common.models;

import com.trutek.looped.msas.common.contracts.IModel;

public class DataModel<TModel extends IModel> extends RemoteData {
    public TModel data;
}
