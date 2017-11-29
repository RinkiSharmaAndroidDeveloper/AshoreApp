package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.msas.common.contracts.IModelMapper;

public class InterestMapper implements IModelMapper<Interest, InterestModel> {

    @Override
    public InterestModel Map(Interest interest) {
        InterestModel interestModel=new InterestModel();
        interestModel.setId(interest.getId());
        interestModel.setServerId(interest.getServerId());
        interestModel.setName(interest.getName());
        interestModel.setProfileId(interest.getProfileId());
        return interestModel;
    }
}
