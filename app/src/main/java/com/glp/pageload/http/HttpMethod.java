package com.glp.pageload.http;

import com.glp.pageload.bean.ArticleBean;
import com.glp.pageload.bean.HttpResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Guolipeng on 2018/4/8.
 * 网络请求方法接口
 */

public interface HttpMethod {

    @GET("data/{category}/{page_size}/{page}")
    Observable<HttpResult<ArticleBean>> getAndroidList(@Path("category") String category, @Path("page_size") int pageSize, @Path("page") int page);
}
