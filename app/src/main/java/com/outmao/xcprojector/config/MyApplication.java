package com.outmao.xcprojector.config;

import android.app.Application;
import android.content.Context;

import com.outmao.xcprojector.util.BaiduLocationManager;
import com.outmao.xcprojector.util.SharepreferencesUtils;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

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

        initGSYPlayer();
    }

    /**
     * GSYVideo 初始化
     */
    private void initGSYPlayer() {
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);//EXO模式
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

}
