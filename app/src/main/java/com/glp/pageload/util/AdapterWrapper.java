package com.glp.pageload.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.glp.pageload.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 加载更多 Adapter 包装类
 */

public class AdapterWrapper extends RecyclerView.Adapter {

    private static final int ITEM_TYPE_LOAD = Integer.MAX_VALUE / 2;
    private RecyclerView.Adapter mAdapter;
    private boolean mShowLoadItem = true;
    private WrapperHolder mWrapperHolder;
    private final int mPageSize; // 每页加载数目
    private final Context mContext;

    public AdapterWrapper(Context context, RecyclerView.Adapter adapter, int pageSize) {
        mContext = context;
        mAdapter = adapter;
        mPageSize = pageSize;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOAD) {
            if (mWrapperHolder == null) {
                mWrapperHolder = new WrapperHolder(LayoutInflater.from(mContext).inflate(R.layout.item_load_more, parent, false));
            }
            return mWrapperHolder;
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    // 允许显示"加载更多"item, 并且position为末尾时,拦截
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mShowLoadItem && position == getItemCount() - 1) {
            // 最后一项 不需要做什么额外的事
        } else if (position < mAdapter.getItemCount()) {
            // 正常情况
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        // 这里是关键,当 item 数目大于等于 pageSize,即加载内容够一页，才显示 底部加载更多的条目
        mShowLoadItem = mAdapter.getItemCount() >= mPageSize;
        // 需要显示加载更多,则 AdapterWrapper 的 getItemCount 需要在控制真正加载数据的 Adapter 的基础上 +1
        return mShowLoadItem ? mAdapter.getItemCount() + 1 : mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        // 位置是最后一个时, AdapterWrapper进行拦截
        if (mShowLoadItem && position == getItemCount() - 1) {
            return ITEM_TYPE_LOAD;  // 注意要避免和原生adapter返回值重复
        }
        // 其他情况交给原生adapter处理
        return mAdapter.getItemViewType(position);
    }

    public void setLoadItemVisibility(boolean isShow) {
        mShowLoadItem = isShow;
        notifyDataSetChanged();
    }

    public void setLoadItemState(boolean isLoading, boolean isLoadAll) {
        if (mWrapperHolder != null) {
            if (!isLoading && isLoadAll) {
                mWrapperHolder.setLoadText(mContext.getString(R.string.load_all_text));
                mWrapperHolder.setLoadPbVisibility(false);
            } else {
                mWrapperHolder.setLoadText(isLoading ? mContext.getString(R.string.loading_text) : mContext.getString(R.string.load_more_text));
                mWrapperHolder.setLoadPbVisibility(isLoading);
            }
        }
    }

    class WrapperHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_load_tv)
        TextView mLoadTv;

        @BindView(R.id.item_load_pb)
        ProgressBar mLoadPb;

        WrapperHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setLoadText(CharSequence text) {
            mLoadTv.setText(text);
        }

        void setLoadPbVisibility(boolean show) {
            mLoadPb.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}