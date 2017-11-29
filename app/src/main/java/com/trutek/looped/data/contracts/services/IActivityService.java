package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.impl.entities.Activity;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msas on 10/10/2016.
 */
public interface IActivityService extends ICRUDService<ActivityModel> {

    void createActivity(ActivityModel model, AsyncResult<ActivityModel> result, String intentAction);

    ActivityModel saveActivity(ActivityModel activityModel, String intentAction);

    void updateActivity(ActivityModel model, AsyncResult<ActivityModel> result);

    void getActivity(ActivityModel model, AsyncResult<ActivityModel> result);

    void joinActivity(ActivityModel model, AsyncResult<ActivityModel> result);

    void activitiesByCommunity(CommunityModel communityModel, AsyncResult<Page<ActivityModel>> result);

    void upcomingActivities(AsyncResult<Page<ActivityModel>> result);

    void pastActivities(String communityId,AsyncResult<Page<ActivityModel>> result);

    void recentActivities(AsyncResult<Page<ActivityModel>> result);

    //  void agendaActivities(AsyncResult<Page<ActivityModel>> result);

    // void plannerActivities(AsyncResult<Page<ActivityModel>> result);
    void fetchMyActivities();

    void fetchMyPlannerActivity(PageInput pageInput);

    List<ActivityModel> getPlannerActivity();

    List<ActivityModel> getAgenda();

}
