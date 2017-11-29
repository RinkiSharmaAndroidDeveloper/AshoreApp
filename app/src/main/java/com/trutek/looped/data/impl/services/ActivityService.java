package com.trutek.looped.data.impl.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.trutek.looped.data.contracts.apis.IActivityApi;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;
import com.trutek.looped.utils.helpers.alarm.AlarmHelper;

import java.util.Date;
import java.util.List;

import java.util.Collections;
import java.util.Comparator;

public class ActivityService extends BaseService<ActivityModel> implements IActivityService {

    final static String TAG = ActivityService.class.getSimpleName();

    private IActivityApi<ActivityModel> remote;
//    private IRepository<CommunityModel> localCommunity;

    public ActivityService(IRepository<ActivityModel> local, IActivityApi<ActivityModel> remote) {
        super(local);
        this.remote = remote;
//        this.localCommunity = localCommunity;
    }

    @Override
    public void createActivity(final ActivityModel model, final AsyncResult<ActivityModel> result, final String intentAction) {
        remote.create(model, new AsyncResult<ActivityModel>() {
            @Override
            public void success(ActivityModel activityModel) {
                activityModel.setCommunity(model.getCommunity());
                saveActivity(activityModel, intentAction);
                result.success(activityModel);
                PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
                String profileServerId=helper.getPreference(PreferenceHelper.USER_PROFILE_ID);
               if(activityModel.admin.getServerId().equals(profileServerId)){
                    activityModel.setMine(true);
                  _local.create(activityModel);
                }
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public ActivityModel saveActivity(ActivityModel activityModel, String intentAction) {
        return create(activityModel, intentAction);
    }

    @Override
    public void updateActivity(ActivityModel model, AsyncResult<ActivityModel> result) {
        remote.update(model, result);

    }

    @Override
    public void getActivity(ActivityModel model, AsyncResult<ActivityModel> result) {
        remote.get(model.getServerId(), result);
    }

    @Override
    public void joinActivity(ActivityModel model, AsyncResult<ActivityModel> result) {
        String action = model.getServerId() + "/participants";
        remote.joinActivity(action, model, result);
    }

    @Override
    public void activitiesByCommunity(CommunityModel community, AsyncResult<Page<ActivityModel>> result) {
        PageInput input = new PageInput();
        input.query.add("communityId", community.getServerId());
        remote.page(input, result);
    }

    @Override
    public void upcomingActivities(final AsyncResult<Page<ActivityModel>> result) {
        PageInput input = new PageInput();
        input.query.add("mineOnly", true);
        remote.page(input, new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(Page<ActivityModel> activityModelPage) {

                Collections.sort(activityModelPage.items, new Comparator<ActivityModel>() {
                    @Override
                    public int compare(ActivityModel lhs, ActivityModel rhs) {
                        return lhs.timeStamp.compareTo(rhs.timeStamp);
                    }
                });

                Collections.reverse(activityModelPage.items);

                result.success(activityModelPage);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void pastActivities(String communityId, AsyncResult<Page<ActivityModel>> result) {
        remote.page(null,"past/"+ communityId,result);
    }

    @Override
    public void recentActivities(final AsyncResult<Page<ActivityModel>> result) {
        PageInput input = new PageInput();
        input.query.add("recent", true);
        remote.page(input, new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(Page<ActivityModel> activityModelPage) {

                /*Collections.sort(activityModelPage.items, new Comparator<ActivityModel>() {
                    @Override
                    public int compare(ActivityModel lhs, ActivityModel rhs) {
                        return lhs.timeStamp.compareTo(rhs.timeStamp);
                    }
                });

                Collections.reverse(activityModelPage.items);*/

                result.success(activityModelPage);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public List<ActivityModel> getAgenda() {
        PageInput input = new PageInput();
        Date toDate = DateHelper.getToday(),fromDate=DateHelper.getDayAfter(1,toDate);
        input.query.add(Constants.TO_DATE,toDate);
        input.query.add(Constants.FROM_DATE,fromDate);
        input.query.add(Constants.ORDER_ASC_NO_LIMIT,"");
        Log.d(TAG,String.format("ToDate: %s FromDate: %s",toDate,fromDate));
        return search(input).items;

    }

  /*  @Override
    public void agendaActivities(AsyncResult<Page<ActivityModel>> result) {
        PageInput input = new PageInput();
        input.query.add("mineOnly", true);
        remote.page(input, result);
    }*/

    /*@Override
    public void plannerActivities(AsyncResult<Page<ActivityModel>> result) {
        PageInput input = new PageInput();
        input.query.add("mineOnly", true);
        remote.page(input, result);
    }*/
    @Override
    public void fetchMyActivities() {
        PageInput input = new PageInput();
        input.query.add("mineOnly", true);
        remote.page(input, new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(Page<ActivityModel> activityModelPage) {
                /*for (ActivityModel model : activityModelPage.items) {

                    CommunityModel communityModel = localCommunity.getByServerId(model.community.getServerId());

                    if (null == communityModel) {
                        communityModel = localCommunity.create(model.community);
                    }
                    ActivityModel createModel = _local.getByServerId(model.getServerId());
                    if (createModel == null) {
                        model.setCommunity(communityModel);
                        _local.create(model, Constants.BROADCAST_MY_ACTIVITIES);
                    } else {
                        model.setId(createModel.getId());
                        model.setCommunity(communityModel);
                        update(model, Constants.BROADCAST_MY_ACTIVITIES);
                    }
                }*/
            }

            @Override
            public void error(String error) {
                Log.d("hda", error);
            }
        });
    }

    @Override
    public void fetchMyPlannerActivity(PageInput pageInput) {
        PageInput input = new PageInput();
        input.query.add("mineOnly", true);
        remote.page(input, new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(Page<ActivityModel> activityModelPage) {
                for (ActivityModel model : activityModelPage.items) {
                    ActivityModel createModel = _local.getByServerId(model.getServerId());
                    if (createModel == null) {
                        _local.create(model, Constants.BROADCAST_MY_PLANNER);
                    } else {
                        _local.update(createModel.getId(), model, Constants.BROADCAST_MY_PLANNER);
                    }

                }
            }

            @Override
            public void error(String error) {
                Log.d("hda", error);
            }
        });

    }

    @Override
    public List<ActivityModel> getPlannerActivity() {
        PageInput input = new PageInput();
//        input.query.add("isMine", true);
        return search(input).items;
    }
}

