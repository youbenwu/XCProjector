package com.outmao.xcprojector;


import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.ActivitySplashBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.util.SharepreferencesUtils;

import java.util.List;


public class SplashActivity extends AppCompatActivity {

    private boolean first=true;

    private ActivitySplashBinding binding;

    private boolean error=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!first){
                    go();
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                go();
                first=false;
            }
        }, 1500);

        registerWifiStateReceiver();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void go(){
        if(SharepreferencesUtils.getShareInstance().getString(AppConfig.NEW_ACTIVE)==null){
            goActivate();
            return;
        }
        if(!isNetwork()){
            Toast.makeText(getBaseContext(), "无网络，请先联接WIFY", Toast.LENGTH_LONG).show();
            error=true;
            return;
        }
        checkState();
    }

    //去激活设备
    private void goActivate(){
        Intent intent = new Intent(SplashActivity.this, ActivateActivity.class);
        startActivity(intent);
        //finish();
    }

    private void goMain(){
        //Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        //finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) {
            goSetting();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    private boolean isNetwork(){
        try {
            ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            return cwjManager.getActiveNetworkInfo().isAvailable();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

   //检测设备是否激活
   private void checkState() {
       HttpApiService.getInstance().account_status()
               .subscribe(new RxSubscriber<YYResponseData<AccountStatusData>>() {
                   @Override
                   public void onFail(YYResponseData<AccountStatusData> responseData) {
                       super.onFail(responseData);
                       Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                       error=true;
                   }

                   @Override
                   public void onSuccess(YYResponseData<AccountStatusData> responseData) {
                       super.onSuccess(responseData);
                       Log.d("接口返回", responseData.toString());
                       if(responseData.getData().getStatus()==1){
                           //已激活
                           goMain();
                       }else{
                           goActivate();
                       }
                       error=false;
                   }
               });
   }


    private void registerWifiStateReceiver(){
        // 创建Wifi状态变化的IntentFilter
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // 注册BroadcastReceiver
        registerReceiver(wifiStateReceiver, intentFilter);
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                    // Wifi已启用
                    if(error==true){
                        error=false;
                        go();
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    // Wifi已禁用
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    // Wifi状态未知

                    break;
            }
        }
    };

   private void goSetting(){
       try{
           String actionName = "com.htc.htcpublicsettings";
           // 包名不是Null 的时候跳转
           if(actionName != null) {
               PackageInfo pi = getPackageManager().getPackageInfo(actionName, 0);

               Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
               resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
               resolveIntent.setPackage(pi.packageName);

               List<ResolveInfo> apps = getPackageManager().queryIntentActivities(resolveIntent, 0);

               ResolveInfo ri = apps.iterator().next();
               if (ri != null ) {
                   String packageName = ri.activityInfo.packageName;
                   String className = ri.activityInfo.name;

                   Intent intent = new Intent(Intent.ACTION_MAIN);
                   intent.addCategory(Intent.CATEGORY_LAUNCHER);
                   ComponentName cn = new ComponentName(packageName, className);

                   intent.setComponent(cn);
                   startActivity(intent);
               } else {
                   // 包名是Null的时候,提示用户下载

               }


           }
       }catch(Exception e) {
           e.printStackTrace();
           // Toast.makeText(getContext(), "暂无未支持该应用", Toast.LENGTH_LONG).show();
           Log.d("OtherApplication Error: ", "otherApplication.getPackageName()");
       }
   }




}