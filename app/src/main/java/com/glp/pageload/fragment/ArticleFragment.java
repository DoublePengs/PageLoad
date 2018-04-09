package com.glp.pageload.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.glp.pageload.R;
import com.glp.pageload.base.BaseFragment;
import com.glp.pageload.bean.ArticleBean;
import com.glp.pageload.bean.HttpResult;
import com.glp.pageload.http.DataSource;
import com.glp.pageload.http.HttpRepository;
import com.glp.pageload.util.AdapterWrapper;
import com.glp.pageload.util.SwipeToLoadHelper;
import com.glp.pageload.view.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;

/**
 * Created by Guolipeng on 2018/4/8.
 * 文章列表页 [注：本页面数据源来源于 “干货集中营” API  http://gank.io/api]
 */

public class ArticleFragment extends BaseFragment {

    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private static final String TITLE = "TITLE";
    private List<ArticleBean> mList;
    private int mPageSize = 10;
    private int mPage = 1;
    private SwipeToLoadHelper mLoadMoreHelper;

    public static ArticleFragment newInstance(String title) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle b = new Bundle();
        b.putString(TITLE, title);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void handleMessage(Message msg) {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void doCreateView(View view) {

    }

    @Override
    protected void initData() {
        mRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mList = new ArrayList<>();
        RecyclerView.Adapter adapter = new ArticleAdapter(mContext, mList);
        AdapterWrapper mWrapper = new AdapterWrapper(mContext, adapter, mPageSize);
        mLoadMoreHelper = new SwipeToLoadHelper(mRv, mWrapper);
        mRv.setAdapter(mWrapper);
        mRv.addItemDecoration(new RecyclerViewItemDecoration(mContext, LinearLayoutManager.HORIZONTAL));
        getAndroidList(mPageSize, mPage);
        initListener();
    }

    private void initListener() {
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新时禁用上拉加载更多
                mLoadMoreHelper.setLoadMoreEnabled(false);

                mPage = 1;
                getAndroidList(mPageSize, mPage);
            }
        });
        mLoadMoreHelper.setLoadMoreListener(new SwipeToLoadHelper.LoadMoreListener() {
            @Override
            public void onLoad() {
                // 请求更多数据时禁用SwipeRefresh功能
                mRefreshLayout.setEnabled(false);

                getAndroidList(mPageSize, ++mPage);
            }
        });
    }

    private void getAndroidList(final int pageSize, int page) {
        HttpRepository.getInstance(mContext).getAndroidArticleList(getArguments().getString(TITLE), pageSize, page, new DataSource.RequestCallback<HttpResult<ArticleBean>>() {
            @Override
            public void onDataLoaded(HttpResult<ArticleBean> result) {
                resetRefreshLayout();

                if (result.isError()) {
                    Toast.makeText(mContext, "请求出错", Toast.LENGTH_SHORT).show();
                    if (mPage != 1) {
                        mPage--;
                    }
                } else {
                    if (mPage == 1) {
                        mList.clear();
                    }
                    List<ArticleBean> newData = result.getResults();
                    mList.addAll(newData);

                    // 将请求回来的数据添加进 mList 数据源,如果不够一页内容,则禁用上拉加载更多
                    mLoadMoreHelper.setLoadMoreEnabled(mList.size() >= mPageSize);
                    // 设置加载完成,通过新加载回来的数据与pageSize的关系来判断还有没有更多内容
                    mLoadMoreHelper.setLoadMoreFinish(newData.size() < mPageSize);
                }
            }

            @Override
            public void onDataError(String msg) {
                resetRefreshLayout();
                mLoadMoreHelper.setLoadMoreFinish(false);

                if (mPage != 1) {
                    mPage--;
                }
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscription(Subscription subscription) {

            }
        });
    }

    /**
     * 重置 下拉刷新布局的状态
     */
    private void resetRefreshLayout() {
        // 如果下拉刷新处于正在刷新状态，则停止刷新动画
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        // 如果下拉刷新仍处于被禁用状态,则启用
        if (!mRefreshLayout.isEnabled()) {
            mRefreshLayout.setEnabled(true);
        }
    }


}
