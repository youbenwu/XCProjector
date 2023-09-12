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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.api.models.SlideInfo;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.api.models.SlideListSubSlides;
import com.outmao.xcprojector.api.models.WeaterResult;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.ActivityHomeBinding;
import com.outmao.xcprojector.databinding.ActivityMainBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.util.BaiduLocationManager;
import com.outmao.xcprojector.util.SharepreferencesUtils;
import com.outmao.xcprojector.views.MenuItemView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;


    private int pageIndex=0;

    private Timer timer;

    private Dialog closeDialog;

    private boolean isCheckPwd=false;

    private boolean isWeaterQuery=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void startTimer(){

        if(timer==null){
            timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    online();
                    checkState();
                    if(!isWeaterQuery){
                        isWeaterQuery=true;
                        weaterQuery();
                    }
                }
            };
            timer.schedule(task,1000,600*1000);
        }
    }
    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    private void weaterQuery(){
        String city="广州";
        if(BaiduLocationManager.manager.location!=null){
            city=BaiduLocationManager.manager.location.getCity();
        }
        HttpApiService.getInstance().weatherQuery(city)
                .subscribe(new RxSubscriber<WeaterResult>() {
                    @Override
                    public void onFail(WeaterResult responseData) {
                        super.onFail(responseData);
                        Log.d("weatherQuery",responseData.toString());
                        isWeaterQuery=false;
                    }

                    @Override
                    public void onSuccess(WeaterResult responseData) {
                        super.onSuccess(responseData);
                        if(responseData.getError_code()==0){
                            binding.header.tvQiwen.setText(responseData.getResult().getRealtime().getTemperature()+"°C");
                            binding.header.tvFengli.setText(responseData.getResult().getRealtime().getPower());
                            binding.header.tvKongqi.setText(responseData.getResult().getRealtime().getHumidity());
                            binding.header.tvKongqi.setText(responseData.getResult().getRealtime().getInfo());
                        }
                        Log.d("weatherQuery",responseData.toString());
                    }
                });
    }

    private void init(){
        initHeader();
        initMenus();
        initButtons();
        startTimer();

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
                    pageIndex=0;
                    NavController navController = Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.HomeFragment);
                    return true;
                }else if(action==MainMenusFragment.MENU_ACTION_SETUP){
                    onClickSetting();
                    return false;
                }else if(action==MainMenusFragment.MENU_ACTION_MOVIES){
                    pageIndex=2;
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
                    pageIndex=0;
                }else if(navDestination.getId()==R.id.VideoFragment){
                    menusFragment.setSelectedItem(MainMenusFragment.MENU_ACTION_MOVIES);
                    binding.ibBack.setVisibility(View.GONE);
                    binding.ibNext.setVisibility(View.GONE);
                    pageIndex=2;
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

    private void online(){
        HttpApiService.getInstance().account_line()
                .subscribe(new RxSubscriber<YYResponseData<Object>>() {
                    @Override
                    public void onFail(YYResponseData<Object> responseData) {
                        super.onFail(responseData);
                        Log.d("account_line接口返回", responseData.toString());
                    }
                    @Override
                    public void onSuccess(YYResponseData<Object> responseData) {
                        super.onSuccess(responseData);
                        Log.d("account_line接口返回", responseData.toString());
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
                                if(closeDialog==null) {
                                    showCloseDialog();
                                }else{
                                    closeDialog.cancel();
                                    closeDialog=null;
                                }
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
        TextView btn_ok =(TextView) dialogView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkState();
            }
        });
        dialog.setView(dialogView);
        dialog.show();

        closeDialog=dialog;
    }


    private void checkNewPwd(){

        if(closeDialog!=null){
            return;
        }

        if(isCheckPwd){
            return;
        }

        if(SharepreferencesUtils.getShareInstance().getString(AppConfig.ACTIVE_STATUS_KEY)==null){
            return;
        }

        isCheckPwd=true;

        String pwd= SharepreferencesUtils.getShareInstance().getString(AppConfig.PWD);
        if(pwd!=null){
            //checkPwd();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
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
        cancelTv.setVisibility(View.GONE);
        titleTv.setText("尊敬的用户");
        msgTv.setText("欢迎你使用喵影互娱");
        submsgTv.setText("为了你的安全使用\n请先设定您的“设置”新密码");
//        closeTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//            }
//        });
//        cancelTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//            }
//        });
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){
            //如果是左按钮，则跳到右按钮
           if(binding.ibBack.isFocused()){
               binding.ibNext.requestFocus();
               return true;
           }

            //左边菜单控制
            if(pageIndex==0) {
                MainMenusFragment menusFragment = (MainMenusFragment) getSupportFragmentManager().findFragmentById(R.id.menus);
                MenuItemView menu_home = menusFragment.getView().findViewById(R.id.menu_home);
                if (menu_home.iconView.isFocused()) {
                    binding.ibBack.requestFocus();
                    return true;
                }
                menusFragment = (MainMenusFragment) getSupportFragmentManager().findFragmentById(R.id.menus);
                MenuItemView menu_setup = menusFragment.getView().findViewById(R.id.menu_setup);
                if (menu_setup.iconView.isFocused()) {
                    binding.ibBack.requestFocus();
                    return true;
                }
                menusFragment = (MainMenusFragment) getSupportFragmentManager().findFragmentById(R.id.menus);
                MenuItemView menu_movies = menusFragment.getView().findViewById(R.id.menu_movies);
                if (menu_movies.iconView.isFocused()) {
                    binding.ibBack.requestFocus();
                    return true;
                }
            }

            //从分页内容出来
            Fragment mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            Fragment fragment = mMainNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (fragment instanceof HomeFragment) {
                View view=((HomeFragment) fragment).getViewById(R.id.video_view2);
                if(view!=null&&view.isFocused()){
                    binding.ibNext.requestFocus();
                    return true;
                }
                view=((HomeFragment) fragment).getViewById(R.id.video_view3);
                if(view!=null&&view.isFocused()){
                    binding.ibNext.requestFocus();
                    return true;
                }
            }


        }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN){

            //左边菜单控制
            MainMenusFragment menusFragment=(MainMenusFragment)getSupportFragmentManager().findFragmentById(R.id.menus);
            MenuItemView menu_setup=menusFragment.getView().findViewById(R.id.menu_setup);
            if(menu_setup.iconView.isFocused()){
                MenuItemView menu_movies=menusFragment.getView().findViewById(R.id.menu_movies);
                menu_movies.iconView.requestFocus();
                return true;
            }

        }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP){

            //左边菜单控制
            MainMenusFragment menusFragment=(MainMenusFragment)getSupportFragmentManager().findFragmentById(R.id.menus);
            MenuItemView menu_movies=menusFragment.getView().findViewById(R.id.menu_movies);
            if(menu_movies.iconView.isFocused()){
                MenuItemView menu_setup=menusFragment.getView().findViewById(R.id.menu_setup);
                menu_setup.iconView.requestFocus();
                return true;
            }


        }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){


            //从分页内容出来
            Fragment mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            Fragment fragment = mMainNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (fragment instanceof HomeFragment) {
                View view=((HomeFragment) fragment).getViewById(R.id.video_view1);
                if(view!=null&&view.isFocused()){
                    binding.ibBack.requestFocus();
                    return true;
                }
                view=((HomeFragment) fragment).getViewById(R.id.video_view4);
                if(view!=null&&view.isFocused()){
                    binding.ibBack.requestFocus();
                    return true;
                }
            }

        } else if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            // 返回不执行操作
            return false;
        }


        return super.onKeyDown(keyCode, event);
    }


}