package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.apis.ICommunityApi;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.ModelState;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;
import com.trutek.looped.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class CommunityService extends BaseService<CommunityModel> implements ICommunityService {

    private IRepository<InterestModel> interestLocal;
    private IRepository<TagModel> tagLocal;
    private IRepository<ActivityModel> activityLocal;
    private ICommunityApi<CommunityModel> remote;
    private CommunityModel createModel;

    public CommunityService(IRepository<CommunityModel> local, IRepository<InterestModel> interestLocal, IRepository<TagModel> tagLocal,
                            IRepository<ActivityModel> activityLocal,ICommunityApi<CommunityModel> remote) {
        super(local);
        this.interestLocal = interestLocal;
        this.tagLocal = tagLocal;
        this.activityLocal = activityLocal;
        this.remote = remote;
    }

    @Override
    public void discoverCommunities(PageInput input, AsyncResult<Page<CommunityModel>> result) {
        remote.page(input, result);
    }

    @Override
    public void discoverCommunitiesForFilter(String query, AsyncResult<Page<CommunityModel>> result) {
        remote.page(null, query, result);
    }

    @Override
    public List<CommunityModel> myCommunities() {
        PageInput input = new PageInput();
        input.query.add("isMine", true);
     //   input.query.add("admin", true);
//        fetchMyCommunities(input);
        return search(input).items;

    }

    @Override
    public List<CommunityModel> joinedCommunities() {
        PageInput input = new PageInput();
      //  input.query.add("isMine", false);
//        input.query.add("notAdmin", true);
        fetchJoinCommunities(input);
        return search(input).items;
    }

    @Override
    public List<CommunityModel> allCommunities() {
        return search(new PageInput()).items;
    }
    @Override
    public List<CommunityModel> allMyCommunities() {
        PageInput input = new PageInput();
        return search(input).items;
    }

    @Override
    public void createCommunity(CommunityModel model, final AsyncResult<CommunityModel> result) {
        remote.create(model, new AsyncResult<CommunityModel>() {
            @Override
            public void success(CommunityModel communityModel) {
                communityModel.setSyncStatus(ModelState.synced.name());
                communityModel.setMine(true);
                _local.create(communityModel);
                result.success(communityModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void updateCommunity(CommunityModel model, AsyncResult<CommunityModel> result) {
        remote.update(model, result);
    }

    @Override
    public void getCommunity(String id,final AsyncResult<CommunityModel> result) {
        remote.get(id,result);
    }




    @Override
    public void joinCommunity(CommunityModel community, AsyncResult<CommunityModel> result) {
        String action = community.getServerId() + "/members";
        remote.joinCommunity(action, community, result);
    }

    @Override
    public void joinCommunityToMember(CommunityModel community, AsyncResult<Page<CommunityModel>> result) {
        String action = community.getServerId() + "/members";
        remote.joinCommunityMember(action, community, result);
    }

    @Override
    public void leaveCommunity(String profileId,CommunityModel community,  AsyncNotify notify) {
        String action = community.getServerId() + "/members/" + profileId;
        remote.delete(action,notify);
    }

    @Override
    public void unJoinCommunity(CommunityModel community, AsyncNotify notify) {

    }

    @Override
    public void inviteMembersIntoCommunity(CommunityModel community, final AsyncNotify notify) {

        String action = community.getServerId() + "/members";
        remote.joinCommunityMember(action, community, new AsyncResult<Page<CommunityModel>>() {

            @Override
            public void success(Page<CommunityModel> communityModelPage) {
                notify.success();
            }

            @Override
            public void error(String error) {
                notify.error(error);
            }
        });

    }


    @Override
    public void fetchCommunities(AsyncResult<Page<CommunityModel>> result) {
        remote.page(new PageInput(),result);
        /*remote.page(new PageInput(), new AsyncResult<Page<CommunityModel>>() {
            @Override
            public void success(Page<CommunityModel> communityModelPage) {
                for (CommunityModel communityModel : communityModelPage.items) {
                    communityModel.setMine(true);
                    communityModel.setSyncStatus(ModelState.synced.name());
                    CommunityModel createModel=_local.getByServerId(communityModel.getServerId());
                    if(createModel==null){
                        _local.create(communityModel,Constants.BROADCAST_MY_COMMUNITIES);
                    }else {
                        _local.update(createModel.getId(),communityModel,Constants.BROADCAST_MY_COMMUNITIES);
                    }
                }
            }

            @Override
            public void error(String error) {

            }
        });*/
    }

    @Override
    public void fetchJoinCommunities(PageInput pageInput) {
        PageInput input = new PageInput();
        input.query.add("mineOnly", true);
//        input.query.add("notAdmin", true);
        remote.page(input, new AsyncResult<Page<CommunityModel>>() {
            @Override
            public void success(Page<CommunityModel> communityModelPage) {
                for (CommunityModel communityModel : communityModelPage.items) {
                    communityModel.setMine(true);
                    communityModel.setSyncStatus(ModelState.synced.name());
                    CommunityModel createModel=_local.getByServerId(communityModel.getServerId());
                    if(createModel==null){
                        _local.create(communityModel);
                    }else {
                        communityModel.setId(createModel.getId());
                        _local.update(createModel.getId(),communityModel);
                    }

                }
                _local.notifyBroadCast(Constants.BROADCAST_JOINED_COMMUNITIES);
            }

            @Override
            public void error(String error) {

            }
        });
    }

    @Override
    public void fetchMyAllCommunities(final ProfileModel myProfile) {
        PageInput input = new PageInput();
        input.query.add("mineOnly", true);
        remote.page(input, new AsyncResult<Page<CommunityModel>>() {
            @Override
            public void success(Page<CommunityModel> communityModelPage) {
                for (CommunityModel communityModel : communityModelPage.items) {

                    communityModel.setMine(communityModel.getAdmin().getServerId().equals(myProfile.getServerId()));

                    CommunityModel createModel=_local.getByServerId(communityModel.getServerId());
                    if(createModel==null){
                        communityModel.setId(_local.create(communityModel).getId());

                        for (ActivityModel activityModel:communityModel.activities) {
                            activityModel.setCommunity(communityModel);
                            if(communityModel.getAdmin().getServerId().equals(myProfile.getServerId())){
                                activityModel.setMine(true);
                            }else {
                                activityModel.setMine(false);
                            }
                            activityLocal.create(activityModel);
                        }

                    }else {
                        communityModel.setId(createModel.getId());
                        update(communityModel);

                        for (ActivityModel model : communityModel.activities) {
                            model.setCommunity(communityModel);
                            ActivityModel activityModel = activityLocal.getByServerId(model.getServerId());
                            if (activityModel == null) {
                                if(communityModel.getAdmin().getServerId().equals(myProfile.getServerId())){
                                    model.setMine(true);
                                }else {
                                    model.setMine(false);
                                }
                                activityLocal.create(model);
                            } else {
                                model.setId(activityModel.getId());
                                model.setMine(activityModel.isMine);
                                activityLocal.update(model.getId(),model);
                            }
                        }
                    }
                }

                _local.notifyBroadCast(Constants.BROADCAST_MY_COMMUNITIES);
            }

            @Override
            public void error(String error) {

            }
        });
    }

}
