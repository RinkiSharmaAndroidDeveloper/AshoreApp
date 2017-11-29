package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.impl.entities.Activity;
import com.trutek.looped.data.impl.entities.ActivityDao;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.data.impl.entities.ConnectionDao;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;


public class ActivityRepository extends BaseRepository<Activity, ActivityModel> {
    private AbstractDao<Activity, Long> dao;
    private IModelMapper<Activity, ActivityModel> mapper;
    public ActivityRepository(Context context, IModelMapper<Activity, ActivityModel> mapper, AbstractDao<Activity, Long> dao) {
        super(context,mapper, dao, ActivityRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;

    }

    @Override
    protected QueryBuilder<Activity> query(Long id) {
        return dao.queryBuilder().where(ActivityDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Activity> query(PageQuery query) {
        QueryBuilder<Activity> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(ActivityDao.Properties.Id.eq(query.getDate("id")));
        }

        if(query.contains("serverId")){
            queryBuilder.where(ActivityDao.Properties.ServerId.eq(query.getString("serverId")));
        }

        if(query.contains("dueDate")){
            queryBuilder.where(ActivityDao.Properties.DueDate.le(query.getDate("dueDate")));
        }

        if(query.contains(Constants.KEY_COMMUNITY_ID)){
            queryBuilder.where(ActivityDao.Properties.CommunityId.eq(query.getLong(Constants.KEY_COMMUNITY_ID)));
        }

        if(query.contains(Constants.TO_DATE)){
            queryBuilder.where(ActivityDao.Properties.DueDate.ge(query.getDate(Constants.TO_DATE)));
        }

        if(query.contains(Constants.GREATER_THEN)){
            queryBuilder.where(ActivityDao.Properties.DueDate.gt(query.getDate(Constants.GREATER_THEN)));
        }

        if(query.contains(Constants.FROM_DATE)){
            queryBuilder.where(ActivityDao.Properties.DueDate.le(query.getDate(Constants.FROM_DATE)));
        }

        if(query.contains(Constants.ORDER_DESC)){
            queryBuilder.orderDesc(ActivityDao.Properties.DueDate).limit(1);
        }

        if(query.contains(Constants.ORDER_ASC)){
            queryBuilder.orderAsc(ActivityDao.Properties.DueDate).limit(1);
        }
        if(query.contains(Constants.ORDER_DESC_NO_LIMIT)){
            queryBuilder.orderDesc(ActivityDao.Properties.DueDate);
        }

        if(query.contains(Constants.ORDER_ASC_NO_LIMIT)){
            queryBuilder.orderAsc(ActivityDao.Properties.DueDate);
        }

        return queryBuilder;
    }

    @Override
    protected void map(ActivityModel model) {

    }

    @Override
    protected void map(Activity activity, ActivityModel model) {
        activity.setServerId(model.getServerId());
        activity.setId(model.getId());
        activity.setSubject(model.getSubject());
        activity.setBody(model.getBody());
        activity.setDueDate(model.dueDate);
        activity.setUpdated_At(model.getUpdated_At());
        activity.setType(model.getType());
        activity.setPicUrl(model.getPicUrl());
        activity.setPicData(model.getPicData());
        activity.setDueDate(model.getDueDate());
        activity.setIsPrivate(model.isPrivate());
        activity.setIsMine(model.isMine());
        activity.setCommunityId(model.getCommunity().getId());
    }

    @Override
    protected Activity newEntity() {
        return new Activity();
    }

    @Override
    public void addObservers(Observer observer) {
        this.addObserver(observer);
    }

    @Override
    public void deleteObservers(Observer observer) {
        this.addObserver(observer);
    }
}
