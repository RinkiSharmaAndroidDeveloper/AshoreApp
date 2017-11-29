package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.data.impl.entities.HealthParam;
import com.trutek.looped.msas.common.contracts.IModelMapper;

import java.util.ArrayList;

/**
 * Created by Amrit on 03/12/16.
 */
public class HealthParamMapper implements IModelMapper<HealthParam,HealthParameterModel> {
    @Override
    public HealthParameterModel Map(HealthParam healthParam) {
        HealthParameterModel healthParameterModel = new HealthParameterModel();

        healthParameterModel.setId(healthParam.getId());
        healthParameterModel.setServerId(healthParam.getServerId());
        healthParameterModel.setName(healthParam.getName());

        ArrayList<String> units = new ArrayList<>();
        units.add(healthParam.getUnit());

        healthParameterModel.setUnits(units);
        return healthParameterModel;
    }
}
