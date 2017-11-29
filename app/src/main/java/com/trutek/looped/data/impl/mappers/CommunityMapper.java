package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.ArrayList;

/**
 * Created by msas on 9/28/2016.
 */
public class CommunityMapper implements IModelMapper<Community, CommunityModel> {
    private IRepository<ActivityModel> activityLocal;

    public CommunityMapper(IRepository<ActivityModel> activityLocal) {

        this.activityLocal = activityLocal;
    }

    @Override
    public CommunityModel Map(Community community) {
        CommunityModel model = new CommunityModel();
        model.setId(community.getId());
        model.setSubject(community.getSubject());
        model.setBody(community.getBody());
        model.setMembersCount(community.getMembersCount());
        model.setFriendsCount(community.getFriendsCount());
        model.setPicData(community.getPicData());
        model.setPicUrl(community.getPicUrl());
        model.setPrivate(community.getIsPrivate());
        model.setServerId(community.getServerId());
        model.setTimeStamp(community.getTimeStamp());
        model.setSyncStatus(community.getSyncStatus());

        PageInput input = new PageInput();
        input.query.add(Constants.KEY_COMMUNITY_ID,community.getId());
        model.activities = new ArrayList<>(activityLocal.page(input).items);

        model.setMine(community.getIsMine());
        return model;
    }
}
