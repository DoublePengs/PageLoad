package com.glp.pageload.bean;

import java.util.List;

/**
 * Created by Guolipeng on 2018/4/8.
 */

public class HttpResult<T> {

    private boolean error;
    private List<T> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
