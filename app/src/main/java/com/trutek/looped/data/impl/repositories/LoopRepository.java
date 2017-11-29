package com.trutek.looped.data.impl.repositories;


import android.content.Context;

import com.trutek.looped.data.contracts.models.LoopModel;
import com.trutek.looped.data.impl.entities.Loop;
import com.trutek.looped.data.impl.entities.LoopDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class LoopRepository extends BaseRepository<Loop, LoopModel> {

    private AbstractDao<Loop, Long> dao;

    public LoopRepository(Context context, IModelMapper<Loop, LoopModel> mapper, AbstractDao<Loop, Long> dao) {
        super(context, mapper, dao, LoopRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Loop> query(Long id) {
        return dao.queryBuilder().where(LoopDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Loop> query(PageQuery query) {
        QueryBuilder<Loop> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(LoopDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("serverId")){
            queryBuilder.where(LoopDao.Properties.ServerId.eq(query.getString("serverId")));
        }
        if(query.contains("recipientId")){
            queryBuilder.where(LoopDao.Properties.RecipientId.eq(query.getString("recipientId")));
        }
        if(query.contains("status")){
            queryBuilder.where(LoopDao.Properties.Status.eq(query.getString("status")));
        }
        return queryBuilder;
    }

    @Override
    protected void map(LoopModel model) {

    }

    @Override
    protected void map(Loop loop, LoopModel model) {
        loop.setId(model.getId());

        if(model.getProfile() != null){
            loop.setName(model.getProfile().getName());
        } else {
            loop.setName(model.getName());
        }

        if(model.getProfile() != null){
            loop.setPicUrl(model.getProfile().getPicUrl());
        } else {
            loop.setPicUrl(model.getPicUrl());
        }

        if(model.getProfile() != null){
            loop.setProfileId(model.getProfile().getServerId());
        } else {
            loop.setProfileId(model.getProfileId());
        }

        loop.setServerId(model.getServerId());
        loop.setRole(model.getRole());
        loop.setStatus(model.getLoopStatus());
        loop.setRecipientId(model.getRecipientId());
    }

    @Override
    protected Loop newEntity() {
        return new Loop();
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
