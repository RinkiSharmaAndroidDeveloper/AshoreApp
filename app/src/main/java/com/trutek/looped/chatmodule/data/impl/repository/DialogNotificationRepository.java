package com.trutek.looped.chatmodule.data.impl.repository;

import android.content.Context;

import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.data.impl.entities.DialogNotification;
import com.trutek.looped.data.impl.entities.DialogNotificationDao;
import com.trutek.looped.data.impl.entities.MessageDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by msas on 11/8/2016.
 */
public class DialogNotificationRepository extends BaseRepository<DialogNotification, DialogNotificationModel> {

    private IModelMapper<DialogNotification, DialogNotificationModel> mapper;
    private AbstractDao<DialogNotification, Long> dao;

    public DialogNotificationRepository(Context context,IModelMapper<DialogNotification, DialogNotificationModel> mapper, AbstractDao<DialogNotification, Long> dao) {
        super(null,mapper, dao, DialogNotificationRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<DialogNotification> query(Long id) {
        return dao.queryBuilder().where(DialogNotificationDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<DialogNotification> query(PageQuery query) {
        QueryBuilder<DialogNotification> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(DialogNotificationDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("notificationId")){
            queryBuilder.where(DialogNotificationDao.Properties.NotificationId.eq(query.getString("notificationId")));
        }
        if(query.contains("dialogId")){
            queryBuilder.where(DialogNotificationDao.Properties.DialogId.eq(query.getString("dialogId")));
        }
        if(query.contains("lastNotification")){
            queryBuilder.orderDesc(DialogNotificationDao.Properties.CreatedDate).limit(1);
        }
        return queryBuilder;
    }

    @Override
    protected void map(DialogNotificationModel model) {

    }

    @Override
    protected void map(DialogNotification dialogNotification, DialogNotificationModel model) {
        dialogNotification.setId(model.getId());
        dialogNotification.setNotificationId(model.getNotificationId());
        dialogNotification.setBody(model.getBody());
        dialogNotification.setType(model.getType().name());
        dialogNotification.setState(model.getState().name());
        dialogNotification.setCreatedDate(model.getCreatedDate());
        dialogNotification.setDialogId(model.getDialogId());
    }

    @Override
    protected DialogNotification newEntity() {
        return new DialogNotification();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
