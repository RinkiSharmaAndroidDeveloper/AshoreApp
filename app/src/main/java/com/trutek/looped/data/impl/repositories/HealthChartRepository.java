package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.data.impl.entities.HealthChart;
import com.trutek.looped.data.impl.entities.HealthChartDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Amrit on 03/12/16.
 */
public class HealthChartRepository extends BaseRepository<HealthChart,HealthChartModel> {


    private AbstractDao<HealthChart, Long> dao;

    public HealthChartRepository(Context context, IModelMapper<HealthChart, HealthChartModel> mapper, AbstractDao<HealthChart, Long> dao) {
        super(context,mapper, dao, HealthChartRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<HealthChart> query(Long id) {
        return dao.queryBuilder().where(HealthChartDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<HealthChart> query(PageQuery query) {
        QueryBuilder<HealthChart> queryBuilder =dao.queryBuilder();
        if(query.contains("serverId")){
            queryBuilder.where(HealthChartDao.Properties.ServerId.eq(query.getString("serverId")));
        }
        return queryBuilder;
    }

    @Override
    protected void map(HealthChartModel model) {

    }

    @Override
    protected void map(HealthChart healthChart, HealthChartModel model) {
        healthChart.setId(model.getId());
        healthChart.setServerId(model.getServerId());
        healthChart.setHealthParamId(model.getHealthParam().getId());
    }

    @Override
    protected HealthChart newEntity() {
        return new HealthChart();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
