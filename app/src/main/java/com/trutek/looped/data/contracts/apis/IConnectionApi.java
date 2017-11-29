package com.trutek.looped.data.contracts.apis;

import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;

/**
 * Created by msas on 9/29/2016.
 */
public interface IConnectionApi<TModel> extends IAsyncRemoteApi<ConnectionModel> {
}
