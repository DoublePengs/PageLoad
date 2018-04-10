package com.glp.pageload.bean;


/**
 * Created by Guolipeng on 2018/4/8.
 */

public class HttpResult<T> {

    private boolean error;
    private T results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}
