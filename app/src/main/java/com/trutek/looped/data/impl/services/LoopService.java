package com.trutek.looped.data.impl.services;


import com.trutek.looped.data.contracts.models.LoopModel;
import com.trutek.looped.data.contracts.services.ILoopService;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.services.base.BaseService;

public class LoopService extends BaseService<LoopModel> implements ILoopService{

    public LoopService(IRepository<LoopModel> local) {
        super(local);
    }
}
