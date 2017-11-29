package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;

/**
 * Created by Rinki on 12/3/2016.
 */
public interface IMedicineService extends ICRUDService<MedicineModel> {
    void getAllMedicins(String id, AsyncResult<Page<MedicineModel>> result);
    void deleteMedicine(MedicineModel model, AsyncNotify result);
    void createMedicine(MedicineModel model,AsyncResult<MedicineModel> result);
    void updateMedicine(MedicineModel model,AsyncResult<MedicineModel> result);
}
