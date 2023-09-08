package com.outmao.xcprojector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.config.MyApplication;
import com.outmao.xcprojector.databinding.ActivityActivateBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.util.BaiduLocationManager;
import com.outmao.xcprojector.util.DeviceUtils;
import com.outmao.xcprojector.util.SharepreferencesUtils;

import java.net.InetAddress;

public class ActivateActivity extends AppCompatActivity {

    //设备激活状态KEY
    private static final String ACTIVE_STATUS_KEY="ACTIVE_STATUS";

    private AppBarConfiguration appBarConfiguration;
    private ActivityActivateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityActivateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.activityForm.deviceId.setText(DeviceUtils.getUUID());

        registerWifiStateReceiver();
        checkWify();

        binding.activityForm.tvWify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickWify();
            }
        });

        binding.activityForm.btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doActivite();
            }
        });


    }


    private void checkWify(){
        String wify=getWifiId();
        wify=wify==null?"":wify;
        binding.activityForm.etWifi.setText(wify);
    }

    private void doActivite(){

        String wifiId=getWifiId();
        if(wifiId==null){
            Toast.makeText(this, "请先连接WIFI", Toast.LENGTH_LONG).show();
            return ;
        }

        String device_id=binding.activityForm.deviceId.getText().toString();
        String room_id=binding.activityForm.etRoomId.getText().toString();
        String location=binding.activityForm.etLoaction.getText().toString();
        String name=binding.activityForm.etName.getText().toString();

        if(location.trim().length()==0){
            Toast.makeText(this, "请输入酒店位置", Toast.LENGTH_LONG).show();
            return ;
        }

        if(location.trim().length()<4){
            Toast.makeText(this, "酒店名称太短了", Toast.LENGTH_LONG).show();
            return ;
        }

        if(name.trim().length()==0){
            Toast.makeText(this, "输入维护人姓名", Toast.LENGTH_LONG).show();
            return ;
        }

        String province="广东省";
        String city="广州市";
        String area="天河区";
        String longitude="1";
        String latitude="1";

        if(BaiduLocationManager.manager.location!=null){
            province=BaiduLocationManager.manager.location.getProvince();
            city=BaiduLocationManager.manager.location.getCity();
            area=BaiduLocationManager.manager.location.getDistrict();
            longitude=BaiduLocationManager.manager.location.getLongitude()+"";
            latitude=BaiduLocationManager.manager.location.getLatitude()+"";
        }


        HttpApiService.getInstance().account_active(location,room_id,name,province,city,area,longitude,latitude,DeviceUtils.getUUID())
                .subscribe(new RxSubscriber<YYResponseData<Object>>() {
                    @Override
                    public void onFail(YYResponseData<Object> responseData) {
                        super.onFail(responseData);
                        Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ActivateActivity.this, ActivateFailActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onSuccess(YYResponseData<Object> responseData) {
                        super.onSuccess(responseData);
                        if(responseData.isSuccess()){
                            //成功
                            setActived();
                            Intent intent = new Intent(ActivateActivity.this, ActivateSuccessActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActivateActivity.this, ActivateFailActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }


                });

    }

    //设备激活
    private void setActived(){
        SharepreferencesUtils.getShareInstance().putString(ACTIVE_STATUS_KEY,"1");
    }

    private void onClickWify(){
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    private String getWifiId(){
        WifiManager wifiMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        return wifiId;
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
                    checkWify();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    // Wifi已禁用
                    checkWify();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    // Wifi状态未知
                    checkWify();
                    break;
            }
        }
    };

}