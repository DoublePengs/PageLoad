package com.glp.pageload.http;

import java.util.Map;

import rx.Subscription;

/**
 * Created by Guolipeng on 2018/4/8.
 * 数据源接口
 */

public interface DataSource {

    interface RequestCallback<T> {

        void onDataLoaded(T t);

        void onDataError(String msg);

        void onSubscription(Subscription subscription);
    }

    void getAndroidArticleList(String category, int pageSize, int page, RequestCallback callback);

    void getWebArticleList(String category, int pageSize, int page, RequestCallback callback);

    void getIOSArticleList(String category, int pageSize, int page, RequestCallback callback);
}
