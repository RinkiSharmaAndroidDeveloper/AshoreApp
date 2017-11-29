package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.data.impl.entities.ConnectionDao;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.data.impl.entities.ProfileDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class ConnectionRepository extends BaseRepository<Connection, ConnectionModel> {
    private IModelMapper<Connection, ConnectionModel> mapper;
    private AbstractDao<Connection, Long> dao;

    public ConnectionRepository(Context context, IModelMapper<Connection, ConnectionModel> mapper, AbstractDao<Connection, Long> dao) {
        super(context,mapper, dao, ConnectionRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }


    @Override
    protected QueryBuilder<Connection> query(Long id) {
        return dao.queryBuilder().where(ConnectionDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Connection> query(PageQuery query) {
        QueryBuilder<Connection> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(ConnectionDao.Properties.Id.eq(query.getDate("id")));
        }

        if(query.contains("serverId")){
            queryBuilder.where(ConnectionDao.Properties.ServerId.eq(query.getString("serverId")));
        }

        if(query.contains(ConnectionModel.KEY_MINE)){
            queryBuilder.where(ConnectionDao.Properties.IsMine.eq(query.getBoolean(ConnectionModel.KEY_MINE)));
        }
        return queryBuilder;
    }

    @Override
    protected void map(ConnectionModel model) {


    }

    @Override
    protected void map(Connection connection, ConnectionModel model) {
        connection.setServerId(model.getServerId());
        connection.setId(model.getId());
        connection.setName(model.getProfile().getName());
        connection.setJabberId(model.getProfile().getChat().id);
        connection.setPicUrl(model.getProfile().getPicUrl());
        connection.setIsMine(model.isMine);
    }

    @Override
    protected Connection newEntity() {
        return new Connection();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
