package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.impl.entities.Tag;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by msas on 9/28/2016.
 */
public class TagMapper implements IModelMapper<Tag, TagModel> {

    @Override
    public TagModel Map(Tag tag) {
        TagModel tagtModel=new TagModel();
        tagtModel.setId(tag.getId());
        tagtModel.setServerId(tag.getServerId());
        tagtModel.setName(tag.getName());
        tagtModel.setProfileId(tag.getProfileId());
        return tagtModel;
    }
}
