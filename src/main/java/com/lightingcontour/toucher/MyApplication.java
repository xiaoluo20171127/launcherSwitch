package com.lightingcontour.toucher;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class MyApplication extends Application {
    public static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static final HandlerThread mThread;
    public static final Handler handler;

    static {
        mThread = new HandlerThread("Application-Thread");
        mThread.start();
        handler = new Handler(mThread.getLooper());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
