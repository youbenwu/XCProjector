package com.outmao.xcprojector.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.outmao.xcprojector.R;

public class ImageClipView extends RelativeLayout {
    public ImageClipView(Context context) {
        super(context);
    }

    public ImageClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageClipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Resources resources = getResources();
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int mBackgroundColor = resources.getColor(R.color.black);
        PorterDuffXfermode mBitmapfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        int layoutId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawColor(mBackgroundColor);
        //设置镂空的模式
        mPaint.setXfermode(mBitmapfermode);
        canvas.drawCircle(50,50,30,mPaint);
        //清空镂空模式
        mPaint.setXfermode(null);
        canvas.restoreToCount(layoutId);
    }


}
