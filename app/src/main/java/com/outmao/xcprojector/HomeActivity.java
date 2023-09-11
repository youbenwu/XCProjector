package com.outmao.xcprojector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.api.models.SlideInfo;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.api.models.SlideListSubSlides;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.ActivityHomeBinding;
import com.outmao.xcprojector.databinding.ActivityMainBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.util.BaiduLocationManager;
import com.outmao.xcprojector.util.SharepreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init(){
        initHeader();
        initMenus();
        initButtons();
        checkState();
    }

    private void initHeader(){

        if(BaiduLocationManager.manager.location!=null){
            binding.header.tvCity.setText(BaiduLocationManager.manager.location.getCity());
        }

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2=new SimpleDateFormat("HH-mm-ss");
        int week=calendar.get(Calendar.DAY_OF_WEEK);
        String w=week==1?"周日":week==2?"周一":week==3?"周二":week==4?"周三":week==5?"周四":week==6?"周五":week==7?"周六":"周六";
        String time=format.format(calendar.getTime())+" "+ w+" "+format2.format(calendar.getTime());
        binding.header.tvTime.setText(time);


    }

    private void initButtons(){
        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                Fragment fragment = mMainNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                if (fragment instanceof HomeFragment) {
                    ((HomeFragment)fragment).backPage();
                }
            }
        });
        binding.ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                Fragment fragment = mMainNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                if (fragment instanceof HomeFragment) {
                    ((HomeFragment)fragment).nextPage();
                }
            }
        });
    }

    private void initMenus(){
        MainMenusFragment menusFragment=(MainMenusFragment)getSupportFragmentManager().findFragmentById(R.id.menus);
        menusFragment.setListener(new MainMenusFragment.MainMenusListener() {
            @Override
            public boolean onMenu(int action) {
                if(action==MainMenusFragment.MENU_ACTION_HOME){
                    NavController navController = Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.HomeFragment);
                    return true;
                }else if(action==MainMenusFragment.MENU_ACTION_SETUP){
                    onClickSetting();
                    return false;
                }else if(action==MainMenusFragment.MENU_ACTION_MOVIES){
                    NavController navController = Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.VideoFragment);
                    return true;
                }
                return false;
            }
        });
        NavController navController = Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment_content_main);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if(navDestination.getId()==R.id.HomeFragment){
                    menusFragment.setSelectedItem(MainMenusFragment.MENU_ACTION_HOME);
                    binding.ibBack.setVisibility(View.VISIBLE);
                    binding.ibNext.setVisibility(View.VISIBLE);
                }else if(navDestination.getId()==R.id.VideoFragment){
                    menusFragment.setSelectedItem(MainMenusFragment.MENU_ACTION_MOVIES);
                    binding.ibBack.setVisibility(View.GONE);
                    binding.ibNext.setVisibility(View.GONE);
                }
            }
        });
    }





    private void onClickSetting(){
        String pwd= SharepreferencesUtils.getShareInstance().getString(AppConfig.PWD);
        if(pwd==null){
            checkNewPwd();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View dialogView =  View.inflate(HomeActivity.this, R.layout.dialog, null);
        dialog.setView(dialogView);
        dialog.show();
        // 获取布局控件
        TextView titleTv =(TextView) dialogView.findViewById(R.id.tv_title);
        TextView closeTv =(TextView) dialogView.findViewById(R.id.tv_close);
        TextView msgTv =(TextView) dialogView.findViewById(R.id.tv_msg);
        TextView submsgTv =(TextView) dialogView.findViewById(R.id.tv_sub_msg);
        TextView okTv =(TextView) dialogView.findViewById(R.id.btn_ok);
        TextView cancelTv =(TextView) dialogView.findViewById(R.id.btn_cancel);
        EditText pwdEt =(EditText) dialogView.findViewById(R.id.et_pwd);
        titleTv.setText("尊敬的用户");
        msgTv.setText("此内容受密码保护");
        submsgTv.setText("请输入您的设置密码");
        closeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd=pwdEt.getText().toString().trim();
                if(pwd.length()==0){
                    Toast.makeText(HomeActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }
                okTv.setEnabled(false);
                HttpApiService.getInstance().account_check_pwd(pwd)
                        .subscribe(new RxSubscriber<YYResponseData<Object>>() {
                            @Override
                            public void onFail(YYResponseData<Object> responseData) {
                                super.onFail(responseData);
                                okTv.setEnabled(true);
                                Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(YYResponseData<Object> responseData) {
                                super.onSuccess(responseData);
                                okTv.setEnabled(true);
                                Log.d("接口返回", responseData.toString());
                                if(responseData.isSuccess()){
                                    dialog.cancel();
                                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                                }else{
                                    Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                        });
            }
        });

    }


    private void checkState(){
        HttpApiService.getInstance().account_status()
                .subscribe(new RxSubscriber<YYResponseData<AccountStatusData>>() {
                    @Override
                    public void onFail(YYResponseData<AccountStatusData> responseData) {
                        super.onFail(responseData);
                        Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        checkNewPwd();
                    }

                    @Override
                    public void onSuccess(YYResponseData<AccountStatusData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            if(responseData.getData().getDetail_info().getClose_status()==1){
                                //欠费
                                showCloseDialog();
                                return;
                            }
                        }else{
                            Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        checkNewPwd();
                    }

                });
    }

    private void showCloseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View dialogView =  View.inflate(HomeActivity.this, R.layout.dialog2, null);
        dialog.setView(dialogView);
        dialog.show();
    }


    private void checkNewPwd(){
        String pwd= SharepreferencesUtils.getShareInstance().getString(AppConfig.PWD);
        if(pwd!=null){
            checkPwd();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View dialogView =  View.inflate(HomeActivity.this, R.layout.dialog, null);
        dialog.setView(dialogView);
        dialog.show();
        // 获取布局控件
        TextView titleTv =(TextView) dialogView.findViewById(R.id.tv_title);
        TextView closeTv =(TextView) dialogView.findViewById(R.id.tv_close);
        TextView msgTv =(TextView) dialogView.findViewById(R.id.tv_msg);
        TextView submsgTv =(TextView) dialogView.findViewById(R.id.tv_sub_msg);
        TextView okTv =(TextView) dialogView.findViewById(R.id.btn_ok);
        TextView cancelTv =(TextView) dialogView.findViewById(R.id.btn_cancel);
        EditText pwdEt =(EditText) dialogView.findViewById(R.id.et_pwd);
        titleTv.setText("尊敬的用户");
        msgTv.setText("欢迎你使用喵影互娱");
        submsgTv.setText("为了你的安全使用\n请先设定您的“设置”新密码");
        closeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd=pwdEt.getText().toString().trim();
                if(pwd.length()==0){
                    Toast.makeText(HomeActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }
                okTv.setEnabled(false);
                HttpApiService.getInstance().account_pwd(pwd)
                        .subscribe(new RxSubscriber<YYResponseData<Object>>() {
                            @Override
                            public void onFail(YYResponseData<Object> responseData) {
                                super.onFail(responseData);
                                okTv.setEnabled(true);
                                Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(YYResponseData<Object> responseData) {
                                super.onSuccess(responseData);
                                okTv.setEnabled(true);
                                Log.d("接口返回", responseData.toString());
                                if(responseData.isSuccess()){
                                    dialog.cancel();
                                    SharepreferencesUtils.getShareInstance().putString(AppConfig.PWD,pwd);
                                    checkPwd();
                                }else{
                                    Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }


                        });
            }
        });

    }



    private void checkPwd(){
        String ischeck=SharepreferencesUtils.getShareInstance().getString(AppConfig.CHECK_PWD);
        if(ischeck!=null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View dialogView =  View.inflate(HomeActivity.this, R.layout.dialog, null);
        dialog.setView(dialogView);
        dialog.show();
        // 获取布局控件
        TextView titleTv =(TextView) dialogView.findViewById(R.id.tv_title);
        TextView closeTv =(TextView) dialogView.findViewById(R.id.tv_close);
        TextView msgTv =(TextView) dialogView.findViewById(R.id.tv_msg);
        TextView submsgTv =(TextView) dialogView.findViewById(R.id.tv_sub_msg);
        TextView okTv =(TextView) dialogView.findViewById(R.id.btn_ok);
        TextView cancelTv =(TextView) dialogView.findViewById(R.id.btn_cancel);
        EditText pwdEt =(EditText) dialogView.findViewById(R.id.et_pwd);
        titleTv.setText("尊敬的用户");
        msgTv.setText("此内容受密码保护");
        submsgTv.setText("请输入您的设置密码，下次进\n入则不需要输入密码");
        closeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd=pwdEt.getText().toString().trim();
                if(pwd.length()==0){
                    Toast.makeText(HomeActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }
                okTv.setEnabled(false);
                HttpApiService.getInstance().account_check_pwd(pwd)
                        .subscribe(new RxSubscriber<YYResponseData<Object>>() {
                            @Override
                            public void onFail(YYResponseData<Object> responseData) {
                                super.onFail(responseData);
                                okTv.setEnabled(true);
                                Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(YYResponseData<Object> responseData) {
                                super.onSuccess(responseData);
                                okTv.setEnabled(true);
                                Log.d("接口返回", responseData.toString());
                                Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                                if(responseData.isSuccess()){
                                    dialog.cancel();
                                    SharepreferencesUtils.getShareInstance().putString(AppConfig.CHECK_PWD,"1");
                                }
                            }


                        });
            }
        });



    }





}