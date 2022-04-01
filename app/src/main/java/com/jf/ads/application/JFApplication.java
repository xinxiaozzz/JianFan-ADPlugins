package com.jf.ads.application;

import android.app.Application;
import android.content.Context;

public class JFApplication extends Application {

    private static JFApplication instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getInstance() {
        return instance;
    }
}
