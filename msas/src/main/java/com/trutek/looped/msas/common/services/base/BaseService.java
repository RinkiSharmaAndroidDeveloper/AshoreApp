package com.trutek.looped.msas.common.services.base;

import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncResponse;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.Date;
import java.util.Observer;


public  class BaseService<TModel extends ISynchronizedModel> implements ICRUDService<TModel> {
    protected final IRepository<TModel> _local;

    public BaseService(IRepository<TModel> local) {
        _local = local;
    }


    @Override
    public TModel get(Long id) {
        return _local.get(id);
    }

    @Override
    public TModel getByServerId(String id) {
        return _local.getByServerId(id);
    }

    @Override
    public TModel get(PageQuery query) {
        return _local.get(query);
    }

    @Override
    public Page<TModel> search(PageInput input) {
        return _local.page(input);
    }

    @Override
    public TModel update(TModel tModel,String action) {
        return _local.update(tModel.getId(), tModel,action);
    }

    @Override
    public TModel update(TModel model) {
        return _local.update(model.getId(),model);
    }

    @Override
    public TModel create(TModel entity,String action) {
        return _local.create(entity,action);
    }

    @Override
    public TModel create(TModel entity) {
        return _local.create(entity);
    }

    @Override
    public void delete(Long id) {
        _local.remove(id);
    }

    @Override
    public void deleteAll() {
        _local.removeAll();
    }

    @Override
    public boolean isExist(int id) {
        return _local.get(Long.valueOf(id)) != null;
    }

    @Override
    public void addObservers(Observer observer) {
        _local.addObservers(observer);
    }

    @Override
    public void deleteObservers(Observer observer) {
        _local.deleteObservers(observer);
    }

    @Override
    public void push(PageInput input, Date timeStamp, String action) {

    }

    @Override
    public void pull(PageQuery query, Date timeStamp, AsyncResult<Page<TModel>> result) {

    }


    private class AsyncResponse implements IAsyncResponse<Integer> {

        Boolean _isBusy;
        String _error;
        Integer _result;

        @Override
        public boolean isBusy() {
            return _isBusy;
        }

        @Override
        public boolean hasError() {
            return _error != null;
        }

        @Override
        public String getError() {
            return _error;
        }

        @Override
        public void waitForResult() {
            while (_isBusy) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public Integer getResult() {
            return _result;
        }
    }


}
