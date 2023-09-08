package com.outmao.xcprojector.config;

import android.app.Application;
import android.content.Context;

import com.outmao.xcprojector.util.BaiduLocationManager;
import com.outmao.xcprojector.util.SharepreferencesUtils;

public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
        SharepreferencesUtils.initSharepreferencesUtils(getApplicationContext());
        try{
            BaiduLocationManager.manager.initLocationOption(context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

}
