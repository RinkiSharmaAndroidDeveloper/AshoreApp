package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.data.impl.entities.CommunityDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by msas on 9/28/2016.
 */
public class CommunityRepository extends BaseRepository<Community, CommunityModel> {

    private AbstractDao<Community, Long> dao;

    public CommunityRepository(Context context, IModelMapper<Community, CommunityModel> mapper, AbstractDao<Community, Long> dao) {
        super(context,mapper, dao, CommunityRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Community> query(Long id) {
        return dao.queryBuilder().where(CommunityDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Community> query(PageQuery query) {
        QueryBuilder<Community> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(CommunityDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("serverId")){
            queryBuilder.where(CommunityDao.Properties.ServerId.eq(query.getString("serverId")));
        }
        if(query.contains("isMine")){
            queryBuilder.where(CommunityDao.Properties.IsMine.eq(query.getBoolean("isMine")));
        }
        return queryBuilder;
    }

    @Override
    protected void map(CommunityModel model) {

    }

    @Override
    protected void map(Community community, CommunityModel model) {
        community.setId(model.getId());
        community.setSubject(model.getSubject());
        community.setBody(model.getBody());
        community.setMembersCount(model.getMembersCount());
        community.setFriendsCount(model.getFriendsCount());
        community.setPicData(model.getPicData());
        community.setPicUrl(model.getPicUrl());
        community.setIsPrivate(model.getIsPrivate());
        community.setServerId(model.getServerId());
        community.setTimeStamp(model.getTimeStamp());
        community.setIsMine(model.getMine());
    }

    @Override
    protected Community newEntity() {
        return new Community();
    }

    @Override
    public void addObservers(Observer observer) {
        addObserver(observer);
    }

    @Override
    public void deleteObservers(Observer observer) {
        deleteObserver(observer);
    }
}
