package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.apis.ICategoryApi;
import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.services.ICategoryService;
import com.trutek.looped.data.impl.entities.Category;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

/**
 * Created by Rinki on 1/20/2017.
 */
public class CategoryService extends BaseService<CategoryModel> implements ICategoryService {

    private ICategoryApi<CategoryModel> remote;

    public CategoryService(ICategoryApi<CategoryModel> remote) {
        super(null);
        this.remote = remote;
    }


    @Override
    public void getAllCategory(PageInput input, AsyncResult<Page<CategoryModel>> result) {
        remote.page(input,result);
    }
}
