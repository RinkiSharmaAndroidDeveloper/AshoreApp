package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Category;
import com.trutek.looped.data.impl.entities.CategoryDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Rinki on 1/19/2017.
 */
public class CategoryRepository extends BaseRepository<Category, CategoryModel> {
    private AbstractDao<Category, Long> dao;
    private IModelMapper<Category, CategoryModel> mapper;

    public CategoryRepository(Context context, IModelMapper<Category, CategoryModel> mapper, AbstractDao<Category, Long> dao) {
        super(context, mapper, dao, CategoryRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Category> query(Long id) {
        return null;
    }

    @Override
    protected QueryBuilder<Category> query(PageQuery query) {
        QueryBuilder<Category> queryBuilder = dao.queryBuilder();


        if(query.contains(ProfileModel.KEY_PROFILE_ID)){
            queryBuilder.where(CategoryDao.Properties.ProfileId.eq(query.getLong(ProfileModel.KEY_PROFILE_ID)));
        }
        return queryBuilder;

    }

    @Override
    protected void map(CategoryModel model) {

    }

    @Override
    protected void map(Category category, CategoryModel model) {
        category.setId(model.getId());
        category.setServerId(model.getServerId());
        category.setName(model.getName());
        category.setProfileId(model.getProfileId());
    }

    @Override
    protected Category newEntity() {
        return new Category();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
