package com.hbtl.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbtl.api.model.CoamPageList;

import timber.log.Timber;

/*
 * Copyright (C) 2012 Fabian Leon Ortega <http://orleonsoft.blogspot.com/,
 *  http://yelamablog.blogspot.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class LoadMoreRecyclerView extends RecyclerView {

    private static final String TAG = "LoadMoreRecyclerView";

    /**
     * Listener that will receive notifications every time the list scrolls.
     */
    private OnScrollListener mOnScrollListener;
    private LayoutInflater mInflater;

    // footer view
    private RelativeLayout mFooterView;
    private TextView mLabLoadMore;
    private ProgressBar mProgressBarLoadMore;

    // Listener to process load more items when user reaches the end of the list
    private OnLoadMoreData mOnLoadMoreData;
    // To know if the list is app_loading_view more items
    private boolean mIsLoadingMore = false;

    private boolean mCanLoadMore = true;
    private int mCurrentScrollState;

    private LoadMoreRecyclerAdapter mAdapter;

    public OnRecyclerScrollListener mOnRecyclerScrollListener;

    // load page list...
    private int mLoadPage;
    public CoamPageList mCoamPageList;
//    public int first;
//    public int before;
//    public int current;
//    public int last;
//    public int next;
//    public int total_pages;
//    public int total_items;
//    public int limit;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // footer
        //mFooterView = (RelativeLayout) mInflater.inflate(R.layout.app_load_more_footer, this, false);
        //mLabLoadMore = (TextView) mFooterView.findViewById(R.id.no_more_textView);
        //mProgressBarLoadMore = (ProgressBar) mFooterView.findViewById(R.id.load_more_progressBar);

        //addFooterView(mFooterView);

        mOnRecyclerScrollListener = new OnRecyclerScrollListener() {
            @Override
            public void onLoadMore(final int loadPage) {
                super.onLoadMore(mCoamPageList.next);
                omLoadMore(mCoamPageList.next);
                Timber.i("##############################oooo");
                // 到底部自动加载
                //getAdapter().setHasFooter(true);
                //shareInfoRecyclerListAdapter.setHasFooter(true);

                //getData("2");
            }

            @Override
            public void onLoadComplete() {
                super.onLoadComplete();
                // TODO...
            }
        };
        this.addOnScrollListener(mOnRecyclerScrollListener);
    }

    public void setOnLoadMoreData(OnLoadMoreData onLoadMoreData) {
        this.mOnLoadMoreData = onLoadMoreData;
    }

    public void setScrollPageList(CoamPageList coamPageList) {
        // 排除刷新首页...
        if (mLoadPage == 0) return;
        this.mCoamPageList = coamPageList;
        getAdapter().setCoamPageList(coamPageList);

        // 判断是否到达最后一页-显示加载更多...
        if (mLoadPage == coamPageList.getLast()) {
            getAdapter().setHasMoreData(false);
        } else {
            getAdapter().setHasMoreData(true);
        }
        getAdapter().setHasFooter(true);
    }

    //	@Override
    public void setAdapter(LoadMoreRecyclerAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;
    }

    public LoadMoreRecyclerAdapter getAdapter() {
        return this.mAdapter;
    }

    public void omLoadNew() {
        mLoadPage = 0;
        mOnLoadMoreData.onLoadData(0);
    }

    public void omLoadStart() {
        mLoadPage = 1;
        mOnLoadMoreData.onLoadData(1);
    }

    public void omLoadMore(int loadPage) {
        Timber.i("##############################oooo");
        // 到底部自动加载
        getAdapter().setHasFooter(true);
        mLoadPage = loadPage;
        mOnLoadMoreData.onLoadData(loadPage);
    }

    public void omLoadComplete() {
        mOnRecyclerScrollListener.onLoadComplete();
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     *
     * @param l
     *            The scroll listener.
     */
//	@Override
//	public void setOnScrollListener(OnScrollListener l) {
//		mOnScrollListener = l;
//	}

    /**
     * Register a callback to be invoked when this list reaches the end (last
     * item be visible)
     *
     * @param onLoadMoreListener The callback to run.
     */

//    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
//        mOnLoadMoreListener = onLoadMoreListener;
//    }

//    public void setCanLoadMore(boolean canLoadMore) {
//        mCanLoadMore = canLoadMore;
//        mLabLoadMore.setVisibility(View.INVISIBLE);
//    }

//    public void onLoadMore() {
//        Log.d(TAG, "onLoadMore");
//        if (mOnLoadMoreListener != null) {
//            mOnLoadMoreListener.onLoadMore();
//        }
//    }

    /**
     * Notify the app_loading_view more operation has finished
     */
//    public void onLoadMoreComplete() {
//        mIsLoadingMore = false;
//        mProgressBarLoadMore.setVisibility(View.GONE);
//    }

}