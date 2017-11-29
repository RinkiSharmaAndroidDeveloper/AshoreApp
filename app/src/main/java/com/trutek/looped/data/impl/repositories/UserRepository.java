package com.trutek.looped.data.impl.repositories;

import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.impl.entities.User;
import com.trutek.looped.data.impl.entities.UserDao;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class UserRepository extends BaseRepository<User, UserModel> {
    private IModelMapper<User, UserModel> mapper;
    private AbstractDao<User, Long> dao;

    public UserRepository(IModelMapper<User, UserModel> mapper, AbstractDao<User, Long> dao) {
        super(null,mapper, dao, UserRepository.class.getSimpleName());
        this.mapper=mapper;
        this.dao=dao;
    }

    @Override
    protected QueryBuilder<User> query(Long id) {
        return dao.queryBuilder().where(UserDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<User> query(PageQuery query) {
        QueryBuilder<User> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(UserDao.Properties.Id.ge(query.getDate("id")));
        }
        return queryBuilder;
    }

    @Override
    protected void map(UserModel model) {

    }

    @Override
    protected void map(User contact, UserModel model) {
        contact.setName(model.phone);
    }

    @Override
    protected User newEntity() {
        return new User();
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
