package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.data.impl.entities.Provider;
import com.trutek.looped.data.impl.entities.ProviderDao;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Sandy on 12/5/2016.
 */

public class ProviderRepository  extends BaseRepository<Provider,ProviderModel> {

    private AbstractDao<Provider, Long> dao;

    public ProviderRepository(Context context, IModelMapper<Provider, ProviderModel> mapper, AbstractDao<Provider, Long> dao) {
        super(context,mapper, dao, ProviderRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Provider> query(Long id) {

        return dao.queryBuilder().where(ProviderDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Provider> query(PageQuery query) {
        QueryBuilder<Provider> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(ProviderDao.Properties.Id.eq(query.getLong("id")));
        }
        if(query.contains(Constants.REPO_KEY_RECIPEINT_ID)){
            queryBuilder.where(ProviderDao.Properties.RecipientId.eq(query.getLong(Constants.REPO_KEY_RECIPEINT_ID)));
        }
        return queryBuilder;
    }

    @Override
    protected void map(ProviderModel model) {

    }

    @Override
    protected void map(Provider provider, ProviderModel model) {

        provider.setId(model.getId());
        provider.setName(model.getName());
        provider.setNumber(model.getPhone());
        provider.setRecipientId(model.getRecipientId());

    }

    @Override
    protected Provider newEntity() {

        return new Provider();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
