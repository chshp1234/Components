package com.basic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class AutoVerticalRollRecyclerView extends RecyclerView implements Runnable {
    private boolean isScroll = false;
    private int scrollItemHeight = 0;
    private static final int scrollDelay = 3000;
    private static final int delay = 15;
    private int firstIndex;

    public AutoVerticalRollRecyclerView(@NonNull Context context) {
        super(context);
    }

    public AutoVerticalRollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoVerticalRollRecyclerView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //        startAutoScroll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //        stopAutoScroll();
    }

    /** 开始滑动 */
    public void startAutoScroll() {
        // 已经滑动时/没有设置适配器时直接返回
        if (isScroll || getAdapter() == null) return;
        isScroll = true;
        postDelayed(this, scrollDelay);
    }

    /** 停止滑动 */
    public void stopAutoScroll() {
        // 已经滑动时/没有设置适配器时直接返回
        if (!isScroll) return;
        isScroll = false;
        removeCallbacks(this);
    }

    @Override
    public void run() {
        if (this.isScroll) {
            if (getAdapter() == null || getChildAt(0) == null || getAdapter().getItemCount() <= 1) {
//                LogUtils.w(
//                        "adapter==null:"
//                                + (getAdapter() == null)
//                                + "\n"
//                                + "childAt(0)==null:"
//                                + (getChildAt(0) == null)
//                                + "\n"
//                                + "itemCount:"
//                                + (getAdapter() == null ? -1 : getAdapter().getItemCount()));
                stopAutoScroll();
            } else {
                if (scrollItemHeight < getChildAt(0).getHeight()) {
                    scrollBy(0, 1);
                    scrollItemHeight++;
                    postDelayed(this, delay);
                } else {
                    scrollItemHeight = 0;
                    getAdapter().notifyItemRangeChanged(firstIndex, 1);
                    postDelayed(this, scrollDelay);
                }
            }
        }
    }

    /**
     * 事件触摸： 1.若让滑动不受用户触摸影响，直接返回false，表示不处理事件
     *
     * <p>2.若需要在用户触摸时停止，用户离开时开始，只需要根据情况触摸事件进行处理即可
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }
}
