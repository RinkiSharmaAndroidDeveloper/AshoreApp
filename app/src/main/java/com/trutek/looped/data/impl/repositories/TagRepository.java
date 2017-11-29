package com.trutek.looped.data.impl.repositories;

import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.data.impl.entities.InterestDao;
import com.trutek.looped.data.impl.entities.Tag;
import com.trutek.looped.data.impl.entities.TagDao;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by msas on 9/28/2016.
 */
public class TagRepository extends BaseRepository<Tag, TagModel> {
    private AbstractDao<Tag, Long> dao;

    public TagRepository(IModelMapper<Tag, TagModel> mapper, AbstractDao<Tag, Long> dao) {
        super(null,mapper, dao, TagRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Tag> query(Long id) {
        return  dao.queryBuilder().where(TagDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<Tag> query(PageQuery query) {
        QueryBuilder<Tag> queryBuilder = dao.queryBuilder();

        if(query.contains(ProfileModel.KEY_PROFILE_ID)){
            queryBuilder.where(TagDao.Properties.ProfileId.eq(query.getLong(ProfileModel.KEY_PROFILE_ID)));
        }
        return queryBuilder;
    }

    @Override
    protected void map(TagModel model) {

    }

    @Override
    protected void map(Tag tag, TagModel model) {
        tag.setId(model.getId());
        tag.setName(model.getName());
        tag.setServerId(model.getServerId());
        tag.setProfileId(model.getProfileId());

    }

    @Override
    protected Tag newEntity() {
      return new Tag();
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
