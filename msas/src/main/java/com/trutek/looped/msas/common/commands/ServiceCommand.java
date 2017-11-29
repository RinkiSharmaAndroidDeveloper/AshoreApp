package com.trutek.looped.msas.common.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public abstract class ServiceCommand implements Command {

    private static final String TAG = ServiceCommand.class.getSimpleName();
    protected final Context context;
    protected final String successAction;
    protected final String failAction;

    public ServiceCommand(Context context, String successAction, String failAction) {
        this.context = context;
        this.successAction = successAction;
        this.failAction = failAction;
    }

    public void execute(Bundle bundle) throws Exception {
        Bundle result;
        try {
            result = perform(bundle);
            sendResult(result, successAction);
        } catch (Exception e) {
            Log.e("ServiceCommand", e.toString());
            result = new Bundle();
            sendResult(result, failAction);
            throw e;
        }
    }

    protected void sendResult(Bundle result, String action) {
        Intent intent = new Intent(action);
        if (null != result) {
            intent.putExtras(result);
        }
        Log.d(TAG,"SendResult - BroadCastReceiverAction: " +action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    protected abstract Bundle perform(Bundle extras) throws Exception;
}