package com.trutek.looped.msas.common.contracts;

import android.content.Context;

import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.Observer;

public interface IRepository<TEntity> {
    TEntity get(Long id);

    TEntity get(PageQuery query);

    Page<TEntity> page(PageInput input);

    TEntity update(Long id, TEntity entity,String intentAction);

    TEntity update(Long id, TEntity entity);

    TEntity create(TEntity entity,String intentAction);

    TEntity create(TEntity entity);

    boolean any(PageQuery query);

    void remove(Long id);

    void removeAll();

    TEntity getByServerId(String serverId);

    boolean isExist(int id);

    boolean isExist(PageQuery query);

    void addObservers(Observer observer);

    void deleteObservers(Observer observer);

    void notifyBroadCast(String intentAction);


}

