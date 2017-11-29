package com.trutek.looped.ui.base;

import android.app.Service;


public abstract class BaseService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        setupServiceComponent();
    }

    protected abstract void setupServiceComponent();
}
