package com.trutek.looped.data.contracts.services;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.msas.common.contracts.ICRUDService;

import java.util.List;

/**
 * Created by Sandy on 12/5/2016.
 */

public interface IProviderService extends ICRUDService<ProviderModel> {
    void saveProviders( List<ProviderModel> ProviderModels);

    ProviderModel saveProvider(ProviderModel ProviderModel);

    List<ProviderModel> getProvidersLocally(Long recipientId);

    void deletePreviousProviders();
}
