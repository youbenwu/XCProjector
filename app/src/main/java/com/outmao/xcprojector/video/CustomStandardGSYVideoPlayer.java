package com.outmao.xcprojector.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.outmao.xcprojector.R;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.lang.ref.WeakReference;

import static com.shuyu.gsyvideoplayer.utils.CommonUtil.hideNavKey;


public class CustomStandardGSYVideoPlayer extends StandardGSYVideoPlayer {

    private MyHandler mHandler;

    public CustomStandardGSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CustomStandardGSYVideoPlayer(Context context) {
        super(context);
    }

    public CustomStandardGSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mHandler = new MyHandler(this);
        findViewById(R.id.rl_start).setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.custom_video_layout_standard;
    }



    //在某些不能用ennter键的情况下进行测试
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (mHideKey && mIfCurrentIsFullscreen) {
            hideNavKey(mContext);
        }
        if (i == R.id.rl_start) {
            clickStartIcon();
        }
    }

    @Override
    protected boolean isShowNetConfirm() {
        return false;
    }

    //处理按键快进和快退
    //连续按下左右键
    private void keyMove() {
        if (mSeekTimePosition >= getDuration()) {
            mSeekTimePosition = getDuration();
            cancelProgressTimer();
            if (mProgressBar != null) {
                mProgressBar.setProgress(100);
            }
            if (mCurrentTimeTextView != null && mTotalTimeTextView != null) {
                mCurrentTimeTextView.setText(mTotalTimeTextView.getText());
            }
            if (mBottomProgressBar != null) {
                mBottomProgressBar.setProgress(100);
            }
            findViewById(R.id.thumb).setVisibility(View.VISIBLE);
            mBottomContainer.setVisibility(GONE);
        } else {
            mBottomContainer.setVisibility(VISIBLE);
            long progress = mSeekTimePosition * 100 / (getDuration() == 0 ? 1 : getDuration());
            if (mProgressBar != null)
                mProgressBar.setProgress((int) progress);

//            if (mCurrentTimeTextView != null) {
//                mCurrentTimeTextView.setText(CommonUtil.stringForTime(progress * getDuration() / 100));
//            }
            if (mBottomProgressBar != null) {
                mBottomProgressBar.setProgress((int) progress);
            }

            getGSYVideoManager().seekTo(mSeekTimePosition);
            if (System.currentTimeMillis()-lastTime>1000){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBottomContainer.setVisibility(GONE);
                    }
                }, 500);
            }
            lastTime=System.currentTimeMillis();
            Log.d("mz--", "keyMove:" + mSeekTimePosition + ":" + getDuration() + ":" + mSeekTimePosition * 100 / getDuration());
        }
    }

    //定义变量
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int CANCLE = 2;
    // TODO: 2023/2/23 判断是否连续点击快进键，避免控件频繁的visible和gone
    private static  long lastTime=0;


    //程序启动时，初始化并发送消息
    // TODO: 2022/12/20 用 CustomStandardGSYVideoPlayer的弱引用，防止内存泄泄露
    private static class MyHandler extends Handler {
        private WeakReference<CustomStandardGSYVideoPlayer> weakReference;

        public MyHandler(CustomStandardGSYVideoPlayer videoPlayer) {
            weakReference = new WeakReference<>(videoPlayer);
        }

        @Override
        public void handleMessage(Message msg) {
            CustomStandardGSYVideoPlayer videoPlayer = weakReference.get();
            videoPlayer.mSeekTimePosition= (int) videoPlayer.getGSYVideoManager().getCurrentPosition();
            if (System.currentTimeMillis()-lastTime>1000){
                videoPlayer.mBottomContainer.setVisibility(VISIBLE);
            }
            switch (msg.what) {
                case LEFT:
                    if (videoPlayer.mSeekTimePosition > 2000) {
                        videoPlayer.mSeekTimePosition -= 2000;
                    } else {
                        videoPlayer.mSeekTimePosition = 0;
                    }
                    videoPlayer.keyMove();
                    break;
                case RIGHT:
                    if (videoPlayer.mSeekTimePosition < videoPlayer.getDuration()) {
                        videoPlayer.mSeekTimePosition += 2000;
                    } else {
                        videoPlayer.mCurrentState = CURRENT_STATE_AUTO_COMPLETE;
                        videoPlayer.mSeekTimePosition = videoPlayer.getDuration();
                        videoPlayer.cancelProgressTimer();
                        if (videoPlayer.mProgressBar != null) {
                            videoPlayer.mProgressBar.setProgress(100);
                        }
                        if (videoPlayer.mCurrentTimeTextView != null && videoPlayer.mTotalTimeTextView != null) {
                            videoPlayer.mCurrentTimeTextView.setText(videoPlayer.mTotalTimeTextView.getText());
                        }
                        if (videoPlayer.mBottomProgressBar != null) {
                            videoPlayer.mBottomProgressBar.setProgress(100);
                        }

                        videoPlayer.changeUiToCompleteShow();
                        videoPlayer.cancelDismissControlViewTimer();

                        videoPlayer.findViewById(R.id.thumb).setVisibility(View.VISIBLE);
                        videoPlayer.mBottomContainer.setVisibility(GONE);
                        return;
                    }
                    videoPlayer.keyMove();
                    break;
                case CANCLE:                        //停止按键
                    videoPlayer.onStopTrackingTouch(videoPlayer.mProgressBar);
                    videoPlayer.mBottomContainer.setVisibility(GONE);
                    videoPlayer.startDismissControlViewTimer();
                    videoPlayer.touchSurfaceUp();
                    videoPlayer.startProgressTimer();
                    //不要和隐藏虚拟按键后，滑出虚拟按键冲突
                    if (videoPlayer.mHideKey && videoPlayer.mShowVKey) {
                        // return true;
                    }
                    break;
            }
        }
    }

    @Override
    protected void dismissProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnkeyDown(int keyCode, KeyEvent event) {
        Log.d("mz--", "setOnkeyDown: " + keyCode);
        switch (keyCode) {
            case KeyEvent.ACTION_DOWN:
                break;
            case KeyEvent.ACTION_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mHandler.sendEmptyMessage(LEFT);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mHandler.sendEmptyMessage(RIGHT);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (mSeekTimePosition >= getDuration()) {
                    mSeekTimePosition = 0;
                }
                Log.d("mz--", "mSeekTimePosition: " + mSeekTimePosition);
                clickStartIcon();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        mSeekTimePosition = 0;
    }
}
