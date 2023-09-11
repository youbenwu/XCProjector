package com.outmao.xcprojector.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.outmao.xcprojector.R;

public class ImageClipView2 extends androidx.appcompat.widget.AppCompatImageView {
    public ImageClipView2(Context context) {
        super(context);
        init();
    }

    public ImageClipView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageClipView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private Bitmap bitmap;
    private void init(){
        bitmap=getBitmap();
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Resources resources = getResources();
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_play_mb2);
        int mBackgroundColor = resources.getColor(R.color.black);
        PorterDuffXfermode mBitmapfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        int layoutId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawColor(mBackgroundColor);
        canvas.drawBitmap(bitmap,0,0,null);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img_test1);
        //设置镂空的模式
        mPaint.setXfermode(mBitmapfermode);
        canvas.drawBitmap(bitmap,0,0,null);
        //清空镂空模式
        mPaint.setXfermode(null);
        canvas.restoreToCount(layoutId);
    }

    private Bitmap getBitmap(){
        DisplayMetrics dm = getResources().getDisplayMetrics();

        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.ic_play_mb2);
        Bitmap mBitmap = Bitmap.createScaledBitmap(bmp, mScreenWidth, mScreenHeight, true);
        return mBitmap;
    }

}
