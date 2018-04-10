package com.glp.pageload.http;

import android.content.Context;

import com.glp.pageload.BuildConfig;
import com.glp.pageload.bean.ArticleBean;
import com.glp.pageload.bean.HttpResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Guolipeng on 2018/4/8.
 * 网络请求仓库类
 */

public class HttpRepository implements DataSource {

    private volatile static HttpRepository INSTANCE;
    private Context mContext;
    private HttpMethod mHttpMethod;

    private HttpRepository(Context context) {
        initHttpMethod(context, BuildConfig.BASE_URL);
    }

    public static HttpRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HttpRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    private void initHttpMethod(Context context, String baseUrl) {
        mContext = context.getApplicationContext();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(5, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true) // 设置出现错误进行重新连接。
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClientBuilder)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        mHttpMethod = retrofit.create(HttpMethod.class);
    }


    @Override
    public void getArticleList(String category, int pageSize, int page, final RequestCallback callback) {
        Subscription subscription = mHttpMethod.getArticleList(category, pageSize, page)
                .compose(this.<HttpResult<List<ArticleBean>>>applySchedulers())
                .map(new HttpResultFunc<List<ArticleBean>>())
                .subscribe(new MySubscriber<List<ArticleBean>>() {
                    @Override
                    protected void onMyCompleted() {
                    }

                    @Override
                    protected void onMyError(String msg) {
                        callback.onDataError(msg);
                    }

                    @Override
                    protected void onMyNext(List<ArticleBean> articleList) {
                        callback.onDataLoaded(articleList);
                    }
                });
        callback.onSubscription(subscription);
    }


    // #########################################################
    <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }

    private final Observable.Transformer schedulersTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {
        @Override
        public T call(HttpResult<T> result) {
            if (!result.isError()) {
                return result.getResults();
            } else {
                throw new DataErrorException(result);
            }
        }
    }
}
