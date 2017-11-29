package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.data.impl.entities.Notification;
import com.trutek.looped.data.impl.entities.NotificationDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class NotificationRepository extends BaseRepository<Notification, NotificationModel> {

    private AbstractDao<Notification, Long> dao;

    public NotificationRepository(Context context, IModelMapper<Notification, NotificationModel> mapper, AbstractDao<Notification, Long> dao) {
        super(context,mapper, dao, NotificationRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Notification> query(Long id) {
        return dao.queryBuilder().where(NotificationDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Notification> query(PageQuery query) {
        return null;
    }

    @Override
    protected void map(NotificationModel model) {

    }

    @Override
    protected void map(Notification notification, NotificationModel model) {

    }

    @Override
    protected Notification newEntity() {
        return null;
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
