package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.data.impl.entities.Provider;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by Sandy on 12/5/2016.
 */

public class ProviderMapper implements IModelMapper<Provider, ProviderModel> {
    @Override
    public ProviderModel Map(Provider provider) {
        ProviderModel ProviderModel = new ProviderModel();
        ProviderModel.setId(provider.getId());
        ProviderModel.setName(provider.getName());
        ProviderModel.setPhone(provider.getNumber());
        return ProviderModel;
    }
}
