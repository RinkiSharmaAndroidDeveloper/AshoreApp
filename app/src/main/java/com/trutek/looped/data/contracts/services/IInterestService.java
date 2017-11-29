package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msas on 9/28/2016.
 */
public interface IInterestService extends ICRUDService<InterestModel> {

    void getAll(PageInput input, AsyncResult<Page<InterestModel>> result);
    void getAll(String action, AsyncResult<Page<InterestModel>> result);
    void getAllInterest(String id, AsyncResult<Page<InterestModel>> result);

    InterestModel saveInterest(InterestModel interestModel);

    void   saveInterests(List<InterestModel> interestModels);

    void  deleteInterest(InterestModel interestModel);

    List<InterestModel> getInterests(ProfileModel profileModel);


}
