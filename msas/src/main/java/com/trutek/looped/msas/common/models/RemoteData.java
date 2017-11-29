package com.trutek.looped.msas.common.models;

public class RemoteData {
    public Boolean isSuccess = false;
    public String message;
    public String code;
    public String error;

    public String getError() {
        if (isSuccess)
            return "";

        return error == null || error.isEmpty() ? message : error;
    }
}
