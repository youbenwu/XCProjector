package com.outmao.xcprojector;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.api.models.SlideInfo;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.api.models.SlideListSubSlides;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.FragmentSlideListBinding;
import com.outmao.xcprojector.image.ImagePagerActivity;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.video.TvVideoPlayer;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SlideListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlideListFragment extends Fragment {

    private FragmentSlideListBinding binding;

    private static final String ARG_PAGE = "page";
    private static final String ARG_DATA = "data";
    private int page;

    private SlideListData data;

    private OrientationUtils orientationUtils;

    StandardGSYVideoPlayer detailPlayer;

    private String topVideoUrl;
    private int topVideoType = 1;

    private ImageView topVideoCover;

    private boolean showUser=true;

    public SlideListFragment() {
    }


    public static SlideListFragment newInstance(int page,SlideListData data) {
        SlideListFragment fragment = new SlideListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        if(data!=null) {
            args.putString(ARG_DATA, data.toString());
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt(ARG_PAGE);
            String json=getArguments().getString(ARG_DATA);
            if(json!=null){
                data=SlideListData.fromJson(json);
            }
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.videoView1Image.setDestImage(R.drawable.ic_play_mb);
        binding.videoView2.setDestImage(R.drawable.ic_play_mb2);
        binding.videoView3.setDestImage(R.drawable.ic_play_mb2);
        //Glide.with(this).load("https://qn.huwing.cn/2023/09/01/16935609583821101-1920_1080.jpg").centerCrop().into(binding.testImage);

        updateData();
        if(data==null){
          loadData();
        }
    }


    private void loadData(){
        HttpApiService.getInstance().slide_list(page,5)
                .subscribe(new RxSubscriber<YYResponseData<SlideListData>>() {
                    @Override
                    public void onFail(YYResponseData<SlideListData> responseData) {
                        super.onFail(responseData);
                        Toast.makeText(SlideListFragment.this.getContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        checkState();
                        if(AppConfig.testData){
                            showTestData();
                        }
                    }

                    @Override
                    public void onSuccess(YYResponseData<SlideListData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("slide_list接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            data=responseData.getData();
                            if(data.getSub_slides()!=null&&data.getSub_slides().getLast_page()>0&&data.getSub_slides().getList().size()>0) {
                                updateData();
                                return;
                            }
                        }else{
                            Toast.makeText(SlideListFragment.this.getContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        if(AppConfig.testData){
                            showTestData();
                        }
                    }

                });
    }

    private void checkState(){
        HttpApiService.getInstance().account_status()
                .subscribe(new RxSubscriber<YYResponseData<AccountStatusData>>() {
                    @Override
                    public void onFail(YYResponseData<AccountStatusData> responseData) {
                        super.onFail(responseData);
                        Log.d("account_status接口返回", responseData.toString());
                    }

                    @Override
                    public void onSuccess(YYResponseData<AccountStatusData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("account_status接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            if(responseData.getData().getStatus()==0){
                                //未激活
                                goActivate();
                                getActivity().finish();
                                return;
                            }
                        }
                    }
                });
    }

    //去激活设备
    private void goActivate(){
        Intent intent = new Intent(getActivity(), ActivateActivity.class);
        startActivity(intent);
        //finish();
    }


    private void updateData(){
        binding.rlView1.setVisibility(View.INVISIBLE);
        binding.rlView2.setVisibility(View.INVISIBLE);
        binding.rlView3.setVisibility(View.INVISIBLE);
        binding.rlView4.setVisibility(View.INVISIBLE);
        binding.rlView5.setVisibility(View.INVISIBLE);
        binding.rlView6.setVisibility(View.INVISIBLE);
        if(data!=null){
            //主图
            if(data.getMain_slide()!=null){
                SlideInfo info=data.getMain_slide();
//                binding.videoView1.initData(info.getVideo_url_txt(),info.getThumbs_txt());
//                binding.videoView1.setImageViewVisible(false);
                Log.d("VideoPlayer url: ", info.getVideo_url_txt());
                topVideoUrl = info.getVideo_url_txt();
                topVideoType = info.getType();
                binding.hotelName.setText(info.getHotel_name() == null ? "" : info.getHotel_name());
                binding.mainTitle.setText(info.getTitle() == null ? "" : info.getTitle());
                if(topVideoType == 2) {
                    try {
                        initVideoPlayer();
                    } catch(Exception e) {

                    }
                    binding.videoView1Image.setVisibility(View.GONE);
                    binding.videoView1.setVisibility(View.VISIBLE);
                    Log.d("VideoPlayer url: ", info.getVideo_url_txt());

                    if(info.getThumbs_txt() != null && info.getThumbs_txt().size() > 0 && !("").equals(info.getThumbs_txt().get(0))) {
                        Glide.with(this).load(info.getThumbs_txt().get(0)).centerCrop().into(topVideoCover);
                    }
                    binding.rlView1.setTag(info.getId());
                    binding.rlView1.setVisibility(View.VISIBLE);

                    if(showUser) {
                        startPlay();
                    }

                } else {
                    binding.videoView1.setVisibility(View.GONE);
                    binding.videoView1Image.setVisibility(View.VISIBLE);
                    binding.videoView1Image.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.videoView1Image.setTag(info.getId());
                    binding.videoView1Image.setTag(info.getId());
                    binding.rlView1.setVisibility(View.VISIBLE);
                }

            }


            if(data.getSub_slides()!=null&&data.getSub_slides().getList()!=null){
                //2
                if(data.getSub_slides().getList().size()>0){
                    SlideInfo info=data.getSub_slides().getList().get(0);
                    binding.videoView2Title.setText(info.getTitle() == null ? "" : info.getTitle());
                    binding.videoView2Subtitle.setText(info.getSub_title() == null ? "" : info.getSub_title());
                    binding.videoView2.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.videoView2.setTag(info.getId());
                    binding.rlView2.setVisibility(View.VISIBLE);
                }

                //3
                if(data.getSub_slides().getList().size()>1){
                    SlideInfo info=data.getSub_slides().getList().get(1);
                    binding.videoView3Title.setText(info.getTitle() == null ? "" : info.getTitle());
                    binding.videoView3Subtitle.setText(info.getSub_title() == null ? "" : info.getSub_title());
                    binding.videoView3.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.videoView3.setTag(info.getId());
                    binding.rlView3.setVisibility(View.VISIBLE);
                }

                //4
                if(data.getSub_slides().getList().size()>2){
                    SlideInfo info=data.getSub_slides().getList().get(2);
                    binding.videoView4Title.setText(info.getTitle() == null ? "" : info.getTitle());
                    binding.videoView4.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.videoView4.setTag(info.getId());
                    binding.rlView4.setVisibility(View.VISIBLE);
                }

                //5
                if(data.getSub_slides().getList().size()>3){
                    SlideInfo info=data.getSub_slides().getList().get(3);
                    binding.videoView5Title.setText(info.getTitle() == null ? "" : info.getTitle());
                    binding.videoView5.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.videoView5.setTag(info.getId());
                    binding.rlView5.setVisibility(View.VISIBLE);
                }

                //6
                if(data.getSub_slides().getList().size()>4){
                    SlideInfo info=data.getSub_slides().getList().get(4);
                    binding.videoView6Title.setText(info.getTitle() == null ? "" : info.getTitle());
                    binding.videoView6.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.videoView6.setTag(info.getId());
                    binding.rlView6.setVisibility(View.VISIBLE);
                }

            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSlideListBinding.inflate(inflater, container, false);

        //视频封面ImageView
        topVideoCover = new ImageView(getActivity());
        topVideoCover.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Log.d("VideoPlayer TYPE: ", topVideoType + "");
        detailPlayer = binding.videoView1;

        return binding.getRoot();
    }


    private void showTestData(){
        SlideListData data=new SlideListData();
        SlideInfo info1=new SlideInfo();
        info1.setThumbs_txt(new ArrayList<>());
        info1.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/4k/s/02/210924233115O14-0-lp.jpg");
        info1.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1114/062621110J7/210626110J7-10-1200.jpg");
        info1.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1113/031920120534/200319120534-7-1200.jpg");
        info1.setType(1);

        SlideInfo info2=new SlideInfo();
        info2.setThumbs_txt(new ArrayList<>());
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/4k/s/02/210924233115O14-0-lp.jpg");
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1114/062621110J7/210626110J7-10-1200.jpg");
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1113/031920120534/200319120534-7-1200.jpg");
        info2.setVideo_url_txt("https://lmg.jj20.com/up/allimg/4k/s/02/210924233115O14-0-lp.jpg");
        info2.setType(2);

        data.setMain_slide(info2);
        SlideListSubSlides subSlides=new SlideListSubSlides();
        subSlides.setLast_page(2);
        subSlides.setList(new ArrayList<>());
        subSlides.getList().add(info1);
        subSlides.getList().add(info2);
        subSlides.getList().add(info1);
        subSlides.getList().add(info2);
        subSlides.getList().add(info1);
        data.setSub_slides(subSlides);

        this.data=data;
        updateData();

    }

    private void initVideoPlayer() {

        if(topVideoType == 1) {
            return;
        }
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(getActivity(), detailPlayer);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
                .setThumbImageView(topVideoCover)
                .setIsTouchWiget(false)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setCacheWithPlay(false)
                .setVideoTitle("测试视频")
                .setLooping(true)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
//                        isPlay = true;
                    }

                    @Override
                    public void onClickStopFullscreen(String url, Object... objects) {
                        super.onClickStopFullscreen(url, objects);
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                        // 播放完成设置
                        TvVideoPlayer.setPlayComplete(true);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);

//                        if (orientationUtils != null) {
//                            orientationUtils.backToProtVideo();
//                        }
                    }

                    @Override
                    public void onQuitSmallWidget(String url, Object... objects) {
                        super.onQuitSmallWidget(url, objects);
                        //GSYVideoManager.onPause();

                    }
                }).setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            orientationUtils.setEnable(!lock);
                        }
                    }
                })
                .build(detailPlayer);


        detailPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用第三方控件播放, 前进后退会有问题
                // detailPlayer.startWindowFullscreen(getActivity(), false, true);
                // 调用系统播放,按键可用,视频播放进度上会有问题
                try{
                    // topVideoUrl = "http://tengdamy.cn/video/video2.mp4";
                    GSYVideoManager.releaseAllVideos();
                    openSystemVideo(topVideoUrl);
                } catch(Exception e) {

                }
            }
        });

