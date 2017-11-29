package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.impl.entities.Disease;
import com.trutek.looped.data.impl.entities.DiseaseDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class DiseaseRepository extends BaseRepository<Disease, DiseaseModel> {

    private IModelMapper<Disease, DiseaseModel> mapper;
    private AbstractDao<Disease, Long> dao;

    public DiseaseRepository(Context context,IModelMapper<Disease, DiseaseModel> mapper, AbstractDao<Disease, Long> dao) {
        super(context,mapper, dao, DiseaseRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Disease> query(Long id) {
        return dao.queryBuilder().where(DiseaseDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Disease> query(PageQuery query) {
        QueryBuilder<Disease> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(DiseaseDao.Properties.Id.ge(query.getDate("id")));
        }
        if(query.contains("serverId")){
            queryBuilder.where(DiseaseDao.Properties.ServerId.eq(query.getString("serverId")));
        }
        if(query.contains("recipientId")){
            queryBuilder.where(DiseaseDao.Properties.RecipientId.eq(query.getString("recipientId")));
        }
        return queryBuilder;
    }

    @Override
    protected void map(DiseaseModel model) {

    }

    @Override
    protected void map(Disease disease, DiseaseModel model) {
        disease.setId(model.getId());
        disease.setServerId(model.getServerId());
        disease.setName(model.getName());
        disease.setRecipientId(model.getRecipientId());
    }

    @Override
    protected Disease newEntity() {
        return new Disease();
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
