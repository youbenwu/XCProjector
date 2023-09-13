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
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private int pageIndex=0;

    private Timer timer;

    private Dialog closeDialog;

    private boolean isSetPwd=false;

    private Timer timer2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private final Handler timerHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            online();
            checkState();
            weaterQuery();
            return false;
        }
    });

    private final Handler timerHandler2=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            initHeader();
            return false;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        stopTimer2();
    }

    private void startTimer(){

        if(timer==null){
            timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    timerHandler.sendMessage(new Message());
                }
            };
            //timer.schedule(task,1000,1*1000);
            timer.schedule(task,1000,600*1000);
        }
    }
    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    private void startTimer2(){

        if(timer2==null){
            timer2=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    timerHandler2.sendMessage(new Message());
                }
            };
            timer2.schedule(task,1000,1000);
        }
    }
    private void stopTimer2(){
        if(timer2!=null){
            timer2.cancel();
            timer2=null;
        }
    }


    private void weaterQuery(){
        String d=SharepreferencesUtils.getShareInstance().getString("weater-day");
        if(d!=null){
            String nowDay=Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+"";
            if(d.equals(nowDay)){
                String json=SharepreferencesUtils.getShareInstance().getString("weater");
                if(json!=null){
                    WeaterResult result=new Gson().fromJson(json,WeaterResult.class);
                    updateWeaterInfo(result);
                    return;
                }
            }
        }

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
                    }

                    @Override
                    public void onSuccess(WeaterResult responseData) {
                        super.onSuccess(responseData);
                        if(responseData.getError_code()==0){
                            SharepreferencesUtils.getShareInstance().putString("weater-day",Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+"");
                            SharepreferencesUtils.getShareInstance().putString("weater",responseData.toString());
                            updateWeaterInfo(responseData);
                        }
                        Log.d("weatherQuery",responseData.toString());
                    }
                });
    }

    private void updateWeaterInfo(WeaterResult responseData){
        binding.header.tvTianqi.setText(responseData.getResult().getRealtime().getInfo());
        binding.header.tvQiwen.setText(responseData.getResult().getRealtime().getTemperature()+"°C");
        binding.header.tvFengli.setText(responseData.getResult().getRealtime().getPower());
        binding.header.tvKongqi.setText(responseData.getResult().getRealtime().getAqiString());

    }

    private void init(){
        initHeader();
        initMenus();
        initButtons();
        startTimer();
        startTimer2();
    }

    private void initHeader(){

        if(BaiduLocationManager.manager.location!=null){
            binding.header.tvCity.setText(BaiduLocationManager.manager.location.getCity());
        }

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2=new SimpleDateFormat("HH:mm:ss");
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
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        GSYVideoManager.onPause();
//                    }
//                }, 1000);
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
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        GSYVideoManager.onPause();
//                    }
//                }, 1000);
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
                    //navController.navigate(R.id.HomeFragment);
                    navController.popBackStack(R.id.HomeFragment,false);
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
        if(isNewActived()){
            setPwd();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        window.setLayout(width/2,height/2);
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
        //设置密文显示
        TransformationMethod method = PasswordTransformationMethod.getInstance();
        pwdEt.setTransformationMethod(method);
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

    //去激活设备
    private void goActivate(){
        Intent intent = new Intent(HomeActivity.this, ActivateActivity.class);
        startActivity(intent);
        //finish();
    }

    private void checkState(){
        HttpApiService.getInstance().account_status()
                .subscribe(new RxSubscriber<YYResponseData<AccountStatusData>>() {
                    @Override
                    public void onFail(YYResponseData<AccountStatusData> responseData) {
                        super.onFail(responseData);
                        Log.d("account_status接口返回", responseData.toString());
                        Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(YYResponseData<AccountStatusData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("account_status接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            if(responseData.getData().getStatus()==0){
                                //未激活
                                goActivate();
                                finish();
                                return;
                            }
                            if(responseData.getData().getDetail_info().getClose_status()==1){
                                //欠费
                                showCloseDialog();
                                return;
                            }
                            if(closeDialog!=null){
                                closeDialog.cancel();
                                closeDialog=null;
                            }
                            if(isNewActived()){
                                setPwd();
                            }
                        }else{
                            Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    private boolean isNewActived(){
        String isNew=SharepreferencesUtils.getShareInstance().getString(AppConfig.NEW_ACTIVE);
        return isNew!=null&&isNew.equals("1");
    }
    private void setNotNewActived(){
        SharepreferencesUtils.getShareInstance().putString(AppConfig.NEW_ACTIVE,"0");
    }

    private void showCloseDialog(){
        if(closeDialog!=null){
            closeDialog.cancel();
            closeDialog=null;
        }
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


    private void setPwd(){

        if(closeDialog!=null){
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        window.setLayout(width/2,height/2);
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
        //设置密文显示
        TransformationMethod method = PasswordTransformationMethod.getInstance();
        pwdEt.setTransformationMethod(method);
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
                                    setNotNewActived();
                                    //checkPwd();
                                }else{
                                    Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }


                        });
            }
        });

    }



    private void checkPwd(){
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
            MenuItemView menu_home=menusFragment.getView().findViewById(R.id.menu_home);
            MenuItemView menu_movies=menusFragment.getView().findViewById(R.id.menu_movies);
            MenuItemView menu_setup=menusFragment.getView().findViewById(R.id.menu_setup);
            if(menu_home.iconView.isFocused()){
                menu_movies.iconView.requestFocus();
                return true;
            }

            if(menu_movies.iconView.isFocused()){
                menu_setup.iconView.requestFocus();
                return true;
            }
            if(menu_setup.iconView.isFocused()){
                menu_home.iconView.requestFocus();
                return true;
            }

        }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP){

            //左边菜单控制
            MainMenusFragment menusFragment=(MainMenusFragment)getSupportFragmentManager().findFragmentById(R.id.menus);
            MenuItemView menu_home=menusFragment.getView().findViewById(R.id.menu_home);
            MenuItemView menu_movies=menusFragment.getView().findViewById(R.id.menu_movies);
            MenuItemView menu_setup=menusFragment.getView().findViewById(R.id.menu_setup);
            if(menu_home.iconView.isFocused()){
                menu_setup.iconView.requestFocus();
                return true;
            }
            if(menu_movies.iconView.isFocused()){
                menu_home.iconView.requestFocus();
                return true;
            }
            if(menu_setup.iconView.isFocused()){
                menu_movies.iconView.requestFocus();
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
                view=((HomeFragment) fragment).getViewById(R.id.video_view1_image);
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