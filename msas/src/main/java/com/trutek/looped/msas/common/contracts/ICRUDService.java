package com.trutek.looped.msas.common.contracts;


import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.Observer;

public interface ICRUDService<TModel> extends ISyncService<TModel>{

    TModel get(Long id);

    TModel getByServerId(String id);

    TModel get(PageQuery query);

    Page<TModel> search(PageInput input);

    TModel update(TModel model,String action);

    TModel update(TModel model);

    TModel create(TModel entity,String action);

    TModel create(TModel entity);

    void delete(Long id);

    void deleteAll();

    boolean isExist(int id);

    void addObservers(Observer observer);

    void deleteObservers(Observer observer);

}
