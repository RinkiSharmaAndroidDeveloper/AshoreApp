package com.trutek.looped.msas.common.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.EditText;
import android.widget.Toast;

public class UIHelper {

    public static ProgressDialog progressDialog;

    public static ProgressDialog showProgress(Activity activity, int messageId) {
        return showProgress(activity, activity.getResources().getString(messageId));
    }

    public static ProgressDialog showProgress(Activity activity, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        return progressDialog;
    }


    public static void showToast(Activity activity, int messageId) {
        showToast(activity, activity.getResources().getString(messageId));
    }

    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public static String getString(EditText control) {
        return control.getText().toString();
    }
}
