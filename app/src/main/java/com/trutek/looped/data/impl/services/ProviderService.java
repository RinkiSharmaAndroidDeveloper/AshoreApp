package com.trutek.looped.data.impl.services;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.data.contracts.services.IProviderService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandy on 12/5/2016.
 */

public class ProviderService extends BaseService<ProviderModel> implements IProviderService {

    public ProviderService(IRepository<ProviderModel> local) {
        super(local);
    }


    @Override
    public void saveProviders(List<ProviderModel> ProviderModels) {
        for (ProviderModel ProviderModel : ProviderModels) {
            ProviderModel.setId(create(ProviderModel,null).getId());
        }
    }

    @Override
    public ProviderModel saveProvider(ProviderModel ProviderModel) {
        return create(ProviderModel,null);
    }

    @Override
    public List<ProviderModel> getProvidersLocally(Long recipientId) {
        PageInput pageInput = new PageInput();
        pageInput.query.add(Constants.REPO_KEY_RECIPEINT_ID,recipientId);
        return search(pageInput).items;
    }

    @Override
    public void deletePreviousProviders() {
        deleteAll();
    }
}
