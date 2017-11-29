package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.impl.entities.Activity;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by msas on 10/10/2016.
 */
public class ActivityMapper implements IModelMapper<Activity, ActivityModel> {


    @Override
    public ActivityModel Map(Activity activity) {
        ActivityModel activityModel=new ActivityModel();
        activityModel.setId(activity.getId());
        activityModel.setServerId(activity.getServerId());
        activityModel.setSubject(activity.getSubject());
        activityModel.setBody(activity.getBody());
        activityModel.setType(activity.getType());
        activityModel.setPicUrl(activity.getPicUrl());
        activityModel.setPicData(activity.getPicData());
        activityModel.setDueDate(activity.getDueDate());
        activityModel.setUpdated_At(activity.getUpdated_At());
        activityModel.setPrivate(activity.getIsPrivate());
        return activityModel;

    }
}
