package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.impl.entities.Recipient;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.PageInput;

public class RecipientMapper implements IModelMapper<Recipient, RecipientModel> {

    private IRepository<DiseaseModel> diseases;

    public RecipientMapper(IRepository<DiseaseModel> diseases){

        this.diseases = diseases;
    }

    @Override
    public RecipientModel Map(Recipient recipient) {
        RecipientModel model = new RecipientModel();
        model.setId(recipient.getId());
        model.setServerId(recipient.getServerId());
        model.setName(recipient.getName());
        model.setAge(recipient.getAge());
        model.setGender(recipient.getGender());
        model.setPicUrl(recipient.getPicUrl());

        if(recipient.getServerId() != null){
            PageInput input = new PageInput();
            input.query.add("recipientId", recipient.getServerId());
            model.setDiseases(diseases.page(input).items);
        }

        return model;
    }
}
