package com.outmao.xcprojector;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.ActivitySplashBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.util.SharepreferencesUtils;



public class SplashActivity extends AppCompatActivity {


    //设备是否激活
    private boolean isActived(){
        return SharepreferencesUtils.getShareInstance().getString(AppConfig.ACTIVE_STATUS_KEY)!=null;
    }

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
                    if(!isActived()){
                        goActivate();
                    }else{
                        goMain();
                    }
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goMain();
                if(!isActived()){
                    goActivate();
                }else{
                    goMain();
                }
                first=false;
            }
        }, 1500);

        //测试接口
        //this.api_test_slide_list();
       // this.getLocation();

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
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        //finish();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    //广告列表
    private void api_test_slide_list() {
        int page=1;
        int limit=5;
        HttpApiService.getInstance().slide_list(1,5)
                .subscribe(new RxSubscriber<YYResponseData<SlideListData>>() {
                    @Override
                    public void onFail(YYResponseData<SlideListData> responseData) {
                        super.onFail(responseData);
                        Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(YYResponseData<SlideListData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("slide_list接口返回", responseData.toString());
                    }


                });
    }



    //获取省市区
//    private void getLocation() {
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);
//            return;
//        }
//        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    Geocoder geocoder = new Geocoder(SplashActivity.this, Locale.getDefault());
//                    try {
//                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                        if (addresses != null && addresses.size() > 0) {
//                            Log.d("addresses",addresses.toString());
//                            String city = addresses.get(0).getLocality();
//                            // 在这里处理获取到的城市信息
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        switch (requestCode) {
//            case 0: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // 权限被用户同意。
//                    getLocation();
//                } else {
//                    // 权限被用户拒绝了。
//                }
//                return;
//            }
//        }
//    }

    private void player(){
        String url = "http://192.168.0.1/1.mp4";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String type = "video/*";
        Uri uri = Uri.parse(url);
        intent.setDataAndType(uri,type);
        startActivity(intent);
    }



}