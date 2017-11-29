package com.trutek.looped.msas.common.helpers;

import android.app.Activity;
import android.content.Intent;

import java.io.Serializable;

public class IntentExtender {
    Intent _intent;

    public IntentExtender(Class<?> cls, Activity activity) {
        _intent = new Intent(activity, cls);
    }

    public IntentExtender(Intent intent) {
        _intent = intent;
    }

    public IntentExtender(Activity activity) {
        _intent = activity.getIntent();
    }

    public IntentExtender(ActivityExtender xActivity) {
        _intent = xActivity.getActivity().getIntent();
    }

    public IntentExtender addModel(Serializable model) {
        _intent.putExtra("model", model);
        return this;
    }

    public IntentExtender openFor(int fragmentCode) {
        _intent.putExtra("OPEN_FOR", fragmentCode);
        return this;

    }

    public int getOpenFor() {
        return _intent.getIntExtra("OPEN_FOR", 0);
    }

    public Object getModel() {
        return _intent.getSerializableExtra("model");
    }

    public Intent getIntent() {
        return _intent;
    }

}
