package com.trutek.looped.chatmodule.data.impl.repository;

import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.data.impl.entities.Message;
import com.trutek.looped.data.impl.entities.MessageDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by msas on 9/15/2016.
 */
public class MessageRepository extends BaseRepository<Message, MessageModel> {

    private IModelMapper<Message, MessageModel> mapper;
    private AbstractDao<Message, Long> dao;

    public MessageRepository(IModelMapper<Message, MessageModel> mapper, AbstractDao<Message, Long> dao) {
        super(null,mapper, dao, MessageRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Message> query(Long id) {
        return dao.queryBuilder().where(MessageDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Message> query(PageQuery query) {
        QueryBuilder<Message> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(MessageDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("dialogId")){
            queryBuilder.where(MessageDao.Properties.DialogId.eq(query.getString("dialogId")));
        }
        if(query.contains("messageId")){
            queryBuilder.where(MessageDao.Properties.MessageId.eq(query.getString("messageId")));
        }
        if(query.contains("lastMessage")){
            queryBuilder.orderDesc(MessageDao.Properties.DateSent).limit(1);
        }
        return queryBuilder;
    }

    @Override
    protected void map(MessageModel model) {

    }

    @Override
    protected void map(Message message, MessageModel model) {
        message.setId(model.getId());
        message.setMessageId(model.getMessageId());
        message.setBody(model.getBody());
        message.setDateSent(model.getDateSent());
        message.setRecipientId(model.getRecipientId());
        message.setSenderId(model.getSenderId());
//        message.setState(model.getState().name());
//        message.setStatus(model.getStatus().name());
        message.setTimeStamp(model.getTimeStamp());

        if(model.getDialog() != null){
            message.setDialogId(model.getDialog().getDialogId());
        } else {
            message.setDialogId(model.getDialogId());
        }

        if (model.getAttachment() != null){
            message.setAttachmentId(model.getAttachment().getAttachmentId());
        } else {
            message.setAttachmentId(model.getAttachmentId());
        }
    }

    @Override
    protected Message newEntity() {
        return new Message();
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
