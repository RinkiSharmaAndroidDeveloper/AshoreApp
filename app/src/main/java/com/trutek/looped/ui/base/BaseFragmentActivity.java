package com.trutek.looped.ui.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent();
    }

    protected abstract void setupActivityComponent();

}
