package com.trutek.looped.ui.base;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

public abstract class BaseLoaderFragment<TData> extends BaseFragment implements LoaderManager.LoaderCallbacks<TData> {

    private Loader<TData> loader;

    protected void initDataLoader(int id) {
        getLoaderManager().initLoader(id, null, this);
    }

    protected abstract Loader<TData> createDataLoader(int id);

    protected void onChangedData() {
        loader.onContentChanged();
    }

    @Override
    public Loader<TData> onCreateLoader(int id, Bundle args) {
        loader = createDataLoader(id);
        return loader;
    }

}
