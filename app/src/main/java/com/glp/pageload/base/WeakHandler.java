package com.glp.pageload.base;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by Guolipeng on 2017/8/2.
 * 弱引用的 Handler
 */

public class WeakHandler extends Handler {

    private final WeakReference<IHandler> ref;

    public interface IHandler {
        void handleMessage(Message msg);
    }

    public WeakHandler(IHandler t){
        ref = new WeakReference<>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        IHandler t = ref.get();
        if (t != null) {
            t.handleMessage(msg);
        }
    }
}
