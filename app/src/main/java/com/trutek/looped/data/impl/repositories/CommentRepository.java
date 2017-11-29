package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.impl.entities.Comment;
import com.trutek.looped.data.impl.entities.CommentDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Rinki on 2/19/2017.
 */
public class CommentRepository extends BaseRepository<Comment, CommentModel> {

    private IModelMapper<Comment, CommentModel> mapper;
    private AbstractDao<Comment, Long> dao;
    public CommentRepository(Context context, IModelMapper<Comment, CommentModel> mapper, AbstractDao<Comment, Long> dao) {
        super(context, mapper, dao,  CommentRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Comment> query(Long id) {
        return dao.queryBuilder().where(CommentDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Comment> query(PageQuery query) {
        QueryBuilder<Comment> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(CommentDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("serverId")){
            queryBuilder.where(CommentDao.Properties.ServerId.eq(query.getString("serverId")));
        }
        if(query.contains("all")){
            queryBuilder.list();
        }

        return queryBuilder;
    }

    @Override
    protected void map(CommentModel model) {

    }

    @Override
    protected void map(Comment comment, CommentModel model) {
        if(null !=model.getId()) {
            comment.setId(model.getId());
        }
        comment.setName(model.text);
        comment.setServerId(model.getServerId());
        comment.setDate(model.date);

    }

    @Override
    protected Comment newEntity() {
        return new Comment();
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
