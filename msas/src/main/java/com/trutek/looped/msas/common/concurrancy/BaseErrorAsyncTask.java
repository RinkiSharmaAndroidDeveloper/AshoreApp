package com.trutek.looped.msas.common.concurrancy;

import android.app.Activity;
import android.app.DialogFragment;

import java.lang.ref.WeakReference;

public abstract class BaseErrorAsyncTask<Params, Progress, Result> extends BaseAsyncTask<Params, Progress, Result> {

    private static final String TAG = BaseErrorAsyncTask.class.getName();

    protected WeakReference<Activity> activityRef;

    protected BaseErrorAsyncTask(Activity activity) {
        this.activityRef = new WeakReference<Activity>(activity);
    }

    @Override
    public void onException(Exception e) {

        Activity parentActivity = activityRef.get();

    }

    protected void showDialog(DialogFragment dialog) {
        showDialog(dialog, null);
    }

    protected void showDialog(DialogFragment dialog, String tag) {
        if (activityRef.get() != null) {
            dialog.show(activityRef.get().getFragmentManager(), tag);
        }
    }

    protected void hideDialog(DialogFragment dialog) {
        if (dialog.getActivity() != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

    protected boolean isActivityAlive() {
        Activity activity = activityRef.get();
        return activity != null && !activity.isFinishing();
    }
}
