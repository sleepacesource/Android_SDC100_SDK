package com.sleepace.sdc100sdk.demo;

import com.sleepace.sdc100sdk.demo.util.CrashHandler;
import com.sleepace.sdk.util.SdkLog;

import android.app.Application;


public class DemoApp extends Application {

    private static DemoApp instance;
    
    public StringBuffer logBuf = new StringBuffer();

    public static DemoApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler.getInstance().init(this);
        String tag = "SDC100";
        String dir = getExternalFilesDir(null) + "/log";
        SdkLog.setLogTag(tag);
        SdkLog.setLogDir(dir);
        SdkLog.setLogEnable(true);
        SdkLog.setSaveLog(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);

    }

}














