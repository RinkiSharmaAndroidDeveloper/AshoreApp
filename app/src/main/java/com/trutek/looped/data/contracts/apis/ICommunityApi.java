package com.trutek.looped.data.contracts.apis;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.models.Page;

/**
 * Created by msas on 9/28/2016.
 */
public interface ICommunityApi<TModel> extends IAsyncRemoteApi<CommunityModel> {

    void joinCommunity(String action, CommunityModel model, AsyncResult<CommunityModel> result);

    void joinCommunityMember(String action, CommunityModel model, AsyncResult<Page<CommunityModel>> result);

   // void leaveCommunity(CommunityModel action, AsyncNotify notify);

}
