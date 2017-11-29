package com.trutek.looped.msas.common.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IAsyncResponse;
import com.trutek.looped.msas.common.contracts.IModel;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.DataModel;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.net.NetworkDetector;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncRemoteApi<TModel extends IModel> implements IAsyncRemoteApi<TModel> {

    private final BlockingQueue<Runnable> _threadQueue;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    protected ThreadPoolExecutor threadPool;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    protected static final String ERROR_NOT_CONNECTED = "Internet connection not available";
    protected static final String ERROR_UNKNOWN = "Unknown error";
    protected final RemoteRepository<TModel> _remoteRepository;
    protected final CachedRepository<TModel> _cachedRepository;
    protected final NetworkDetector _networkDetector;

    protected Gson _gson;
    protected Type _dataType;
    protected PreferenceHelper _helper;

    public AsyncRemoteApi(Context context,
                          String key,
                          Type modelType,
                          Type pageType,
                          Type dataType,
                          SQLiteDatabase database) {
        _threadQueue = new LinkedBlockingQueue<>();
        _networkDetector = new NetworkDetector(context);
        _remoteRepository = new RemoteRepository<>(key, pageType, dataType);
        _cachedRepository = new CachedRepository<>(key, modelType, pageType, database);
        _helper = PreferenceHelper.getPrefsHelper();

        _dataType = dataType;

        _gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Date.class, new DateHelper())
                .registerTypeHierarchyAdapter(Date.class, new DateHelper())
                .create();

        initThreads();
    }

    private void initThreads() {
        threadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, _threadQueue);
        threadPool.allowCoreThreadTimeOut(true);
    }


    @Override
    public IAsyncResponse<TModel> get(final String id) {
        final ModelResponse response = new ModelResponse();
        response._isBusy = true;

        response._model = _cachedRepository.get(id);

        if (!_networkDetector.isNetworkAvailable()) {
            response._error = ERROR_NOT_CONNECTED;
            return response;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    response._model = _remoteRepository.get(id);
                    _cachedRepository.update(id, response._model);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response._error = ERROR_UNKNOWN;
                } finally {
                    response._isBusy = false;
                }
            }
        });

        return response;
    }

    @Override
    public void get(String id, AsyncResult<TModel> result) {
        get(id, null, result);
    }

    @Override
    public void get(final String id, final String action, final AsyncResult<TModel> result) {
        if (!_networkDetector.isNetworkAvailable()) {
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TModel model = _remoteRepository.get(id, action);
                    result.success(model);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

    @Override
    public void get(final PageInput input, final String action, final AsyncResult<TModel> result) {

        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TModel model = _remoteRepository.get(input, action);
                    result.success(model);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        })).start();
    }

    @Override
    public IAsyncResponse<TModel> update(final TModel model) {
        final ModelResponse response = new ModelResponse();
        response._isBusy = true;

        if (!_networkDetector.isNetworkAvailable()) {
            response._error = ERROR_NOT_CONNECTED;
            return response;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    response._model = _remoteRepository.update(model);
//                    _cachedRepository.update(model.getId(), response._model);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response._error = ERROR_UNKNOWN;
                } finally {
                    response._isBusy = false;
                }
            }
        });

        return response;
    }

    @Override
    public void update(final TModel model, final AsyncResult<TModel> result) {
        update(null, model, result);
    }

    @Override
    public void update(String action, AsyncResult<TModel> result) {
        update(action, null, result);
    }

    @Override
    public void update(final String action, final TModel model, final AsyncResult<TModel> result) {
        if (!_networkDetector.isNetworkAvailable()) {
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TModel remoteModel = _remoteRepository.update(action, model);
//                    _cachedRepository.update(model.getId(), remoteModel);
                    result.success(remoteModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

    @Override
    public IAsyncResponse<Page<TModel>> page(final PageInput input) {
        final PageResponse response = new PageResponse();
        response._isBusy = true;

        response._page = _cachedRepository.page(input);

        if (!_networkDetector.isNetworkAvailable()) {
            response._error = ERROR_NOT_CONNECTED;
            return response;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    response._page = _remoteRepository.page(input);
                    _cachedRepository.update(input, response._page);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response._error = ERROR_UNKNOWN;
                } finally {
                    response._isBusy = false;
                }
            }
        });

        return response;
    }

    @Override
    public void page(PageInput input, AsyncResult<Page<TModel>> result) {
        page(input, null, result);
    }

    @Override
    public void page(final PageInput input, final String action, final AsyncResult<Page<TModel>> result) {
        if (!_networkDetector.isNetworkAvailable()) {
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Page<TModel> realPage = _remoteRepository.page(input,action);
                    result.success(realPage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ex.getMessage());
                }
            }
        });
    }

    @Override
    public IAsyncResponse<TModel> create(final TModel model) {
        final ModelResponse response = new ModelResponse();
        response._isBusy = true;

        if (!_networkDetector.isNetworkAvailable()) {
            response._error = ERROR_NOT_CONNECTED;
            return response;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    response._model = _remoteRepository.create(model);
//                    _cachedRepository.update(model.getId(), response._model);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response._error = ERROR_UNKNOWN;
                } finally {
                    response._isBusy = false;
                }
            }
        });

        return response;
    }

    @Override
    public void create(final TModel model, final AsyncResult<TModel> result) {
        if (!_networkDetector.isNetworkAvailable()) {
            // TODO - queue this and try with regular sync
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TModel remoteModel = _remoteRepository.create(model);
                    result.success(remoteModel);
                }catch (RemoteException rex){
                    rex.printStackTrace();
                    result.error(rex.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

    @Override
    public void create(final TModel model, final String action, final AsyncResult<TModel> result) {
        if (!_networkDetector.isNetworkAvailable()) {
            // TODO - queue this and try with regular sync
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TModel remoteModel = _remoteRepository.create(action,model);
                    result.success(remoteModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result.error(ERROR_UNKNOWN);
                }
            }
        });
    }

    @Override
    public void delete(final String id, final AsyncNotify result) {

        if (!_networkDetector.isNetworkAvailable()) {
            // TODO - queue this and try with regular sync
            result.error(ERROR_NOT_CONNECTED);
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String responseJson = _remoteRepository.delete(id);
                    DataModel<TModel> responseData = _gson.fromJson(responseJson, _dataType);
                    //_cachedRepository.remove(id, remoteModel);
                    if (responseData.isSuccess)
                        result.success();
                    else
                        result.error(responseData.message);
                } catch (Exception ex) {
                    ex.printStackTrace();
//                    if(Config.isDevelopEnv){
//                        result.error(ex.getMessage());
//                    } else {
                        result.error(ERROR_UNKNOWN);
//                    }
                }
            }
        });
    }

    public class ModelResponse implements IAsyncResponse<TModel> {
        boolean _isBusy;
        String _error;
        TModel _model;

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
            int i = 0;
            while (_isBusy) {
                i++;
                System.out.println(i);
            }
        }

        @Override
        public TModel getResult() {
            return _model;
        }
    }

    private class PageResponse implements IAsyncResponse<Page<TModel>> {
        boolean _isBusy;
        String _error;
        Page<TModel> _page;

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
            }
        }

        @Override
        public Page<TModel> getResult() {
            return _page;
        }
    }
}
