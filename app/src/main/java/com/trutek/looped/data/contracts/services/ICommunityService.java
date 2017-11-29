package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.List;

/**
 * Created by msas on 9/27/2016.
 */
public interface ICommunityService extends ICRUDService<CommunityModel> {

    void discoverCommunities(PageInput input, AsyncResult<Page<CommunityModel>> result);

    List<CommunityModel> allCommunities();

    List<CommunityModel> allMyCommunities();

    void discoverCommunitiesForFilter(String query, AsyncResult<Page<CommunityModel>> result);

    List<CommunityModel>  myCommunities();

    List<CommunityModel> joinedCommunities();

    void createCommunity(CommunityModel model, AsyncResult<CommunityModel> result);

    void updateCommunity(CommunityModel model, AsyncResult<CommunityModel> result);

    void getCommunity(String id, AsyncResult<CommunityModel> result);

    void joinCommunity(CommunityModel community, AsyncResult<CommunityModel> result);

    void joinCommunityToMember(CommunityModel community, AsyncResult<Page<CommunityModel>> result);

    void leaveCommunity(String profileId,CommunityModel community, AsyncNotify result);

    void unJoinCommunity(CommunityModel community, AsyncNotify notify);

    void inviteMembersIntoCommunity(CommunityModel communityModel, AsyncNotify notify);

    void fetchMyAllCommunities(ProfileModel myProfile);

    void fetchCommunities(AsyncResult<Page<CommunityModel>> result);

    void fetchJoinCommunities(PageInput pageInput);
}
