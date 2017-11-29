package com.trutek.looped.msas.common.contracts;

public interface AsyncResult<TData> {
    void success(TData data);
    void error(String error);



}

