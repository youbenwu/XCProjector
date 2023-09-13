package com.outmao.xcprojector;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.ActivitySplashBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.util.SharepreferencesUtils;


public class SplashActivity extends AppCompatActivity {

    private boolean first=true;

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!first){
                    checkState();
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkState();
                first=false;
            }
        }, 100);


    }


    @Override
    protected void onRestart() {
        super.onRestart();
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
        if(!isNetwork()){
            if(SharepreferencesUtils.getShareInstance().getString(AppConfig.NEW_ACTIVE)!=null){
                goMain();
            }else{
                goActivate();
            }
            return;
        }
       HttpApiService.getInstance().account_status()
               .subscribe(new RxSubscriber<YYResponseData<AccountStatusData>>() {
                   @Override
                   public void onFail(YYResponseData<AccountStatusData> responseData) {
                       super.onFail(responseData);
                       Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                       if(SharepreferencesUtils.getShareInstance().getString(AppConfig.NEW_ACTIVE)!=null){
                           goMain();
                       }else{
                           goActivate();
                       }
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
                   }
               });
   }



}