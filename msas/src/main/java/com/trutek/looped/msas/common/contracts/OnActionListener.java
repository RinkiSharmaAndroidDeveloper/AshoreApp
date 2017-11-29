package com.trutek.looped.msas.common.contracts;

public interface OnActionListener<TModel> {
    void notify(TModel model);
}
