package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.List;

/**
 * Created by msas on 9/29/2016.
 */
public interface IConnectionService extends ICRUDService<ConnectionModel> {

    void myConnection(PageInput input, AsyncResult<List<ConnectionModel>> result);

    void myConnectionForFilter(String query, AsyncResult<Page<ConnectionModel>> result);

    void linkConnection(ConnectionModel model, AsyncResult<ConnectionModel> result);
    void acceptConnectionRequest(ConnectionModel model, AsyncResult<ConnectionModel> result);

    void deLinkConnection(ConnectionModel model, AsyncNotify result);

    void cancelLinkConnection(ConnectionModel model, AsyncResult<ConnectionModel> result);

    void getConnectionRequest(PageInput input, AsyncResult<List<ConnectionModel>> result);
    List<ConnectionModel> myConnection();
    List<ConnectionModel> getMyConnections(AsyncResult<Page<ConnectionModel>> result);
   void fetchMyConnections(PageInput pageInput);
}
