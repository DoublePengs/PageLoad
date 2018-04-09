package com.glp.pageload.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Guolipeng on 2017/8/4.
 * Fragment的基类
 */

public abstract class BaseFragment extends Fragment implements WeakHandler.IHandler {

    protected WeakHandler mHandler;
    protected View mView;
    /**
     * 标记位 标记是否已经初始化完成
     */
    protected boolean isPrepared;
    /**
     * 是否可见
     */
    protected boolean isVisible;
    public Unbinder mBinder;
    public Context mContext;
    public CompositeSubscription mSubscriptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHandler = new WeakHandler(this);
        mView = inflater.inflate(getContentLayout(), container, false);
        mBinder = ButterKnife.bind(this, mView);
        doCreateView(mView);
        initData();
        isPrepared = true;
        onVisible();
        return mView;
    }

    protected abstract int getContentLayout();

    /**
     * Viewpager+Fragment
     * 这种模式下，是通过方法setUserVisibleHint进行控制fragment是否可见
     *
     * @param isVisibleToUser 是否用户可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * FragmentTransaction.show(fragment)  hide(fragment)
     * 这种情况调用的方法是onHiddenChanged。
     *
     * @param hidden visible状态
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        if (isVisible && isPrepared) {
            onLazyLoad();
        }
    }

    protected void onInvisible() {

    }

    protected abstract void onLazyLoad();

    protected abstract void doCreateView(View view);

    protected abstract void initData();

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    /**
     * 创建一个 CompositeSubscription 对象来进行管理异步处理与Activity生命周期
     */
    protected void add(Subscription subscription) {
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeSubscription();
        }
        this.mSubscriptions.add(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mSubscriptions != null) {
            this.mSubscriptions.unsubscribe();
        }
    }
}
