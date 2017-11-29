package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

/**
 * Created by Rinki on 1/19/2017.
 */
public interface ICategoryService  extends ICRUDService<CategoryModel> {
    void getAllCategory(PageInput input, AsyncResult<Page<CategoryModel>> result);
}
