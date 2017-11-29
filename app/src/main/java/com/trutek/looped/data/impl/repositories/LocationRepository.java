package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.data.impl.entities.Location;
import com.trutek.looped.data.impl.entities.LocationDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Rinki on 12/21/2016.
 */
public class LocationRepository extends BaseRepository<Location, LocationModel> {
    private AbstractDao<Location, Long> dao;

    public LocationRepository(IModelMapper<Location, LocationModel> mapper, AbstractDao<Location, Long> dao) {
        super(null,mapper, dao, LocationRepository.class.getSimpleName());
        this.dao = dao;

    }

    @Override
    protected QueryBuilder<Location> query(Long id) {
        return dao.queryBuilder().where(LocationDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Location> query(PageQuery query) {
        QueryBuilder<Location> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(LocationDao.Properties.Id.eq(query.getLong("id")));
        }
        return queryBuilder;
    }

    @Override
    protected void map(LocationModel model) {

    }

    @Override
    protected void map(Location location, LocationModel model) {
        location.setId(model.getId());
        location.setName(model.getName());
        location.setServerId(model.getServerId());

    }

    @Override
    protected Location newEntity() {
        return new Location();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
