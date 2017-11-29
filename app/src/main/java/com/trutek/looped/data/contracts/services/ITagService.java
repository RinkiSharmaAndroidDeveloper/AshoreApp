package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.List;

/**
 * Created by msas on 9/28/2016.
 */
public interface ITagService extends ICRUDService<TagModel> {

    void getAll(PageInput input, AsyncResult<Page<TagModel>> result);
    TagModel saveTag(TagModel tagModel);

    void   saveTag(List<TagModel> tagModels);

    void  deleteInterest(TagModel tagModel);

    List<TagModel> getTag(ProfileModel profileModel);
}
