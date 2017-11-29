package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;

/**
 * Created by Amrit on 02/12/16.
 */
public interface IHealthParamService extends ICRUDService<HealthParameterModel> {

    void getAll(AsyncResult<Page<HealthParameterModel>> result);
}
