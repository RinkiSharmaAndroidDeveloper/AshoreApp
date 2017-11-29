package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.impl.entities.HealthParamLog;
import com.trutek.looped.data.impl.entities.HealthParamLogDao;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Amrit on 10/12/16.
 */
public class HealthParamLogRepository extends BaseRepository<HealthParamLog,HealthChartLogsModel> {

    public static final String QUERY_KEY_BY_HEALTH_CHART_ID = "healthChartId";
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";

    private AbstractDao<HealthParamLog, Long> dao;

    public HealthParamLogRepository(Context context, IModelMapper<HealthParamLog, HealthChartLogsModel> mapper, AbstractDao<HealthParamLog, Long> dao) {
        super(context, mapper, dao, HealthParamLogRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<HealthParamLog> query(Long id) {
        return dao.queryBuilder().where(HealthParamLogDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<HealthParamLog> query(PageQuery query) {

        QueryBuilder<HealthParamLog> queryBuilder = dao.queryBuilder();

        if(query.contains(Constants.QUERY_KEY_SERVER_ID)){
            queryBuilder.where(HealthParamLogDao.Properties.ServerId.eq(query.getString(Constants.QUERY_KEY_SERVER_ID)));
        }

        if(query.contains(QUERY_KEY_BY_HEALTH_CHART_ID)){
            queryBuilder.where(HealthParamLogDao.Properties.HealthChartId.eq(query.getLong(QUERY_KEY_BY_HEALTH_CHART_ID)));
        }

        if(query.contains(FROM_DATE)){
            queryBuilder.where(HealthParamLogDao.Properties.CreatedAt.ge(query.getDate(FROM_DATE)));
        }

        if(query.contains(TO_DATE)){
            queryBuilder.where(HealthParamLogDao.Properties.CreatedAt.lt(query.getDate(TO_DATE)));
        }


        return queryBuilder;
    }

    @Override
    protected void map(HealthChartLogsModel model) {

    }

    @Override
    protected void map(HealthParamLog healthParamLog, HealthChartLogsModel model) {
        healthParamLog.setId(model.getId());
        healthParamLog.setServerId(model.getServerId());
        healthParamLog.setCreatedAt(model.getCreate_At());
        healthParamLog.setValue(model.getValue());
        healthParamLog.setUnit(model.getUnit());
        healthParamLog.setHealthChartId(model.getHealthChartLocalId());
    }

    @Override
    protected HealthParamLog newEntity() {
        return new HealthParamLog();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
