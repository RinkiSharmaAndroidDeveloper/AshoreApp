package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.apis.IMedicineApi;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.services.IMedicineService;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.services.base.BaseService;

/**
 * Created by Rinki on 12/3/2016.
 */
public class MedicineService  extends BaseService<MedicineModel> implements IMedicineService{
    private IMedicineApi<MedicineModel> remote;

   public MedicineService(IMedicineApi<MedicineModel> remote) {
        super(null);
        this.remote = remote;
    }

    @Override
    public void getAllMedicins(String id, AsyncResult<Page<MedicineModel>> result) {
        remote.page(null,id,result);
    }

    @Override
    public void deleteMedicine(MedicineModel model, AsyncNotify result) {
        remote.delete(model.getServerId(), result);
    }

    @Override
    public void createMedicine(MedicineModel model, AsyncResult<MedicineModel> result) {
        remote.create(model,model.getRecipientId(),result);
    }

    @Override
    public void updateMedicine(MedicineModel model, AsyncResult<MedicineModel> result) {
        remote.update(model.getMedID(),model,result);

    }

}


