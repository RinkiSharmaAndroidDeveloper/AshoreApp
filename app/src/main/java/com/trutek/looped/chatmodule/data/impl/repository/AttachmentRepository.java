package com.trutek.looped.chatmodule.data.impl.repository;

import com.trutek.looped.chatmodule.data.contracts.models.AttachmentModel;
import com.trutek.looped.data.impl.entities.Attachment;
import com.trutek.looped.data.impl.entities.AttachmentDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by msas on 9/15/2016.
 */
public class AttachmentRepository extends BaseRepository<Attachment, AttachmentModel> {

    private IModelMapper<Attachment, AttachmentModel> mapper;
    private AbstractDao<Attachment, Long> dao;

    public AttachmentRepository(IModelMapper<Attachment, AttachmentModel> mapper, AbstractDao<Attachment, Long> dao) {
        super(null,mapper, dao, AttachmentRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Attachment> query(Long id) {
        return dao.queryBuilder().where(AttachmentDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Attachment> query(PageQuery query) {
        QueryBuilder<Attachment> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(AttachmentDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("attachmentId")){
            queryBuilder.where(AttachmentDao.Properties.AttachmentId.eq(query.getString("attachmentId")));
        }
        if(query.contains("serverId")){
            queryBuilder.where(AttachmentDao.Properties.AttachmentId.eq(query.getString("serverId")));
        }
        return queryBuilder;
    }

    @Override
    protected void map(AttachmentModel model) {

    }

    @Override
    protected void map(Attachment attachment, AttachmentModel model) {
        attachment.setId(model.getId());
        attachment.setAttachmentId(model.getAttachmentId());
        attachment.setName(model.getName());
        attachment.setType(model.getType().name());
        attachment.setURL(model.getURL());
        attachment.setSize(model.getSize());
    }

    @Override
    protected Attachment newEntity() {
        return new Attachment();
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
