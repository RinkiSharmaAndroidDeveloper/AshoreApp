package com.trutek.looped.msas.common.contracts;

public interface IModelMapper<TEntity, TModel> {

    TModel Map(TEntity entity);
}
