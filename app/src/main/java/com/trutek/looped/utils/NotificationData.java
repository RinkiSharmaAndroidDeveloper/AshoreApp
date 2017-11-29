package com.trutek.looped.utils;

import android.content.Context;

import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.impl.services.CommunityService;

/**
 * Created by Crown on 10/19/2016.
 */
public class NotificationData {

    private Context context;
    private ICommunityService communityService;

    public NotificationData(Context context, ICommunityService communityService){
        this.context = context;
        this.communityService = communityService;
    }
}
