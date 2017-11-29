package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.data.impl.entities.InterestDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class InterestRepository extends BaseRepository<Interest, InterestModel> {


    private AbstractDao<Interest, Long> dao;

    public InterestRepository(IModelMapper<Interest, InterestModel> mapper, AbstractDao<Interest, Long> dao) {
        super(null,mapper, dao, InterestRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Interest> query(Long id) {
        return dao.queryBuilder().where(InterestDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Interest> query(PageQuery query) {
        QueryBuilder<Interest> queryBuilder = dao.queryBuilder();

        if(query.contains(ProfileModel.KEY_PROFILE_ID)){
            queryBuilder.where(InterestDao.Properties.ProfileId.eq(query.getLong(ProfileModel.KEY_PROFILE_ID)));
        }
        return queryBuilder;
    }

    @Override
    protected void map(InterestModel model) {

    }

    @Override
    protected void map(Interest interest, InterestModel model) {

        interest.setId(model.getId());
        interest.setName(model.getName());
        interest.setServerId(model.getServerId());
        interest.setProfileId(model.getProfileId());
    }

    @Override
    protected Interest newEntity() {
        return new Interest();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
