package com.trutek.looped.chatmodule.data.impl.repository;


import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.data.impl.entities.Dialog;
import com.trutek.looped.data.impl.entities.DialogDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Date;
import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by msas on 9/15/2016.
 */
public class DialogRepository extends BaseRepository<Dialog, DialogModel> {

    private IModelMapper<Dialog, DialogModel> mapper;
    private AbstractDao<Dialog, Long> dao;

    public DialogRepository(IModelMapper<Dialog, DialogModel> mapper, AbstractDao<Dialog, Long> dao) {
        super(null,mapper, dao, DialogRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Dialog> query(Long id) {
        return dao.queryBuilder().where(DialogDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Dialog> query(PageQuery query) {
        QueryBuilder<Dialog> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(DialogDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("dialogId")){
            queryBuilder.where(DialogDao.Properties.DialogId.eq(query.getString("dialogId")));
        }
        if(query.contains("serverId")){
            queryBuilder.where(DialogDao.Properties.DialogId.eq(query.getString("serverId")));
        }
        return queryBuilder;
    }

    @Override
    protected void map(DialogModel model) {

    }

    @Override
    protected void map(Dialog dialog, DialogModel model) {
        dialog.setId(model.getId());
        dialog.setDialogId(model.getDialogId());
        dialog.setLastMessage(model.getLastMessage());
        dialog.setLastMessageDateSent(model.getLastMessageDateSent());
        dialog.setImageUrl(model.getImageUrl());
        dialog.setName(model.getName());
        dialog.setUnreadMessagesCount(model.getUnreadMessagesCount());
        dialog.setType(model.getType().name());
        dialog.setStatus(model.getSyncStatus());
        dialog.setLastMessageUserId(model.getLastMessageUserId());
        dialog.setUserId(model.getUserId());
        dialog.setXmppRoomJid(model.getXmppRoomJid());

        dialog.setTimeStamp(new Date());
    }

    @Override
    protected Dialog newEntity() {
        return new Dialog();
    }

    @Override
    public void addObservers(Observer observer) {
        this.addObserver(observer);
    }

    @Override
    public void deleteObservers(Observer observer) {
        this.deleteObserver(observer);
    }
}
