package com.glp.pageload.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * 上滑加载更多操作辅助类
 */

public class SwipeToLoadHelper extends RecyclerView.OnScrollListener {

    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterWrapper mAdapterWrapper;
    private LoadMoreListener mListener;
    /**
     * 是否正在加载中
     */
    private boolean mLoading = false;
    /**
     * 上拉刷新功能是否可用
     */
    private boolean mLoadMoreEnabled = true;
    /**
     * 是否已经加载完全部数据
     */
    private boolean mLoadAll = false;

    public SwipeToLoadHelper(RecyclerView recyclerView, AdapterWrapper adapterWrapper) {
        mLayoutManager = recyclerView.getLayoutManager();
        mAdapterWrapper = adapterWrapper;

        // 将OnScrollListener设置RecyclerView
        recyclerView.addOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (mLoadMoreEnabled && SCROLL_STATE_IDLE == newState && !mLoading) {

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mLayoutManager;
            int lastCompletePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            // only when the complete visible item is second last
            if (lastCompletePosition == mLayoutManager.getItemCount() - 2) {
                int firstCompletePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                View child = linearLayoutManager.findViewByPosition(lastCompletePosition);
                if (child == null)
                    return;
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();

                int deltaY = recyclerView.getBottom() - recyclerView.getPaddingBottom() - child.getBottom() - lp.bottomMargin;
                if (deltaY > 0 && firstCompletePosition != 0) {
                    recyclerView.smoothScrollBy(0, -deltaY);
                }
            } else if (!mLoadAll && mLayoutManager.getItemCount() > 0 && lastCompletePosition == mLayoutManager.getItemCount() - 1) {
                // 最后一项完全显示, 触发操作, 执行加载更多操作 禁用回弹判断
                mLoading = true;
                mAdapterWrapper.setLoadItemState(true, false);
                if (mListener != null) {
                    mListener.onLoad();
                }
            }

        }
    }

    /**
     * 设置上拉加载更多功能是否可用
     *
     * @param enabled true 表示上拉加载更多可用
     */
    public void setLoadMoreEnabled(boolean enabled) {
        mLoadMoreEnabled = enabled;
        // “上拉加载更多”可用时,显示对应布局,否则隐藏
        mAdapterWrapper.setLoadItemVisibility(enabled);
        if (enabled) {
            setLoadMoreFinish(false);
        }
    }

    /**
     * 设置LoadMore Item为加载完成状态, 上拉加载更多完成时调用
     *
     * @param isLoadAll 加载完成时时候已经加载完全部的数据
     */
    public void setLoadMoreFinish(boolean isLoadAll) {
        mLoading = false;
        mLoadAll = isLoadAll;
        mAdapterWrapper.setLoadItemState(false, isLoadAll);
    }

    /**
     * 上拉操作触发时调用的接口
     *
     * @param loadMoreListener 上拉加载更多的回调监听
     */
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        mListener = loadMoreListener;
    }

    public interface LoadMoreListener {
        void onLoad();
    }
}
