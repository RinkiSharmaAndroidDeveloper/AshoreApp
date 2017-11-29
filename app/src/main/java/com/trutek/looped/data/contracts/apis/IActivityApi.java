package com.trutek.looped.data.contracts.apis;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;

/**
 * Created by msas on 10/10/2016.
 */
public interface IActivityApi<TModel> extends IAsyncRemoteApi<ActivityModel> {

    void joinActivity(String action, ActivityModel model, AsyncResult<ActivityModel> result);

}
