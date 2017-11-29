package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.LoopModel;
import com.trutek.looped.data.impl.entities.Loop;
import com.trutek.looped.msas.common.contracts.IModelMapper;

public class LoopMapper implements IModelMapper<Loop, LoopModel> {

    @Override
    public LoopModel Map(Loop loop) {
        LoopModel model = new LoopModel();
        model.setId(loop.getId());
        model.setName(loop.getName());
        model.setProfileId(loop.getProfileId());
        model.setServerId(loop.getServerId());
        model.setPicUrl(loop.getPicUrl());
        model.setRole(loop.getRole());
        model.setLoopStatus(loop.getStatus());
        return model;
    }
}
