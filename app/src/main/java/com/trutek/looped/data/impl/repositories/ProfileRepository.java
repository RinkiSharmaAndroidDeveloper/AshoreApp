package com.trutek.looped.data.impl.repositories;


import android.content.Context;

import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.data.impl.entities.ProfileDao;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public class ProfileRepository extends BaseRepository<Profile, ProfileModel> {

    private IModelMapper<Profile, ProfileModel> mapper;
    private AbstractDao<Profile, Long> dao;

    public ProfileRepository(Context context,IModelMapper<Profile, ProfileModel> mapper, AbstractDao<Profile, Long> dao) {
        super(context,mapper, dao, ProfileRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Profile> query(Long id) {
        return dao.queryBuilder().where(ProfileDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Profile> query(PageQuery query) {
        QueryBuilder<Profile> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(ProfileDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains("serverId")){
            queryBuilder.where(ProfileDao.Properties.ServerId.eq(query.getString("serverId")));
        }
        if(query.contains(ProfileModel.KEY_MINE)){
            queryBuilder.where(ProfileDao.Properties.IsMine.eq(query.getBoolean(ProfileModel.KEY_MINE)));
        }
        return queryBuilder;
    }

    @Override
    protected void map(ProfileModel model) {

    }

    @Override
    protected void map(Profile profile, ProfileModel model) {
        if(null !=model.getId()) {
            profile.setId(model.getId());
        }
        profile.setName(model.getName());
        profile.setIsMine(model.isMine);
        profile.setServerId(model.getServerId());
        profile.setAbout(model.getAbout());
        profile.setDateOfBirth(model.dateOfBirth);
        profile.setPicUrl(model.getPicUrl());
        profile.setAge(model.getage());
        profile.setGender(model.getGender());

        if(null != model.getLocation() && model.getLocation().coordinates.size()>0) {
            profile.setLocation(model.getLocation().getName());
            profile.setLocationlng(model.getLocation().coordinates.get(0));
            profile.setLocationlat(model.getLocation().coordinates.get(1));
        }

     }

    @Override
    protected Profile newEntity() {
        return new Profile();
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
