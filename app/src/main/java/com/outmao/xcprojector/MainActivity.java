package com.outmao.xcprojector;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.databinding.ActivityMainBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.util.SharepreferencesUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkState();

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }



    private void onClickSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View dialogView =  View.inflate(MainActivity.this, R.layout.dialog, null);
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
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View dialogView =  View.inflate(MainActivity.this, R.layout.dialog2, null);
        dialog.setView(dialogView);
        dialog.show();
    }


    private void checkNewPwd(){
        String pwd=SharepreferencesUtils.getShareInstance().getString("pwd1");
        if(pwd==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            View dialogView =  View.inflate(MainActivity.this, R.layout.dialog, null);
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
                        Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
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
                                        SharepreferencesUtils.getShareInstance().putString("pwd1",pwd);
                                        checkPwd();
                                    }else{
                                        Toast.makeText(getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }


                            });
                }
            });

        }

    }



    private void checkPwd(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View dialogView =  View.inflate(MainActivity.this, R.layout.dialog, null);
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
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
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



}