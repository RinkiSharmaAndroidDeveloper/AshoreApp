package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;

/**
 * Created by Amrit on 20/02/17.
 */

public interface ILocationService extends ICRUDService<LocationModel> {

    void fetchNearByLocation(LocationModel locationModel, AsyncResult<Page<LocationModel>> result);
}
