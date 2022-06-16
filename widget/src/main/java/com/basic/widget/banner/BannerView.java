package com.basic.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.common.utils.Converter;


/**
 * 广告页轮播
 */
public class BannerView extends FrameLayout {
    public static final int MESSAGE_NEXT_ITEM = 1;
    public static final int DELAY = 3000;

    private InfiniteViewPager mViewPager;
    private IndicatorView mIndicatorView;
    private boolean isInTouchMode;
    private boolean isStarted;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (!isInTouchMode && mViewPager.getAdapter() != null && mViewPager.getAdapter().getCount() > 0) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                isStarted = false;
                startTimer();
            }
            return true;
        }
    });

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewPager = new InfiniteViewPager(getContext());
        addView(mViewPager);

        mIndicatorView = new IndicatorView(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = Converter.dip2px(10);
        addView(mIndicatorView, lp);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mIndicatorView.setSelected(position);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isInTouchMode = true;
                stopTimer();
                break;
            case MotionEvent.ACTION_UP:
                isInTouchMode = false;
                startTimer();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public <T> void setAdapter(InfiniteViewPager.InfiniteAdapter<T> adapter) {
        int realCount = adapter.getRealCount();
        mIndicatorView.setIndicators(realCount);
        mViewPager.setAdapter(adapter);
        stopTimer();
        startTimer();
    }

    private void startTimer() {
        if (!isStarted && !isInTouchMode) {
            isStarted = true;
            mHandler.sendEmptyMessageDelayed(MESSAGE_NEXT_ITEM, DELAY);
        }
    }

    private void stopTimer() {
        mHandler.removeMessages(MESSAGE_NEXT_ITEM);
        isStarted = false;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (mViewPager.getRealCount() == 0)
            savedState.position = 0;
        else
            savedState.position = mViewPager.getCurrentItem() % mViewPager.getRealCount();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        SavedState savedState = (SavedState) state;
        int position = savedState.position;
        if (mViewPager.getAdapter() != null) {
            mViewPager.setCurrentItemByRealSize(position);
            mIndicatorView.setSelected(position);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == VISIBLE)
            startTimer();
        else
            stopTimer();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTimer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimer();
    }

    static class SavedState extends BaseSavedState {
        int position;

        public SavedState(Parcelable source) {
            super(source);
        }

        public SavedState(Parcel source) {
            super(source);
            position = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
        }
    }
}
