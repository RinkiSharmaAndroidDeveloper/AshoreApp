package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.data.impl.entities.HealthParam;
import com.trutek.looped.data.impl.entities.HealthParamDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Amrit on 03/12/16.
 */
public class HealthParamRepository extends BaseRepository<HealthParam, HealthParameterModel> {

    private AbstractDao<HealthParam, Long> dao;

    public HealthParamRepository(Context context, IModelMapper<HealthParam, HealthParameterModel> mapper, AbstractDao<HealthParam, Long> dao) {
        super(context,mapper, dao, HealthParamRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<HealthParam> query(Long id) {
        return dao.queryBuilder().where(HealthParamDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<HealthParam> query(PageQuery query) {
        QueryBuilder<HealthParam> queryBuilder = dao.queryBuilder();

        if(query.contains("serverId")){
            queryBuilder.where(HealthParamDao.Properties.ServerId.eq(query.getString("serverId")));
        }

        return queryBuilder;
    }

    @Override
    protected void map(HealthParameterModel model) {

    }

    @Override
    protected void map(HealthParam healthParam, HealthParameterModel model) {
        healthParam.setId(model.getId());
        healthParam.setName(model.getName());
        healthParam.setServerId(model.getServerId());
        healthParam.setUnit(model.getUnits().get(0));
    }

    @Override
    protected HealthParam newEntity() {
        return new HealthParam();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
