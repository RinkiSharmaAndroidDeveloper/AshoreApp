package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.services.ILocationService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

/**
 * Created by Amrit on 20/02/17.
 */

public class LocationService extends BaseService<LocationModel> implements ILocationService {

    private IAsyncRemoteApi<LocationModel> remoteApi;

    public LocationService(IRepository<LocationModel> local, IAsyncRemoteApi<LocationModel> remoteApi) {
        super(local);
        this.remoteApi = remoteApi;
    }

    @Override
    public void fetchNearByLocation(LocationModel locationModel, AsyncResult<Page<LocationModel>> result) {

        if(null == locationModel.coordinates || locationModel.coordinates.size()<2){
            result.error("Invalid location");
            return;
        }

        PageInput input = new PageInput();
        input.query.add("lat",locationModel.coordinates.get(1));
        input.query.add("lng",locationModel.coordinates.get(0));
        remoteApi.page(input,result);
    }
}
