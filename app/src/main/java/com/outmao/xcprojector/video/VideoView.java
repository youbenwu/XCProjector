package com.outmao.xcprojector.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.outmao.xcprojector.R;
import com.outmao.xcprojector.image.ImagePagerActivity;
import com.outmao.xcprojector.views.XfermodeImageView;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.List;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VideoView extends RelativeLayout {


    public void setDestImage(int resId){
        imageView.setDest(resId);
    }

    private VideoPlayerView playerView;
    private XfermodeImageView imageView;
    private ImageView playButton;

    private String videoUrl;

    private List<String> imageUrls;

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
        playerView=findViewById(R.id.playerView);
        playButton.setVisibility(INVISIBLE);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoUrl!=null&&videoUrl.length()>0){
                    Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                    intent.putExtra("url",videoUrl);
                    getContext().startActivity(intent);

                }else if(imageUrls!=null&&imageUrls.size()>0){
                    Intent intent = new Intent(getContext(), ImagePagerActivity.class);
                    intent.putExtra("data",new Gson().toJson(imageUrls));
                    getContext().startActivity(intent);
                }
            }
        });
//        playerView.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
//            @Override
//            public void onVideoPreparedMainThread() {
//                // We hide the cover when video is prepared. Playback is about to start
//                playButton.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onVideoStoppedMainThread() {
//                // We show the cover when video is stopped
//                playButton.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onVideoCompletionMainThread() {
//                // We show the cover when video is completed
//                playButton.setVisibility(View.VISIBLE);
//            }
//        });
//        this.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(videoUrl!=null) {
//                    if (false) {
//                        PlayerManagerUtil.getManager().playNewVideo(null, playerView, videoUrl);
//                    } else {
//                        playerView.stop();
//                    }
//                }
//            }
//        });
    }



}
