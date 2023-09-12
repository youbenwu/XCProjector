package com.outmao.xcprojector.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.outmao.xcprojector.HomeActivity;
import com.outmao.xcprojector.R;

import java.util.List;

/***
 * @Author : Cheng
 * @CreateDate : 2023/9/8 12:40
 * @Description : 影视页
 */
public class VideoFragment  extends Fragment {

    // 页面View
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.video_fragment_layout, container, false);

        initView();

        return view;
    }

    private void initView() {
        // 图片点击事件
        view.findViewById(R.id.image_aiqiyi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToOtherApplication(OTHER_APPLICATION.QI_YI_GUO_JI_SU);
            }
        });

        view.findViewById(R.id.image_youku).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToOtherApplication(OTHER_APPLICATION.YOU_KU);
            }
        });

        view.findViewById(R.id.image_mangguo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToOtherApplication(OTHER_APPLICATION.MANG_GUO_TV);
            }
        });

        view.findViewById(R.id.image_bestv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToOtherApplication(OTHER_APPLICATION.BES_TV);
            }
        });

        view.findViewById(R.id.image_kugou).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToOtherApplication(OTHER_APPLICATION.KU_GOU_YIN_YUE_DANG_BEI);
            }
        });
    }


    private void jumpToOtherApplication(OTHER_APPLICATION otherApplication) {
        try{
            // 获取并打印应用名及包名,需要时解除注释,不用时不打印,应用过多时耗费资源
            /*PackageManager packageManager = getActivity().getPackageManager();
            List<PackageInfo> packList = packageManager.getInstalledPackages(0);
            for (PackageInfo aa: packList) {
                Log.d("test: ", aa.applicationInfo.packageName);
                Log.d("appinfo", packageManager.getApplicationLabel(aa.applicationInfo).toString());
            }*/

            // 包名不是Null 的时候跳转
            if(otherApplication.getPackageName() != null) {
                PackageInfo pi = getActivity().getPackageManager().getPackageInfo(otherApplication.getPackageName(), 0);

                Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                resolveIntent.setPackage(pi.packageName);

                List<ResolveInfo> apps = getActivity().getPackageManager().queryIntentActivities(resolveIntent, 0);

                ResolveInfo ri = apps.iterator().next();
                if (ri != null ) {
                    String packageName = ri.activityInfo.packageName;
                    String className = ri.activityInfo.name;

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName(packageName, className);

                    intent.setComponent(cn);
                    getActivity().startActivity(intent);
                } else {
                    // 包名是Null的时候,提示用户下载
                    Log.d("OtherApplication", "未找到对应应用：" + otherApplication.getPackageName());
                }


            }
        }catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "暂无未支持该应用", Toast.LENGTH_LONG).show();
            Log.d("OtherApplication Error: ", "otherApplication.getPackageName()");
        }
    }

}

/**
 * 第三方应用的枚举
 * 应用包名根据系统打印出来数据选择需要的写死
 * 应用商城包名: com.dangbei.mimir.lightos.appstore
 * com.dangbei.dbmusic， com.tv.kuaisou
 */
enum OTHER_APPLICATION {
    QI_YI_GUO_JI_SU("com.gitvjisu.video"),
    YUN_SHI_TING("com.ktcp.tvvideo"),
    YOU_KU("com.cibn.tv"),
    XIAO_HONG_SHU(""),
    MANG_GUO_TV("com.starcor.mango"),
    BES_TV("com.tv.kuaisou"),
    KU_GOU_YIN_YUE_DANG_BEI("com.dangbei.dbmusic"),
    DANG_BEI_APP_STORE("com.dangbei.mimir.lightos.appstore");

    private String packageName;

    OTHER_APPLICATION(String value) {
        this.packageName = value;
    }

    public String getPackageName() {
        return ("").equals(this.packageName) ? null : this.packageName;
    }
}
