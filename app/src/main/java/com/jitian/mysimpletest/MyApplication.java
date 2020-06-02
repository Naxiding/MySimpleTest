package com.jitian.mysimpletest;

import android.app.Application;

/**
 * @author YangDing
 * @date 2020/5/14
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
