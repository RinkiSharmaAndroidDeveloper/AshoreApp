package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.impl.entities.Recipient;
import com.trutek.looped.data.impl.entities.RecipientDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class RecipientRepository extends BaseRepository<Recipient, RecipientModel> {

    private IModelMapper<Recipient, RecipientModel> mapper;
    private AbstractDao<Recipient, Long> dao;

    public RecipientRepository(Context context, IModelMapper<Recipient, RecipientModel> mapper, AbstractDao<Recipient, Long> dao) {
        super(context,mapper, dao, RecipientRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Recipient> query(Long id) {
        return dao.queryBuilder().where(RecipientDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Recipient> query(PageQuery query) {
        QueryBuilder<Recipient> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(RecipientDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("serverId")){
            queryBuilder.where(RecipientDao.Properties.ServerId.eq(query.getString("serverId")));
        }
        if(query.contains("lastRecipient")){
            queryBuilder.limit(1);
        }
        return queryBuilder;
    }

    @Override
    protected void map(RecipientModel model) {

    }

    @Override
    protected void map(Recipient recipient, RecipientModel model) {
        recipient.setId(model.getId());
        recipient.setServerId(model.getServerId());
        recipient.setName(model.getName());
        recipient.setAge(model.getAge());
        recipient.setGender(model.getGender());
        recipient.setPicUrl(model.getPicUrl());
    }

    @Override
    protected Recipient newEntity() {
        return new Recipient();
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
