package com.hbtl.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbtl.api.model.CoamPageList;
import com.hbtl.ekt.R;

import java.util.List;

/**
 * Created by 亚飞 on 2015-11-18.
 * https://github.com/kyleduo/LoadMoreRecyclerViewAdapter/blob/master/LoadMoreRecyclerViewAdapter.java
 */
public class LoadMoreRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<?> mList;

    public LoadMoreRecyclerAdapter(Context context, List<?> vList) {
        this.mList = vList;
    }

    // 更新最新分页数据...
    public CoamPageList mCoamPageList;

    public void setCoamPageList(CoamPageList coamPageList) {
        this.mCoamPageList = coamPageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_load_more_recycler_adapter, parent, false);
            //Timber.i("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
            return new LoadFooterViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadFooterViewHolder) {
            TextView textView = ((LoadFooterViewHolder) holder).pageListLoading_TV;
            MetaBallView metaBallView = ((LoadFooterViewHolder) holder).pageListLoading_MBV;
            //没有更多数据
            if (hasMoreData) {
//                ((FooterViewHolder) vh).mProgressView.setVisibility(View.VISIBLE);
//                ((FooterViewHolder) vh).mProgressView.startProgress();
                //((FooterViewHolder) holder).mProgressView.setIndeterminate(true);
                //textView.setVisibility(View.GONE);
                metaBallView.setVisibility(View.VISIBLE);
                //textView.setText(R.string.app_loading_more);
                textView.setText("[" + mCoamPageList.next + "/" + mCoamPageList.total_pages + "],加载中...");
                //Timber.i("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS-加载中");
            } else {
//                ((FooterViewHolder) vh).mProgressView.stopProgress();
//                ((FooterViewHolder) vh).mProgressView.setVisibility(View.GONE);
                //((FooterViewHolder) holder).mProgressView.st;
                //textView.setVisibility(View.VISIBLE);
                metaBallView.setVisibility(View.GONE);
                textView.setText("已加载[" + mCoamPageList.total_items + "/" + mCoamPageList.total_pages + "],没有更多数据了...");
                //Timber.i("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS-没有更多数据了");
            }
        }
    }

//    @Override
//    public int getItemCount() {
//        return 0;
//    }

    @Override
    public int getItemCount() {
        return mList.size() + (hasFooter ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {

        if (position == getBasicItemCount() && hasFooter) {
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    public static class LoadFooterViewHolder extends RecyclerView.ViewHolder {
        //        public final MaterialProgressBarSupport mProgressView;
        public final TextView pageListLoading_TV;
        public final MetaBallView pageListLoading_MBV;

        public LoadFooterViewHolder(View view) {
            super(view);
//            mProgressView = (MaterialProgressBarSupport) view.findViewById(R.id.progress_view);
            pageListLoading_TV = (TextView) view.findViewById(R.id.pageListLoading_TV);
            pageListLoading_MBV = (MetaBallView) view.findViewById(R.id.pageListLoading_MBV);
            pageListLoading_MBV.setPaintMode(0);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + pageListLoading_TV.getText();
        }
    }


    private static final int TYPE_HEADER = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;
    private static final int TYPE_ADAPTEE_OFFSET = 2;
    private List<String> mValues;
    private boolean hasFooter;

    private boolean hasMoreData;

    public int getBasicItemCount() {
        return mList.size();
    }

    public String getValueAt(int position) {
        return mValues.get(position);
    }

    public boolean hasFooter() {
        return hasFooter;
    }

    public void setHasFooter(boolean hasFooter) {
        if (this.hasFooter != hasFooter) {
            this.hasFooter = hasFooter;
            notifyDataSetChanged();
        }
    }

    public boolean hasMoreData() {
        return hasMoreData;
    }

    public void setHasMoreData(boolean isMoreData) {
        if (this.hasMoreData != isMoreData) {
            this.hasMoreData = isMoreData;
            notifyDataSetChanged();
        }
    }

//    public void setHasMoreDataAndFooter(boolean hasMoreData, boolean hasFooter) {
//        if (this.hasMoreData != hasMoreData || this.hasFooter != hasFooter) {
//            this.hasMoreData = hasMoreData;
//            this.hasFooter = hasFooter;
//            notifyDataSetChanged();
//        }
//    }
}
