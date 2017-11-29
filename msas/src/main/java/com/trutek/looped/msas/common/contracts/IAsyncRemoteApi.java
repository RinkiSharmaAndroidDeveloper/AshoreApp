package com.trutek.looped.msas.common.contracts;

import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

public interface IAsyncRemoteApi<TModel extends IModel> {

    IAsyncResponse<TModel> get(final String id);

    void get(final String id, AsyncResult<TModel> result);

    void get(final String id, String action, AsyncResult<TModel> result);

    void get(PageInput input, String action, AsyncResult<TModel> result);

    IAsyncResponse<TModel> update(final TModel model);

    void update(final TModel model, AsyncResult<TModel> result);

    void update(String action, AsyncResult<TModel> result);

    void update(String action, final TModel model, AsyncResult<TModel> result);
    //void update(String action, AsyncResult<TModel> result);
    IAsyncResponse<Page<TModel>> page(final PageInput input);

    void page(final PageInput input, AsyncResult<Page<TModel>> result);

    void page(final PageInput input, String action, AsyncResult<Page<TModel>> result);

    IAsyncResponse<TModel> create(final TModel model);

    void create(final TModel model, AsyncResult<TModel> result);

    void create(TModel model, String action, AsyncResult<TModel> result);

    void delete(String id, AsyncNotify result);

}
