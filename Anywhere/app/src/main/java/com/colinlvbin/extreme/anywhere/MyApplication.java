package com.colinlvbin.extreme.anywhere;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;

/**
 * Created by Colin on 2016/6/7.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this);
    }
}
