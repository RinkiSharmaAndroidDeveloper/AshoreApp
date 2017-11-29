package com.trutek.looped.chatmodule.data.impl.repository;

import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.data.impl.entities.ChatUser;
import com.trutek.looped.data.impl.entities.ChatUserDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by msas on 9/15/2016.
 */
public class ChatUserRepository extends BaseRepository<ChatUser, ChatUserModel> {

    private IModelMapper<ChatUser, ChatUserModel> mapper;
    private AbstractDao<ChatUser, Long> dao;

    public ChatUserRepository(IModelMapper<ChatUser, ChatUserModel> mapper, AbstractDao<ChatUser, Long> dao) {
        super(null,mapper, dao, ChatUserRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<ChatUser> query(Long id) {
        return dao.queryBuilder().where(ChatUserDao.Properties.UserId.eq(id));
    }

    @Override
    protected QueryBuilder<ChatUser> query(PageQuery query) {
        QueryBuilder<ChatUser> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(ChatUserDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("userId")){
            queryBuilder.where(ChatUserDao.Properties.UserId.eq(query.getInteger("userId")));
        }

        return queryBuilder;
    }

    @Override
    protected void map(ChatUserModel model) {

    }

    @Override
    protected void map(ChatUser chatUser, ChatUserModel model) {
        chatUser.setId(model.getId());
        chatUser.setUserId(model.getUserId());
        chatUser.setEmail(model.getEmail());
        chatUser.setName(model.getName());
        chatUser.setNumber(model.getNumber());
        chatUser.setLastRequestAt(model.getLastRequestAt());
        chatUser.setRole(model.getRole().name());

    }

    @Override
    protected ChatUser newEntity() {
        return new ChatUser();
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
