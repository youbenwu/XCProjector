package com.outmao.xcprojector.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.outmao.xcprojector.R;

/***
 * @Author : Cheng
 * @CreateDate : 2023/9/8 12:40
 * @Description : 影视页
 */
public class VideoFragment  extends Fragment {

    // 页面View
    private View view;

    // RecyclerView
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.video_fragment_layout, container, false);



        return view;
    }


}
