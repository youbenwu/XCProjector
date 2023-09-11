package com.outmao.xcprojector.video;

import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

public class PlayerManagerUtil {

    private static VideoPlayerManager manager;

    public static VideoPlayerManager getManager(){
        if(manager==null){
            manager=new SingleVideoPlayerManager(new PlayerItemChangeListener() {
                @Override
                public void onPlayerItemChanged(MetaData currentItemMetaData) {

                }
            });
        }
        return manager;
    }





}
