package com.basic.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;

import com.common.base.simple.SimpleAnimatorListener;


public class ExpandableTextView extends AppCompatTextView implements View.OnClickListener {
    private final int                       duration = 400;
    private       boolean                   exceedLimit;
    private       int                       limit;
    private       ValueAnimator             mAnimator;
    private       int                       maxLines;
    private       int                       limitHeight;
    private       int                       fullHeight;
    private       onExceedLimitLineListener mCallback;
    private       AnimationListener         mAnimationListener;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

    public void setCallback(onExceedLimitLineListener callback) {
        mCallback = callback;
    }

    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }

    public void setLimit(int limitLine) {
        this.limit = limitLine;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        setMaxLines(limit);
        super.setText(text, type);
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                maxLines = getLineCount();
                if (maxLines > limit) {
                    exceedLimit = true;
                } else {
                    exceedLimit = false;
                }
                if (mCallback != null) {
                    mCallback.onExceedLimitLine(exceedLimit);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (exceedLimit && mAnimator == null) {
            if (getMaxLines() == limit) {
                open();
            } else {
                close();
            }
        }
    }

    public void open() {
        if (getMaxLines() == limit) {
            limitHeight = getHeight();
            mAnimator = ValueAnimator.ofInt(limitHeight, fullHeight == 0
                    ? maxLines * getLineHeight()
                    : fullHeight);
            mAnimator.setDuration(duration);
            mAnimator.addUpdateListener(mUpdateListener);
            mAnimator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setMaxLines(Integer.MAX_VALUE);
                    dispatchAnimationStart(true);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator = null;
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    setLayoutParams(layoutParams);
                    dispatchAnimationEnd(true);
                }
            });
            mAnimator.start();
        }
    }

    public void close() {
        if (getMaxLines() > limit) {
            if (fullHeight != getHeight()) {
                fullHeight = getHeight();
            }
            mAnimator = ValueAnimator.ofInt(fullHeight, limitHeight);
            mAnimator.setDuration(duration);
            mAnimator.addUpdateListener(mUpdateListener);
            mAnimator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    dispatchAnimationStart(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator = null;
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    setLayoutParams(layoutParams);
                    setMaxLines(limit);
                    dispatchAnimationEnd(false);
                }
            });
            mAnimator.start();
        }
    }

    private void dispatchAnimationStart(boolean expand) {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationStart(expand, duration);
        }
    }

    private void dispatchAnimationEnd(boolean expand) {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationEnd(expand);
        }
    }

    private ValueAnimator.AnimatorUpdateListener mUpdateListener = animation -> {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = (int) animation.getAnimatedValue();
        setLayoutParams(layoutParams);
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
        mAnimator = null;
    }

    public interface AnimationListener {
        void onAnimationStart(boolean expand, long duration);

        void onAnimationEnd(boolean expand);
    }

    interface onExceedLimitLineListener {
        void onExceedLimitLine(boolean exceedLimit);
    }
}
