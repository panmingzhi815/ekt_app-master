package com.hbtl.view;

/**
 * Interface definition for a callback to be invoked when list reaches the
 * last item (the user load more items in the list)
 */
public interface OnLoadMoreListener {

    void onLoadNew();

    void onLoadStart();

    /**
     * Called when the list reaches the last item (the last item is visible
     * to the user)
     */
    void onLoadMore(final int loadPage);

    void onLoadComplete();
}