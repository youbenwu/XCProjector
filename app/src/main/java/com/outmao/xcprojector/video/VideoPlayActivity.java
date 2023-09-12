package com.outmao.xcprojector.video;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.outmao.xcprojector.HomeFragment;
import com.outmao.xcprojector.MainMenusFragment;
import com.outmao.xcprojector.R;
import com.outmao.xcprojector.databinding.ActivityVideoPlayBinding;
import com.outmao.xcprojector.views.MenuItemView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

public class VideoPlayActivity extends AppCompatActivity {

    private ActivityVideoPlayBinding binding;

    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoUrl=getIntent().getStringExtra("url");
        initVideoPlayer();
    }


    private void initVideoPlayer(){
        binding.videoPlayer.setUp(videoUrl,true,"");
        binding.videoPlayer.startPlayLogic();

    }



    @Override
    protected void onPause() {
        super.onPause();
        binding.videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){
//            backward(5000);
//        }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){
//            forward(5000);
//        }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP){
//
//
//
//        }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN){
//
//
//        } else if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
//
//            return false;
//        }

        return super.onKeyDown(keyCode, event);
    }

    //快进
    private void forward(int s){
        binding.videoPlayer.showContextMenu();
        long duration=binding.videoPlayer.getDuration();
        Log.d("duration:",duration+"");
        int p=binding.videoPlayer.getPlayPosition();
        Log.d("getPlayPosition:",p+"");
        int newP=p+s>=duration?(int)duration:p+s;
        Log.d("newP:",newP+"");
        binding.videoPlayer.seekTo(newP);
    }
    private void backward(int s){
        long duration=binding.videoPlayer.getDuration();
        int p=binding.videoPlayer.getPlayPosition();
        if(p-s>0){
            binding.videoPlayer.setPlayPosition(p-s);
        }else{
            binding.videoPlayer.setPlayPosition(0);
        }
    }



}