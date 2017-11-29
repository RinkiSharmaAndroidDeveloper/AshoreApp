package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.impl.entities.Location;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by Rinki on 12/21/2016.
 */
public class LocationMapper implements IModelMapper<Location, LocationModel> {
    @Override
    public LocationModel Map(Location location) {
        LocationModel locationModel = new LocationModel();
        locationModel.setId(location.getId());
        locationModel.setServerId(location.getServerId());
        locationModel.name = location.getName();
        return locationModel;

    }
}
