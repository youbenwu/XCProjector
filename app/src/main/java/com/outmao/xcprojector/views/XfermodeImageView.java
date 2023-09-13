package com.outmao.xcprojector.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import com.outmao.xcprojector.R;

import androidx.appcompat.widget.AppCompatImageView;

public class XfermodeImageView extends AppCompatImageView {
    private int mViewWidth;
    private int mViewHeight;
    private Paint paint;
    private PorterDuffXfermode xfermode;

    private int dest;

    private Bitmap destBitmap;

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public XfermodeImageView(Context context) {
        this(context, null);
    }

    public XfermodeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.XfermodeImageView);
        dest=typedArray.getResourceId(R.styleable.XfermodeImageView_dest,0);
        init();
    }

    private void init() {
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        if(getDrawable()!=null) {
            // 获取绘制图片对应的 paint
            paint = ((BitmapDrawable) getDrawable()).getPaint();
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        //destBitmap = loadDestBitmap(w,h);

    }

    private Bitmap loadDestBitmap(int w,int h){
        if(dest==0)
            return null;
        float dpi = getResources().getDisplayMetrics().densityDpi;
        float wPx=w * (dpi / 160);
        float hPx=h * (dpi / 160);
        Bitmap destBitmap = BitmapFactory.decodeResource(getResources(),dest);
        Bitmap mBitmap = Bitmap.createScaledBitmap(destBitmap, w, h, true);
        return mBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(dest==0){
            super.onDraw(canvas);
            return;
        }
        if(paint==null){
            if(getDrawable()!=null) {
                // 获取绘制图片对应的 paint
                paint = ((BitmapDrawable) getDrawable()).getPaint();
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
            if(paint==null){
                super.onDraw(canvas);
                return;
            }
        }
        int saveCount = canvas.saveLayer(0, 0, mViewWidth, mViewHeight, null, Canvas.ALL_SAVE_FLAG);
        // 绘制 dst 心形
        /***为什么 paint.setStyle() 放在 onDraw() 中才生效，放在 onSizeChanged() 中进行不能生效***/
        paint.setStyle(Paint.Style.FILL);
        if(destBitmap==null){
            destBitmap=loadDestBitmap(mViewWidth,mViewHeight);
        }
        canvas.drawBitmap(destBitmap,0,0,paint);
        // 将绘制图片对应的 paint Xfermode 设置为 SRC_IN , 重叠的部分显示为 src 图片， dst 中不重叠的部分不变， src 中不重叠的部分显示为透明
        paint.setXfermode(xfermode);
        // 绘制 src 图片
        super.onDraw(canvas);
        paint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }

}
