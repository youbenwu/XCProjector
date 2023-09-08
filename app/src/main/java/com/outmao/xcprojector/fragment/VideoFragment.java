package com.outmao.xcprojector.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
//                Intent intent = new Intent();
//                Log.d("test: ", "com.");
//                ComponentName cn = new ComponentName("com.dangbeimarket", getActivity().getPackageName());
//                try {
//                    Log.d("test: ", "com.t");
//                    intent.setComponent(cn);
//                    getActivity().startActivity(intent);
//                } catch(Exception e) {
//                    Log.d("test: ", "com.e");
//                }
                try{
//                    Log.d("test: ", "ss");
//                    List<PackageInfo> packList = getActivity().getPackageManager().getInstalledPackages(0);
//                    for (PackageInfo aa: packList) {
//                        Log.d("test: ", aa.packageName);
//                    }
//                    String package_name="com.android.tv.settings";
//                    PackageManager packageManager = getActivity().getPackageManager();
//                    Intent it = packageManager.getLaunchIntentForPackage(package_name);
//                    Log.d("test: ", it == null ? "true":"false");
//                    getActivity().startActivity(it);

                    // Android bilibili://



                }catch(Exception e) {

                }
            }
        });
    }


}
