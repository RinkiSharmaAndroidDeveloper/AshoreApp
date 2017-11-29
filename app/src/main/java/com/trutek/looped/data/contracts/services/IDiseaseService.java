package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;

import java.util.List;

public interface IDiseaseService extends ICRUDService<DiseaseModel> {

    void getAllDisease(AsyncResult<List<DiseaseModel>> result);
}
