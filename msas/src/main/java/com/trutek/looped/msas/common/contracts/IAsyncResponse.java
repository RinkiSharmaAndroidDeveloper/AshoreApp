package com.trutek.looped.msas.common.contracts;

public interface IAsyncResponse<TData> {

    boolean isBusy();

    boolean hasError();

    String getError();

    void waitForResult();

    TData getResult();
}


