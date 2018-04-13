package com.hbtl.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import timber.log.Timber;

/**
 * @author Jack Tony
 * @brief recyle view 滚动监听器
 * @date 2015/4/6
 * 简化滑动到底部监听器
 * 通过重写OnScrollListener来监听RecyclerView是否滑动到底部
 * http://www.cnblogs.com/tianzhijiexian/p/4397552.html
 */
public class OnRecyclerScrollListener extends RecyclerView.OnScrollListener implements OnLoadMoreListener {

    private String TAG = getClass().getSimpleName();

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    /**
     * layoutManager的类型(枚举)
     */
    protected LAYOUT_MANAGER_TYPE layoutManagerType;

    /**
     * 最后一个的位置
     */
    private int[] lastPositions;

    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;
/*    *//**
     * 是否正在加载
     *//*
    private boolean isLoadingMore = false;*/

    /**
     * 当前滑动的状态
     */
    private int currentScrollState = 0;
    boolean isPullUp = false;
    boolean isRefreshing = false;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        Log.i(TAG, "dy = " + dy);
        isPullUp = dy > 0;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //  int lastVisibleItemPosition = -1;
        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                break;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);


        // 获取适配器中设置的数据加载情况,断定数据是否已经加载完毕,不用重新发起数据加载的方法调用
        boolean hasMD = false;
        LoadMoreRecyclerAdapter loadMoreRecyclerAdapter = ((LoadMoreRecyclerView) recyclerView).getAdapter();
        if (loadMoreRecyclerAdapter != null) hasMD = loadMoreRecyclerAdapter.hasMoreData();
//            if(isPullUp && !isRefreshing && hasMD) {
//                int lastPos = ((LinearLayoutManager) ((LoadMoreRecyclerView) recyclerView).getLayoutManager()).findLastVisibleItemPosition();
//                if (lastPos > recyclerView.getLayoutManager().getChildCount() - 2) {//最后一个位置的时候加载更多
//                    //Log.d(TAG, "is app_loading_view more");
//                    onLoadMore();
//                    Timber.i("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS-:onLoadMore");
//                }
//            }

        currentScrollState = newState;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();

        Timber.i("SSSSSSSSSSSSS-:onScrollStateChanged|hasMD:" + hasMD + "|isRefreshing:" + isRefreshing + "|isPullUp:" + isPullUp + "|newState:" + newState + "|visibleItemCount:" + visibleItemCount + "|lastVisibleItemPosition:" + lastVisibleItemPosition + "|totalItemCount:" + totalItemCount);

        if (isPullUp && !isRefreshing && hasMD && (visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
            //Log.d(TAG, "is app_loading_view more");
            onLoadMore(10000);
            Timber.i("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS-:onLoadMore");
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @Override
    // 开始最新
    public void onLoadNew() {
        isRefreshing = true;
        Log.d(TAG, "is onLoadNew");
    }

    @Override
    // 开始加载时调用的方法
    public void onLoadStart() {
        isRefreshing = true;
        Log.d(TAG, "is onLoadStart");
    }

    @Override
    // 开始更多时调用的方法
    public void onLoadMore(final int loadPage) {
        isRefreshing = true;
        Log.d(TAG, "is onLoadMore");
    }

    @Override
    // 加载完成后调用的方法
    public void onLoadComplete() {
        isRefreshing = false;
        Log.d(TAG, "is onLoadComplete");
    }
}