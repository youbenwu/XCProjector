package com.outmao.xcprojector.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.outmao.xcprojector.MainActivity;
import com.outmao.xcprojector.R;
import com.outmao.xcprojector.adapter.MainAdapter;
import com.outmao.xcprojector.adapter.PagingScrollHelper;
import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;

/***
 * @Author : Cheng
 * @CreateDate : 2023/9/8 10:03
 * @Description : 首页内容板块
 */
public class HomeFragment  extends Fragment {

    // 页面View
    private View view;

    // RecyclerView
    private RecyclerView rv;

    GSYVideoHelper smallVideoHelper;

    GSYVideoHelper.GSYVideoHelperBuilder gsySmallVideoHelperBuilder;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_recyclerview, container, false);
        getSlideData();

        initRecyclerView();
        return view;
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        rv = view.findViewById(R.id.layout_recycler);
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        //使用通用RecyclerView组件
        PagingScrollHelper scrollHelper = new PagingScrollHelper();//初始化横向管理器
        scrollHelper.setUpRecycleView(rv);//将横向布局管理器和recycler view绑定到一起
        scrollHelper.setOnPageChangeListener(new PagingScrollHelper.onPageChangeListener() {
            @Override
            public void onPageChange(int index) {
            }
        });
        //设置滑动监听
        scrollHelper.updateLayoutManger();
        scrollHelper.scrollToPosition(0);//默认滑动到第一页
        rv.setHorizontalScrollBarEnabled(true);

        rv.setAdapter(new MainAdapter(smallVideoHelper));
    }

    /**
     * 获取首页数据
     */
    private void getSlideData() {
        HttpApiService.getInstance().slide_list(0, 1)
                .subscribe(new RxSubscriber<YYResponseData<SlideListData>>() {
                    @Override
                    public void onFail(YYResponseData<SlideListData> responseData) {
                        super.onFail(responseData);
                        Toast.makeText(getActivity().getBaseContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(YYResponseData<SlideListData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("slide_list接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            // 封装数据

                        } else {

                        }
                    }


                });
    }

    private void initPlayer() {
        //创建小窗口帮助类
        smallVideoHelper = new GSYVideoHelper(getActivity());
        //配置
        gsySmallVideoHelperBuilder = new GSYVideoHelper.GSYVideoHelperBuilder();
        gsySmallVideoHelperBuilder
                .setHideStatusBar(true)
                .setNeedLockFull(true)
                .setCacheWithPlay(true)
                .setShowFullAnimation(false)
                .setRotateViewAuto(false)
                .setLockLand(true)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onQuitSmallWidget(String url, Object... objects) {
                        super.onQuitSmallWidget(url, objects);
                        smallVideoHelper.releaseVideoPlayer();
                    }
                });

        smallVideoHelper.setGsyVideoOptionBuilder(gsySmallVideoHelperBuilder);
    }

    /*if (smallVideoHelper.backFromFull()) {
        return;
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        smallVideoHelper.releaseVideoPlayer();
        GSYVideoManager.releaseAllVideos();
    }
}
