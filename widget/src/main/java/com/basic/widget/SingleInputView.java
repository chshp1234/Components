package com.basic.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.common.base.simple.SimpleAnimatorListener;
import com.common.utils.Converter;

public class SingleInputView extends View implements View.OnClickListener {
    private Paint mPaint;
    private int   itemMargin;
    private int   textColor;
    private int   backColor;
    private int   animateColor;
    private int   itemWidth;
    private int   itemCount;
    private int   backHeight;
    private int   itemPadding;
    private int   shakeExtra;

    private float         baseline;
    private String[]      mDigits;
    private boolean       fired;
    private ValueAnimator mAnimator;

    private boolean animating;
    private int     shakePosition;

    private int      activeColor;//输入状态时颜色
    private int      cursorColor;//光标颜色
    private int      currentSelectedIndex;//当前输入位置
    private Handler  cursorHandler;
    private Runnable cursorRunnable;
    private boolean  showCursor;

    private OnInputDownListener mCallback;
    private ResultReceiver      mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case 0:
                    System.out.println("RESULT_UNCHANGED_SHOWN");
                    break;
                case 1:
                    System.out.println("RESULT_UNCHANGED_HIDDEN");
                    break;
                case 2:
                    System.out.println("RESULT_SHOWN");
                    break;
                case 3:
                    System.out.println("RESULT_HIDDEN");
                    break;
            }
        }
    };

    public SingleInputView(Context context) {
        this(context, null);
    }

    public SingleInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(Converter.sp2px(38));
        mPaint.setStrokeWidth(Converter.dip2px(1));
        mPaint.setStyle(Paint.Style.FILL);

        textColor = Color.parseColor("#333333");
        backColor = Color.parseColor("#aaaaaa");
        animateColor = Color.RED;
        itemWidth = Converter.dip2px(45);
        itemMargin = Converter.dip2px(25);
        backHeight = Converter.dip2px(2);
        itemPadding = Converter.dip2px(4);

        baseline = -mPaint.getFontMetrics().top;

        itemCount = 4;

        mDigits = new String[itemCount];

        shakeExtra = 20;

        setFocusable(true);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setFocusedByDefault(true);
        }

        super.setOnClickListener(this);

        cursorColor = Color.parseColor("#29cca6");
        activeColor = Color.parseColor("#29cca6");
        currentSelectedIndex = 0;
        showCursor = true;
        cursorRunnable = new Runnable() {
            @Override
            public void run() {
                showCursor = !showCursor;
                invalidate();
                cursorHandler.postDelayed(this, 500);
            }
        };
        cursorHandler = new Handler();
        cursorHandler.postDelayed(cursorRunnable, 500);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        setMeasuredDimension(
                MeasureSpec.makeMeasureSpec(itemWidth * itemCount + itemMargin * (itemCount - 1) + 2 * shakeExtra, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) (fontMetrics.bottom - fontMetrics.top + backHeight + itemPadding), MeasureSpec.EXACTLY)
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            for (int i = mDigits.length - 1; i >= 0; i--) {
                if (mDigits[i] != null) {
                    currentSelectedIndex = i;
                    mDigits[i] = null;
                    break;
                }
            }
        } else if (NumberMatcher.match(event)) {
            for (int i = 0; i < mDigits.length; i++) {
                if (mDigits[i] == null) {
                    currentSelectedIndex = i + 1;
                    mDigits[i] = String.valueOf(event.getNumber());
                    if (i == mDigits.length - 1 && mCallback != null) {
                        mCallback.OnInputDown(format());
                    }
                    break;
                }
            }
        } else {
            return false;
        }
        invalidate();
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (isEnabled()) {
            outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;
            outAttrs.imeOptions = EditorInfo.IME_NULL;
            return new BaseInputConnection(this, false);
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!fired) {
            fired = true;
            onClick(this);
        }
        for (int i = 0; i < mDigits.length; i++) {
            boolean empty = TextUtils.isEmpty(mDigits[i]);

            if (animating) {
                mPaint.setColor(animateColor);
            } else {
                if (currentSelectedIndex == i) {
                    mPaint.setColor(activeColor);
                } else {
                    mPaint.setColor(empty ? backColor : textColor);
                }
            }
            int y = (int) (getHeight() - mPaint.getStrokeWidth() / 2);
            int xs = i * (itemWidth + itemMargin) + shakeExtra;
            int xs2 = xs - (animating ? shakePosition : 0);
            canvas.drawLine(xs2, y, xs2 + itemWidth, y, mPaint);

            float textWidth = 0;
            float textHeight = mPaint.getTextSize();
            if (!empty) {
                mPaint.setColor(textColor);
                textWidth = mPaint.measureText(mDigits[i]);
                canvas.drawText(mDigits[i], xs + (itemWidth - textWidth) / 2, baseline, mPaint);
            }

            if (i == currentSelectedIndex && showCursor) {
                mPaint.setColor(cursorColor);
                int cursorX;
                if (mDigits[currentSelectedIndex] != null) {
                    cursorX = xs2 + itemWidth / 2 + (int) textWidth / 2;
                } else {
                    cursorX = xs2 + itemWidth / 2;
                }
                canvas.drawLine(cursorX, (y - textHeight) / 2, cursorX, y - (y - textHeight) / 2,
                        mPaint);
            }
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void onClick(View v) {
        if (findFocus() != this) {
            requestFocus();
        }

        InputMethodManager imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.viewClicked(this);
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED, mResultReceiver);
    }

    public void setOnInputDownListener(OnInputDownListener callback) {
        mCallback = callback;
    }

    public void clear(boolean anim) {
        currentSelectedIndex = 0;
        if (anim) {
            if (mAnimator == null) {
                mAnimator = ValueAnimator.ofInt(-shakeExtra, shakeExtra);
                mAnimator.setRepeatCount(5);
                mAnimator.setRepeatMode(ValueAnimator.REVERSE);
                mAnimator.setInterpolator(new LinearInterpolator());
                mAnimator.setDuration(80);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        shakePosition = (int) animation.getAnimatedValue();
                        invalidate();
                    }
                });
                mAnimator.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        animating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animating = false;
                        clearInternal();
                    }
                });
            }

            if (mAnimator.isRunning()) {
                mAnimator.cancel();
            }
            mAnimator.start();
        } else {
            clearInternal();
        }
    }

    private void clearInternal() {
        for (int i = 0; i < mDigits.length; i++) {
            mDigits[i] = null;
        }
        invalidate();
    }

    private String format() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mDigits.length; i++) {
            builder.append(mDigits[i]);
        }
        return builder.toString();
    }

    /**
     获取输入内容

     @return
     */
    public String getInput() {
        return format();
    }

    /**
     判断是否输入完全

     @return
     */
    public boolean isInputCompleted() {
        boolean isInputCompleted = true;
        for (int i = 0; i < mDigits.length; i++) {
            if (mDigits[i] == null) {
                isInputCompleted = false;
                break;
            }
        }
        return isInputCompleted;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cursorHandler.removeCallbacks(cursorRunnable);
    }

    public static class NumberMatcher {
        private static final char[] mValueMap = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        public static boolean match(char c) {
            for (char value : mValueMap) {
                if (value == c) {
                    return true;
                }
            }
            return false;
        }

        public static boolean match(KeyEvent event) {
            return match(event.getNumber());
        }
    }

    interface OnInputDownListener {
        void OnInputDown(String s);
    }
}
