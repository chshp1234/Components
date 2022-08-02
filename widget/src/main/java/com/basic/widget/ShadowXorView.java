package com.basic.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ShadowXorView extends View {

    RectF mSrcRect, mBigRect, mSmallRect;
    Paint mPaint;
    Paint mPaintIn;
    float mStart = 0, mEnd = 0, mRadius = 0;
    int strokeWidth = 7;
    private boolean isCircle = true;
    PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setColor(Color.parseColor("#57effb"));
        mPaintIn = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaintIn.setColor(Color.parseColor("#cc000000"));
    }


    public void setCircle(float start, float end, float radius) {
        isCircle = true;
        mStart = start + radius;
        mEnd = end + radius;
        mRadius = radius;
        invalidate();
    }

    public void setCircleCenter(float centerX, float centerY, float radius) {
        isCircle = true;
        mStart = centerX;
        mEnd = centerY;
        mRadius = radius;
        invalidate();
    }

    public void setRectangle(float x, float y, float w, float h, float radius) {
        isCircle = false;
        mBigRect = new RectF(x, y, x + w, y + h);
        mSmallRect = new RectF(x + strokeWidth, y + strokeWidth, x + w - strokeWidth,
                y + h - strokeWidth);
        mRadius = radius;
        invalidate();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public ShadowXorView(Context context) {
        super(context);
    }

    public ShadowXorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowXorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShadowXorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将绘制操作保存到新的图层，因为图像合成是很昂贵的操作，将用到硬件加速，这里将图像合成的处理放到离屏缓存中进行
        int saveCount = canvas.saveLayer(mSrcRect, mPaint, Canvas.ALL_SAVE_FLAG);
        //绘制目标图
        canvas.drawRect(mSrcRect, mPaintIn);
        if (isCircle) {
            canvas.drawCircle(mStart, mEnd, mRadius, mPaint);
        } else {
            canvas.drawRoundRect(mBigRect, mRadius, mRadius, mPaint);
        }
        //设置混合模式
        mPaint.setXfermode(mXfermode);
        //绘制源图
        if (isCircle) {
            canvas.drawCircle(mStart, mEnd, mRadius - strokeWidth, mPaint);
        } else {
            canvas.drawRoundRect(mSmallRect, mRadius, mRadius, mPaint);
        }
        //清除混合模式
        mPaint.setXfermode(null);
        //还原画布
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSrcRect = new RectF(0, 0, w, h);
    }
}
