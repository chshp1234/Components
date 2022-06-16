package com.basic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 @description viewpager和recyclerview横向滚动同时存在的时候，recyclerview横向滚动到底的时候不想引起viewpager的滚动，使用该组件 **/
public class ViewPagerWithRecyclerView extends RecyclerView {
    private boolean interceptTouch = true;

    public ViewPagerWithRecyclerView(Context context) {
        super(context);
    }

    public ViewPagerWithRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerWithRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (interceptTouch) {
            ViewParent parent = this;
            // 循环查找ViewPager, 请求ViewPager不拦截触摸事件
            while (!((parent = parent.getParent()) instanceof ViewPager)) {
                if (parent == null) {
                    break;
                }
            }

            if (parent != null) {
                //禁用父类拦截事件，即父类不拦截事件，都交由子类处理
                parent.requestDisallowInterceptTouchEvent(true);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    public void setInterceptTouch(boolean interceptTouch) {
        this.interceptTouch = interceptTouch;
    }
}
