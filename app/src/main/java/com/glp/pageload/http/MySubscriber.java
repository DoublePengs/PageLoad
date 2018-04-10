package com.glp.pageload.http;

import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Guolipeng on 2017/8/8.
 * 网络请求结果的订阅类  继承自 RxJava 中的  Subscriber
 */

public abstract class MySubscriber<T> extends Subscriber<T> {

    protected abstract void onMyCompleted();

    protected abstract void onMyError(String msg);

    protected abstract void onMyNext(T t);

    @Override
    public void onCompleted() {
        onMyCompleted();
    }

    @Override
    public void onError(Throwable e) {
        String msg = "o(╯□╰)o 未知错误";
        if (e instanceof HttpException) {
            msg = "网络请求超时";
        } else if (e instanceof UnknownHostException) {
            msg = "无网络连接";
        } else if (e instanceof SocketTimeoutException) {
            msg = "网络连接超时";
        } else if (e instanceof ConnectException) {
            msg = "网络连接不上";
        } else if (e instanceof JsonSyntaxException || e instanceof MalformedJsonException) {
            msg = "数据解析出错";
        } else if (e instanceof DataErrorException) {
            msg = e.getMessage();
        } else {
            Log.e("MySubscriber", "onError --- msg:" + msg + "  Exception:" + e.toString() + e.getStackTrace()[0].getClassName() + e.getStackTrace()[0].getMethodName());
        }
        onMyError(msg);
    }

    @Override
    public void onNext(T t) {
        onMyNext(t);
    }
}
