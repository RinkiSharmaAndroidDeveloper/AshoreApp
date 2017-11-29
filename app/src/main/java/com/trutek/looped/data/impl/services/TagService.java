package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.apis.ITagApi;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.ITagService;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.List;

/**
 * Created by msas on 9/28/2016.
 */
public class TagService extends BaseService<TagModel> implements ITagService{

    private ITagApi<TagModel> remote;

    public TagService(IRepository<TagModel> local, ITagApi<TagModel> remote) {
        super(local);
        this.remote = remote;
    }

    @Override
    public void getAll(PageInput input, AsyncResult<Page<TagModel>> result) {
        remote.page(input, result);
    }

    @Override
    public TagModel saveTag(TagModel tagModel) {
       return create(tagModel,null);
    }

    @Override
    public void saveTag(List<TagModel> tagModels) {
        for (TagModel tagModel:tagModels) {
            create(tagModel,null);
        }

    }

    @Override
    public void deleteInterest(TagModel tagModel) {
        delete(tagModel.getId());
    }

    @Override
    public List<TagModel> getTag(ProfileModel profileModel) {
        PageInput pageInput=new PageInput();
        pageInput.query.add(ProfileModel.KEY_PROFILE_ID,profileModel.getId());
        return search(pageInput).items;
    }

}
