package com.trutek.looped.ui.base;

import android.os.Bundle;

public abstract class BaseV4DialogFragment extends android.support.v4.app.DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent();
    }

    protected abstract void setupActivityComponent();
}
