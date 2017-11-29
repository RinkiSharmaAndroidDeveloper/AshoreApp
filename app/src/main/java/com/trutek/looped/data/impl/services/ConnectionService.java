package com.trutek.looped.data.impl.services;

import android.util.Log;

import com.trutek.looped.data.contracts.apis.IConnectionApi;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.impl.entities.Connection;
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

public class ConnectionService extends BaseService<ConnectionModel> implements IConnectionService {

    private IConnectionApi<ConnectionModel> remote;

    public ConnectionService(IRepository<ConnectionModel> local, IConnectionApi<ConnectionModel> remote) {
        super(local);
        this.remote = remote;
    }

    @Override
    public void myConnection(PageInput input, final AsyncResult<List<ConnectionModel>> result) {
        remote.page(input, new AsyncResult<Page<ConnectionModel>>() {
            @Override
            public void success(Page<ConnectionModel> connectionModelPage) {
                List<ConnectionModel> filteredList = new ArrayList<>();

                for (ConnectionModel model : connectionModelPage.items) {
                    if (model.status.equalsIgnoreCase(ProfileModel.Status.Active.name())) {
                        filteredList.add(model);
                    }
                }

                result.success(filteredList);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void myConnectionForFilter(String query, AsyncResult<Page<ConnectionModel>> result) {
        remote.page(null, query, result);
    }

    @Override
    public void linkConnection(ConnectionModel model, AsyncResult<ConnectionModel> result) {
        remote.create(model, result);
    }

    @Override
    public void acceptConnectionRequest(ConnectionModel model, AsyncResult<ConnectionModel> result) {
        remote.update(model.getServerId(),model, result);
    }

    @Override
    public void deLinkConnection(final ConnectionModel model, final AsyncNotify result) {
        remote.delete(model.getServerId(), new AsyncNotify() {
            @Override
            public void success() {
                delete(model.getId());
                result.success();
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void cancelLinkConnection(ConnectionModel model, AsyncResult<ConnectionModel> result) {
        remote.update("cancel/"+model.profile.getServerId(),result);
    }

    @Override
    public void getConnectionRequest(PageInput input, final AsyncResult<List<ConnectionModel>> result) {
        remote.page(input, new AsyncResult<Page<ConnectionModel>>() {
            @Override
            public void success(Page<ConnectionModel> connectionModelPage) {
                List<ConnectionModel> filteredList = new ArrayList<>();

                for (ConnectionModel model : connectionModelPage.items) {
                    if(model.status.equalsIgnoreCase(ProfileModel.Status.InComming.name())){
                        filteredList.add(model);
                    }
                }

                result.success(filteredList);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public List<ConnectionModel> myConnection() {
        PageInput input = new PageInput();
        input.query.add(ProfileModel.KEY_MINE, false);
        return search(input).items;
    }


    @Override
    public List<ConnectionModel> getMyConnections(AsyncResult<Page<ConnectionModel>> result) {
        PageInput input = new PageInput();
        input.query.add(ProfileModel.KEY_MINE, false);
        return search(input).items;
    }

    @Override
    public void fetchMyConnections(PageInput pageInput) {
        PageInput input = new PageInput();
        remote.page(input, new AsyncResult<Page<ConnectionModel>>() {
            @Override
            public void success(Page<ConnectionModel> connectionModelPage) {
                for (ConnectionModel model : connectionModelPage.items) {
                    ConnectionModel createModel = _local.getByServerId(model.getServerId());
                    if (createModel == null) {
                        _local.create(model);
                    } else {
                        model.setId(createModel.getId());
                        _local.update(createModel.getId(), model);
                    }

                }
                _local.notifyBroadCast(Constants.BROADCAST_MY_CONNECTIONS);
            }

            @Override
            public void error(String error) {

            }
        });
    }
}
   /* public List<ConnectionModel> myConnection(PageInput input, final AsyncResult<List<ConnectionModel>> result) {
        remote.page(input, new AsyncResult<Page<ConnectionModel>>() {
            @Override
            public void success(Page<ConnectionModel> connectionModelPage) {
                List<ConnectionModel> filteredList = new ArrayList<>();

                for (ConnectionModel model : connectionModelPage.items) {
                    if (model.status.equalsIgnoreCase(ProfileModel.Status.Active.name())) {
                        filteredList.add(model);
                    }
                }

                result.success(filteredList);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });

    }
*/