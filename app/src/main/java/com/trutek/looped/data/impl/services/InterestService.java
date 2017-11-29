package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.apis.IInterestApi;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msas on 9/28/2016.
 */
public class InterestService extends BaseService<InterestModel> implements IInterestService{

    private IInterestApi<InterestModel> remote;

    public InterestService(IRepository<InterestModel> local, IInterestApi<InterestModel> remote) {
        super(local);
        this.remote = remote;
    }

    @Override
    public void getAll(PageInput input, AsyncResult<Page<InterestModel>> result) {
        remote.page(input, result);
    }

    @Override
    public void getAll(String action, AsyncResult<Page<InterestModel>> result) {
        remote.page(null,action, result);

    }

    @Override
    public void getAllInterest(String id, AsyncResult<Page<InterestModel>> result) {
        remote.page(null,id,result);
    }

    @Override
    public InterestModel saveInterest(InterestModel interestModel) {
        return create(interestModel,null);
    }


    @Override
    public void saveInterests(List<InterestModel> interestModels) {
        for (InterestModel interestModel:interestModels) {
            create(interestModel,null);
        }
    }

    @Override
    public void deleteInterest(InterestModel interestModel) {
        delete(interestModel.getId());
    }

    @Override
    public List<InterestModel> getInterests(ProfileModel profileModel) {
        PageInput pageInput=new PageInput();
        pageInput.query.add(ProfileModel.KEY_PROFILE_ID,profileModel.getId());
        return search(pageInput).items;
    }
}
