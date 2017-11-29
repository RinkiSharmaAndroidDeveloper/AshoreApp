package com.trutek.looped.msas.common.contracts;

import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import de.greenrobot.dao.query.QueryBuilder;

public class Pager {

    public static <TEntity, TModel> Page<TModel> get(QueryBuilder<TEntity> queryBuilder, PageInput input, IModelMapper<TEntity, TModel> mapper) {
        Page<TModel> page = new Page();

        page.Total = queryBuilder.count();

        if (!input.noPaging) {
            queryBuilder.offset(input.pageNo * input.pageSize);
            queryBuilder.limit(input.pageSize);
        }

        for (TEntity entity : queryBuilder.list()) {
            page.items.add(mapper.Map(entity));
        }


        return page;
    }
}
