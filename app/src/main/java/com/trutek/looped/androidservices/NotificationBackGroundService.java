package com.trutek.looped.androidservices;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.trutek.looped.App;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.ModelState;
import com.trutek.looped.msas.common.models.PageInput;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * Created by Crown on 10/19/2016.
 */

public class NotificationBackGroundService extends Service {
    ProfileModel abc;
    Date date;
    /* @Inject
     ICommunityService iCommunityService;*/

    boolean isServiceRunning;
    @Inject
    IActivityService _activityService;

    public static final long NOTIFY_INTERVAL = 60 * 1000;
    private Timer mTimer = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).component().inject(this);
        // autoSync();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null!=intent) {
            intent.putExtra("FromService", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void addCommunities(ActivityModel activityModel) {
        // iCommunityService.create(communityModel, Constants.BROADCAST_MY_COMMUNITIES);
        _activityService.create(activityModel, Constants.BROADCAST_MY_ACTIVITIES);
    }

    public void autoSync() {
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        TimerTask syncTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("Auto Sync", "run");
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = fmt.parse("2016-11-17");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ActivityModel activityModel = new ActivityModel();
                activityModel.subject = "MyTestAgenda";
                activityModel.body = "Dummy_Description";
                activityModel.dueDate = date;
                addCommunities(activityModel);
            }
        };
        mTimer.scheduleAtFixedRate(syncTask, 0, NOTIFY_INTERVAL);
    }


}
