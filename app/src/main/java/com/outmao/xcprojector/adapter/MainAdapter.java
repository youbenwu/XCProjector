package com.outmao.xcprojector.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.outmao.xcprojector.R;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;

import java.util.ArrayList;
import java.util.List;

/***
 * @Author : DT
 * @CreateDate : 2023/9/6 17:55
 * @Description : description
 */
public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    // 数据使用
    private List<Integer> mList;

    private int TYPE_VIDEO_ITEM = 110;
    private int TYPE_IMAGE_ITEM = 1;

    private Context mContext;

    private GSYVideoHelper smallVideoHelper;
    public MainAdapter(GSYVideoHelper smallVideoHelper) {
        this.smallVideoHelper = smallVideoHelper;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if(viewType == TYPE_VIDEO_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.video_image_item, parent, false);
            return new MainItemViewHolder(view);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.two_image_item, parent, false);
        return new MainItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == TYPE_VIDEO_ITEM) {
            View videoView = holder.itemView.findViewById(R.id.video_view);
            // 播放视频
//            smallVideoHelper.addVideoPlayer(position, , TAG, holder.videoContainer, holder.playerBtn);
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    smallVideoHelper.setPlayPositionAndTag(position, TAG);
//                    final String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
//                    gsySmallVideoHelperBuilder.setVideoTitle("title " + position)
//                            .setUrl(url);
                    smallVideoHelper.startPlay();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_VIDEO_ITEM;
        } else if(position==1){
            return TYPE_IMAGE_ITEM;
        } else {
            return super.getItemViewType(position);
        }

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public void setData(List<Integer> list) {
        this.mList = list;
    }

    static class MainItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MainItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // tv = itemView.findViewById(R.id.recycler_item_test);
        }

    }

}
