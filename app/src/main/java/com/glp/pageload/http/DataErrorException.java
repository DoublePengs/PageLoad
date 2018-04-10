package com.glp.pageload.http;


import com.glp.pageload.bean.HttpResult;

/**
 * Created by Guolipeng on 2018/3/27.
 */

class DataErrorException extends RuntimeException {

    private final HttpResult mResult;
    private static final String TAG = "DataErrorException";

    public <T> DataErrorException(HttpResult<T> result) {
        mResult = result;
    }

    @Override
    public String getMessage() {
        return mResult.isError() ? "Error" : "OK";
    }
}
