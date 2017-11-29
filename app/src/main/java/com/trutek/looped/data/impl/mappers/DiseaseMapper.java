package com.trutek.looped.data.impl.mappers;


import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.impl.entities.Disease;
import com.trutek.looped.msas.common.contracts.IModelMapper;

public class DiseaseMapper implements IModelMapper<Disease, DiseaseModel> {

    @Override
    public DiseaseModel Map(Disease disease) {
        DiseaseModel model = new DiseaseModel();
        model.setId(disease.getId());
        model.setServerId(disease.getServerId());
        model.setName(disease.getName());
        model.setRecipientId(disease.getRecipientId());
        return model;
    }
}
