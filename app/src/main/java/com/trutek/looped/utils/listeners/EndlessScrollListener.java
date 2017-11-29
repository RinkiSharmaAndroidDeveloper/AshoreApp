package com.trutek.looped.utils.listeners;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;

    private int currentPage = 1;
    private boolean isLoading;

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    public abstract void onLoadMore(int current_page);

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount = linearLayoutManager.getItemCount();
        int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

        int visibleThreshold = 5;
        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

            currentPage ++;
            onLoadMore(currentPage);
            isLoading = true;
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void reset() {
        currentPage = 1;
        isLoading = true;
    }
}
