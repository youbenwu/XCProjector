package com.outmao.xcprojector.config;

import android.app.Application;
import android.content.Context;
import com.outmao.xcprojector.util.SharepreferencesUtils;

public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
        SharepreferencesUtils.initSharepreferencesUtils(getApplicationContext());
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

}
