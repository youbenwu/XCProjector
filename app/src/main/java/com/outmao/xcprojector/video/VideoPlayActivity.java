package com.outmao.xcprojector.video;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.outmao.xcprojector.databinding.ActivityVideoPlayBinding;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

public class VideoPlayActivity extends AppCompatActivity {

    private ActivityVideoPlayBinding binding;

    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoUrl=getIntent().getStringExtra("url");


        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            SingleVideoPlayerManager manager=new SingleVideoPlayerManager(new PlayerItemChangeListener() {
                @Override
                public void onPlayerItemChanged(MetaData currentItemMetaData) {

                }
            });
            @Override
            public void run() {
                manager.playNewVideo(null,binding.playerView,videoUrl);
            }
        }, 1500);

    }



}