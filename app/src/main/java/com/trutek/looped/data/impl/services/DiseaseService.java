package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.contracts.services.IDiseaseService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.List;

public class DiseaseService extends BaseService<DiseaseModel> implements IDiseaseService {

    IAsyncRemoteApi<DiseaseModel> mRemoteApi;
    public DiseaseService(IAsyncRemoteApi<DiseaseModel> remoteApi) {
        super(null);
        mRemoteApi = remoteApi;
    }

    @Override
    public void getAllDisease(final AsyncResult<List<DiseaseModel>> result) {
        mRemoteApi.page(new PageInput(),null, new AsyncResult<Page<DiseaseModel>>() {
            @Override
            public void success(Page<DiseaseModel> diseaseModelPage) {
                result.success(diseaseModelPage.items);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }
}
