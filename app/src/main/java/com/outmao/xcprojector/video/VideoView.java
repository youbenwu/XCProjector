package com.outmao.xcprojector.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.outmao.xcprojector.R;
import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.image.ImagePagerActivity;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.views.XfermodeImageView;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.List;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VideoView extends RelativeLayout {


    public void setDestImage(int resId){
        imageView.setDest(resId);
    }

    private OrientationUtils orientationUtils;

    StandardGSYVideoPlayer detailPlayer;
    private XfermodeImageView imageView;
    private ImageView playButton;

    // 视频封面
    private ImageView videoCover;

    private String videoUrl;

    private List<String> imageUrls;

    private Boolean isAutoPlayer = false;

    public void initData(String videoUrl,List<String> imageUrls){
        setVideoUrl(videoUrl);
        setImageUrls(imageUrls);
        if(videoUrl!=null&&(imageUrls==null||imageUrls.size()==0)){
            loadVideoImage();
        }

    }

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(bitmap!=null) {
                imageView.setImageBitmap(bitmap);
                bitmap = null;
            }
            return false;
        }
    });

    private Bitmap bitmap;
    private void loadVideoImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(videoUrl);
                    long timeUs = 0; // 0秒的时间戳
                    bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    handler.sendMessage(new Message());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadVideo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("VideoView videoUrl: ", videoUrl);
                    detailPlayer.setUp(videoUrl, true, "");
                    handler.sendMessage(new Message());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setVideoUrl(String videoUrl){
        this.videoUrl=videoUrl;
        if(videoUrl!=null&&videoUrl.length()>0){
            playButton.setVisibility(VISIBLE);
        }else{
            playButton.setVisibility(INVISIBLE);
        }
    }
    public void setImageUrls(List<String> imageUrls){
        this.imageUrls=imageUrls;
        if(imageUrls!=null&&imageUrls.size()>0) {
            Glide.with(this).load(imageUrls.get(0)).centerCrop().into(imageView);
//            Glide.with(this).load(imageUrls.get(0)).centerCrop().into(videoCover);
        }
    }


    public VideoView(Context context) {
        super(context);
        inflate(getContext(), R.layout.view_video, this);
        init();
    }

    public VideoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_video, this);
        init();
    }

    public VideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.view_video, this);
        init();
    }

    public VideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(getContext(), R.layout.view_video, this);
        init();
    }

    private void init(){
        imageView=findViewById(R.id.iv_image);
        playButton=findViewById(R.id.iv_play);
        detailPlayer=findViewById(R.id.playerView);
        playButton.setVisibility(INVISIBLE);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoUrl!=null&&videoUrl.length()>0){
//                    Intent intent = new Intent(getContext(), VideoPlayActivity.class);
//                    intent.putExtra("url",videoUrl);
//                    getContext().startActivity(intent);
                    openSystemVideo(videoUrl);
                }else if(imageUrls!=null&&imageUrls.size()>0){
                    Intent intent = new Intent(getContext(), ImagePagerActivity.class);
                    intent.putExtra("data",new Gson().toJson(imageUrls));
                    getContext().startActivity(intent);
                }
                //后台统计数据
                slide_info();
            }
        });
    }

    private void openSystemVideo(String url)  {
        try {
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
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void slide_info(){
        HttpApiService.getInstance().slide_info(getTag()+"")
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

    private void initVideoPlayer() {

        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        detailPlayer.getBackButton().setVisibility(View.VISIBLE);
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils((Activity) getContext(), detailPlayer);
        //初始化不打开外部的旋转
        // orientationUtils.setEnable(false);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
//                .setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setCacheWithPlay(false)
                .setVideoTitle("")
                .setUrl(videoUrl)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
////                        isPlay = true;
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);

                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
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
                detailPlayer.startWindowFullscreen((Activity) getContext(), false, true);
            }
        });
        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                // orientationUtils.resolveByClick();

                // 第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen((Activity) getContext(), false, true);
            }
        });
    }

    /**
     * true 显示, false 不显示
     * @param isVisible
     */
    public void setImageViewVisible(Boolean isVisible) {
        try {
            isAutoPlayer = isVisible;
            imageView.setVisibility((isVisible ? View.VISIBLE : View.GONE));
        } catch(Exception e) {
            imageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 开始播放
     */
    public void onStartPlayer() {
        // 播放
        if(isAutoPlayer) {
            detailPlayer.startPlayLogic();
        } else {

        }
    }


}
