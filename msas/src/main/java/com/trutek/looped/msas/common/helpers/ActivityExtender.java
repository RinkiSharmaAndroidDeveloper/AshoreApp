package com.trutek.looped.msas.common.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.io.Serializable;

public class ActivityExtender {
    Activity _activity;
    ProgressDialog _progressDialog;

    public ActivityExtender(Activity activity) {
        _activity = activity;
        _progressDialog = new ProgressDialog(_activity);
    }

    public Activity getActivity() {
        return _activity;
    }

    public ActivityExtender block() {
        return block("Please wait");
    }

    public ActivityExtender block(int messageId) {
        block(_activity.getResources().getString(messageId));
        return this;
    }

    public ActivityExtender block(String message) {
        _progressDialog.setMessage(message);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setCanceledOnTouchOutside(false);
        _progressDialog.show();
        return this;
    }

    public void onUI(Runnable action) {
        _activity.runOnUiThread(action);
    }

    public ActivityExtender unBlock() {
        if (_progressDialog.isShowing())
            _progressDialog.dismiss();
        return this;
    }

    public ActivityExtender toast(int messageId) {
        toast(_activity.getResources().getString(messageId));
        return this;
    }

    public ActivityExtender toast(String message) {
        Toast.makeText(_activity, message, Toast.LENGTH_SHORT).show();
        return this;
    }

    public ActivityExtender showError(String error) {
        Toast.makeText(_activity, error, Toast.LENGTH_SHORT).show();
        return this;
    }

    public ActivityExtender alert(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
        dialog.setMessage(message).create().show();
        return this;

    }

    public ActivityExtender show(int container, android.app.Fragment fragment) {
        FragmentTransaction fragmentTransaction = _activity.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();
        return this;
    }

    public ActivityExtender navigate(Class<?> cls) {
        Intent intent = new Intent(_activity, cls);
        _activity.startActivity(intent);
        _activity.finish();
        return this;

    }


    public ActivityExtender fullNavigate(Class<?> cls) {
        Intent intent = new Intent(_activity, cls);
        _activity.startActivity(intent);
        _activity.finish();
        return this;

    }

    public IntentExtender newIntent(Class<?> cls) {
        return new IntentExtender(cls, _activity);
    }

    IntentExtender _xIntent = null;

    public IntentExtender getIntent() {
        if (_xIntent == null) {
            _xIntent = new IntentExtender(this);
        }
        return _xIntent;
    }

    public ActivityExtender navigateWithModel(Class<?> cls, Serializable model) {
        Intent intent = new Intent(_activity, cls);
        intent.putExtra("model", model);
        _activity.startActivity(intent);
        return this;

    }

    public Object getModel() {
        return _activity.getIntent().getSerializableExtra("model");
    }

    public void navigate(Class<?> cls, int fragmentCode, int loadFromCode) {
        Intent intent = new Intent(_activity, cls);
        intent.putExtra("OPEN_FOR", fragmentCode);
        intent.putExtra("Load_from", loadFromCode);
        _activity.startActivity(intent);
        _activity.finish();

    }

    public void navigate(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(_activity, cls);
        intent.putExtra("bundle", bundle);
        _activity.startActivity(intent);
        _activity.finish();

    }

}
