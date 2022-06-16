package com.basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ScrollTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {

    private static final int FLAG_START_SCROLL = 0;
    private static final int FLAG_STOP_SCROLL = 1;
    private Context mContext;
    private String[] textContent;
    private Handler mHandler;
    private int currentIndex = 0;

    private float mTextSize;
    private int mTextColor;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollTextView);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.ScrollTextView_android_textSize, 14);
        mTextColor = typedArray.getColor(R.styleable.ScrollTextView_android_textColor, Color.parseColor("#000000"));

        mHandler = new MyHandler();
    }

    public void setTextContent(String[] textContent) {
        this.textContent = textContent;
        currentIndex = 0;
    }

    public void setAnimTime(long animDuration) {
        Animation in = AnimationUtils.loadAnimation(mContext, R.anim.scroll_text_view_in);
        in.setDuration(animDuration);
        in.setInterpolator(new AccelerateInterpolator());

        Animation out = AnimationUtils.loadAnimation(mContext, R.anim.scroll_text_view_out);
        out.setDuration(animDuration);
        out.setInterpolator(new AccelerateInterpolator());

        setInAnimation(in);
        setOutAnimation(out);
    }

    public void startScroll() {
        //如果已经存在2个view，说明view已经添加，动画已经在执行了
        if (getChildCount() == 0) {
            setFactory(this);
            mHandler.sendEmptyMessageDelayed(FLAG_START_SCROLL, 3000);
        }
    }

    public void stopScroll() {
        mHandler.sendEmptyMessage(FLAG_STOP_SCROLL);
    }

    @Override
    public View makeView() {
        TextView tv = new TextView(mContext);
        tv.setText(textContent[currentIndex]);
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setTextColor(mTextColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        return tv;
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FLAG_START_SCROLL:
                    currentIndex++;
                    if (currentIndex >= textContent.length) {
                        currentIndex = 0;
                    }
                    setText(textContent[currentIndex]);
                    sendEmptyMessageDelayed(FLAG_START_SCROLL, 5000);
                    break;
                case FLAG_STOP_SCROLL:
                    this.removeMessages(FLAG_START_SCROLL);
                    break;
            }

        }
    }
}
