package com.trutek.looped.ui.base;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.trutek.looped.msas.common.Utils.bridges.LoadingBridge;

public abstract class BaseLoaderFragmentV4<TData> extends BaseV4Fragment implements LoaderManager.LoaderCallbacks<TData>, LoadingBridge {

    private Loader<TData> loader;

    protected void initDataLoader(int id) {
        getLoaderManager().initLoader(id, null, this);
    }

    protected void restartDataLoader(int id){
        getLoaderManager().restartLoader(id,null,this);
    }

    protected abstract Loader<TData> createDataLoader(int id);

    @Override
    public Loader<TData> onCreateLoader(int id, Bundle args) {
        loader = createDataLoader(id);
        return loader;
    }

    protected void onChangedData() {
        loader.onContentChanged();
    }

}