//        detailPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(onBackPressed()) {
//
//                };
//            }
//        });
        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                // orientationUtils.resolveByClick();

                // 第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(getActivity(), false, true);
            }
        });
    }

   // @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        GSYVideoManager.onPause();
//    }

    /**
     * 返回是否全屏
     * @return
     */
    public boolean onBackPressed() {
        return GSYVideoManager.backFromWindowFull(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onPageSelected(boolean selected){
        if(selected){
            showUser=true;
            startPlay();
        }else{
            showUser=false;
            stopPlay();
        }
    }




    private void openSystemVideo(String url) throws Exception {
        if(url != null && !("").equals(url)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            String type = "video/*";
            Uri uri = Uri.parse(url);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri,type);
            getContext().startActivity(intent);
        } else {
            Toast.makeText(getContext(), "请先设置视频地址", Toast.LENGTH_LONG).show();
        }
        slide_info(binding.rlView1.getTag()+"");
    }

    private void slide_info(String id){
        HttpApiService.getInstance().slide_info(id)
                .subscribe(new RxSubscriber<YYResponseData<Object>>() {
                    @Override
                    public void onFail(YYResponseData<Object> responseData) {
                        super.onFail(responseData);
                        Log.d("slide_info接口返回", responseData.toString());
                    }
                    @Override
                    public void onSuccess(YYResponseData<Object> responseData) {
                        super.onSuccess(responseData);
                        Log.d("slide_info接口返回", responseData.toString());
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        //GSYVideoManager.onResume();
        if(showUser){
            startPlay();
        }
    }

    private Handler playHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int action=msg.getData().getInt("action");
            if(action==PLAY) {
                if (topVideoUrl != null && topVideoUrl.length() > 0) {
                    if(detailPlayer.isActivated()){
                        return false;
                    }
                    GSYVideoManager.releaseAllVideos();
                    detailPlayer.setUp(topVideoUrl, true, "");
                    detailPlayer.startPlayLogic();
                }
            }else if(action==STOP){
                detailPlayer.onVideoPause();
            }else if(action==RELEASE){
                detailPlayer.release();
            }
            return false;
        }
    });

    public static final int PLAY=0;
    public static final int STOP=1;
    public static final int RELEASE=2;

    private void startPlay(){
        Bundle bundle=new Bundle();
        bundle.putInt("action",PLAY);
        Message m=new Message();
        m.setData(bundle);
        playHandler.sendMessage(m);
    }

    private void stopPlay(){
        Bundle bundle=new Bundle();
        bundle.putInt("action",STOP);
        Message m=new Message();
        m.setData(bundle);
        playHandler.sendMessage(m);
    }

    private void releasePlay(){
        detailPlayer.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //GSYVideoManager.releaseAllVideos();
        releasePlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        //GSYVideoManager.onPause();
        stopPlay();
    }

}