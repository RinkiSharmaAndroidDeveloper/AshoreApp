package com.trutek.looped.msas.common.repositories.base;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.trutek.looped.msas.common.contracts.IModel;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.contracts.Pager;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.Observable;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

public abstract class BaseRepository<TEntity, TModel extends IModel> extends Observable implements IRepository<TModel> {

    private static final String TAG = BaseRepository.class.getSimpleName();

    public static String OBSERVE_KEY;
    private Handler handler;
    Context context;

    private final IModelMapper<TEntity, TModel> mapper;
    private final AbstractDao<TEntity, Long> dao;

    public BaseRepository(Context context, IModelMapper<TEntity, TModel> mapper, AbstractDao<TEntity, Long> dao, String observeKey) {
        OBSERVE_KEY = observeKey;
        handler = new Handler(Looper.getMainLooper());
        this.context = context;
        this.mapper = mapper;
        this.dao = dao;
    }


    protected abstract QueryBuilder<TEntity> query(Long id);

    protected abstract QueryBuilder<TEntity> query(PageQuery query);

    protected abstract void map(TModel model);

    protected abstract void map(TEntity entity, TModel model);

    protected abstract TEntity newEntity();

    @Override
    public void notifyObservers(final Object data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setChanged();
                BaseRepository.super.notifyObservers(data);
            }
        });
    }

    @Override
    public TModel get(Long id) {
        TEntity entity = query(id).unique();

        if (entity == null)
            return null;

        TModel model = mapper.Map(entity);
        map(model);
        return model;
    }

    @Override
    public TModel get(PageQuery query) {
        TEntity entity = query(query).unique();

        if (entity == null)
            return null;

        TModel model = mapper.Map(entity);
        map(model);
        return model;
    }

    @Override
    public Page<TModel> page(PageInput input) {
        return Pager.get(query(input.query), input, mapper);
    }

    @Override
    public TModel update(Long id, TModel tModel, String action) {
        TEntity entity = query(id).unique();
        map(entity, tModel);
        dao.update(entity);

        notifyObservers(OBSERVE_KEY);
        notifyBroadCast(action);
        return mapper.Map(entity);
    }

    @Override
    public TModel update(Long id, TModel model) {
        return update(id, model, null);
    }

    @Override
    public TModel create(TModel model) {
        return create(model, null);
    }

    @Override
    public TModel create(TModel tModel, String action) {
        Long id = tModel.getId();

        if (id != null) {
            return update(id, tModel, action);
        }

        TEntity entity = newEntity();
        map(entity, tModel);
        dao.insert(entity);

        notifyObservers(OBSERVE_KEY);
        notifyBroadCast(action);
        return mapper.Map(entity);
    }

    @Override
    public boolean any(PageQuery query) {
        return query(query).count() > 0;
    }

    @Override
    public void remove(Long id) {
        TEntity entity = query(id).unique();

        notifyObservers(OBSERVE_KEY);
        dao.delete(entity);
    }

    @Override
    public void notifyBroadCast(String intentAction) {
        if (context != null && null != intentAction) {

            Intent intent = new Intent(intentAction);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    @Override
    public void removeAll() {
        dao.deleteAll();
    }

    @Override
    public TModel getByServerId(String serverId) {
        return get(new PageQuery("serverId", serverId));
    }

    @Override
    public boolean isExist(int id) {
        return get(Long.valueOf(id)) != null;
    }

    @Override
    public boolean isExist(PageQuery query) {
        return get(query) != null;
    }
}
