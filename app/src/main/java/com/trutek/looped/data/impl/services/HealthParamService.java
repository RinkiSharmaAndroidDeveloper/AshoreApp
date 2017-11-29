package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.data.contracts.services.IHealthParamService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

/**
 * Created by Amrit on 02/12/16.
 */
public class HealthParamService extends BaseService<HealthParameterModel> implements IHealthParamService {

    IAsyncRemoteApi<HealthParameterModel> mRemoteApi;

    public HealthParamService(IRepository<HealthParameterModel> local, IAsyncRemoteApi<HealthParameterModel> remoteApi) {
        super(local);
        mRemoteApi = remoteApi;
    }


    @Override
    public void getAll(AsyncResult<Page<HealthParameterModel>> result) {
        mRemoteApi.page(new PageInput(), result);
    }
}
