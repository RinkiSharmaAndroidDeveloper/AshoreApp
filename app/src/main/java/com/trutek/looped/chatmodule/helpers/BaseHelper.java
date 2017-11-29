package com.trutek.looped.chatmodule.helpers;

import android.content.Context;

import com.trutek.looped.App;

public abstract class BaseHelper {

    protected Context context;

    public BaseHelper(Context context) {
        this.context = context;
    }
}