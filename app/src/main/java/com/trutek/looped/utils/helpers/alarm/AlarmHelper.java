package com.trutek.looped.utils.helpers.alarm;

import android.content.Context;


import com.trutek.looped.alarms.broadcastReceivers.NotificationEventReceiver;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.Calendar;
import java.util.Date;

public class AlarmHelper {

    private Context context;
    private DataManager dataManager;

    public AlarmHelper(Context context){

        this.context = context;
        this.dataManager = DataManager.getInstance();
    }

    public void setAlarm(){
        PageInput input = new PageInput();
        input.query.add(Constants.GREATER_THEN, new Date());
        input.query.add(Constants.ORDER_ASC, true);

        ActivityModel activity = null;

        if(null != dataManager.getActivityRepository()) {
            activity = dataManager.getActivityRepository().get(input.query);
        }

        if(activity != null){

            NotificationEventReceiver.setupAlarm(context, activity.dueDate);
        }
    }


}
