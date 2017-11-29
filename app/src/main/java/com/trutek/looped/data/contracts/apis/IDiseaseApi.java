package com.trutek.looped.data.contracts.apis;

import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;

public interface IDiseaseApi<TModel> extends IAsyncRemoteApi<DiseaseModel> {
}
