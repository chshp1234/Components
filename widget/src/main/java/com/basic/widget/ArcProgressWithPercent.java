package com.basic.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 数字圆环进度
 */
public class ArcProgressWithPercent extends ArcProgress {
    private Paint mPaint;
    private Paint mBackgroundPaint;

    private boolean paused;

    public ArcProgressWithPercent(Context context) {
        this(context, null);
    }

    public ArcProgressWithPercent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressWithPercent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                getResources().getDisplayMetrics()));
        mPaint.setColor(Color.WHITE);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.parseColor("#74000000"));
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        setOnCenterDraw(new OnCenterDraw() {
            @Override
            public void draw(Canvas canvas,
                             RectF rectF,
                             float x,
                             float y,
                             float strokeWidth,
                             int progress) {
                if (!paused) {
                    String progressStr = String.valueOf(progress + "%");
                    canvas.drawCircle(rectF.centerX(), rectF.centerY(), getRadius() - strokeWidth
                            , mBackgroundPaint);
                    float textX = x - (mPaint.measureText(progressStr) / 2);
                    float textY = y - ((mPaint.descent() + mPaint.ascent()) / 2);
                    canvas.drawText(progressStr, textX, textY, mPaint);
                }
            }
        });
    }

    public void setPaused(boolean paused) {
        if (this.paused ^ paused) {
            this.paused = paused;
            invalidate();
        }
    }
}
