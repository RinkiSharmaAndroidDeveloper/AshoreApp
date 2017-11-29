package com.trutek.looped.msas.common.contracts;

import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.Date;

public interface ISyncService<TModel> {

    void push(PageInput input, Date timeStamp, String action);

    void pull(PageQuery query, Date timeStamp, AsyncResult<Page<TModel>> result);

}
