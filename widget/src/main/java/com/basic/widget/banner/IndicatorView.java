package com.basic.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;


import com.basic.widget.R;
import com.common.utils.Converter;

import java.util.Stack;

/**
 * 首页Banner的页标
 */
public class IndicatorView extends LinearLayout {
    private Stack<View> mViews;//view缓存
    private int indicatorMargin;//每个标签之间的距离
    private int indicatorBackResource;//页标的背景
    private int indicatorWidth;
    private int indicatorHeight;
    private int indicatorSelectedWidth;
    private int indicatorSelectedHeight;
    private ViewPager.OnPageChangeListener mListener;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.IndicatorView);
        indicatorMargin = array.getDimensionPixelSize(R.styleable.IndicatorView_indicatorMargin, Converter.dip2px(5));
        indicatorBackResource = array.getResourceId(R.styleable.IndicatorView_indicatorResource, R.drawable.selector_indicator_bg);
        indicatorWidth = array.getDimensionPixelSize(R.styleable.IndicatorView_indicatorSize, Converter.dip2px(5));
        indicatorHeight = indicatorWidth;
        indicatorSelectedWidth = array.getDimensionPixelSize(R.styleable.IndicatorView_indicatorSelectedSize, Converter.dip2px(8));
        indicatorSelectedHeight = indicatorSelectedWidth;
        array.recycle();

        if (indicatorWidth == 0) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), indicatorBackResource);
            indicatorWidth = drawable.getIntrinsicWidth();
            indicatorHeight = drawable.getIntrinsicHeight();
        }

        if (indicatorWidth == 0)
            throw new IllegalStateException("invalid view size");

        mViews = new Stack<>();
    }

    public void setIndicators(int count) {
        for (int i = 0; i < getChildCount(); i++) {
            mViews.add(getChildAt(i));
        }

        removeAllViews();

        for (int i = 0; i < count; i++) {
            View view = getView(i);
            LayoutParams lp = getChildParams(view);
            lp.leftMargin = i == 0 ? 0 : indicatorMargin;
            addView(view, lp);
        }
    }

    public void setSelected(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int tag = (int) view.getTag();
            boolean selected = position == tag;

            changeViewSize(view, selected);
            view.setSelected(selected);
        }
    }

    public void setupWithPager(ViewPager pager) {
        setIndicators(pager.getAdapter().getCount());
        if (mListener == null) {
            mListener = new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    setSelected(position);
                }
            };
        }
        pager.removeOnPageChangeListener(mListener);
        pager.addOnPageChangeListener(mListener);
        setSelected(0);
    }

    private View getView(int position) {
        View view;
        if (mViews.size() > 0) {
            view = mViews.pop();
        } else {
            view = new View(getContext());
        }
        view.setTag(position);
        view.setBackgroundResource(indicatorBackResource);
        return view;
    }

    private LayoutParams getChildParams(View view) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null)
            params = new LayoutParams(indicatorWidth, indicatorHeight);
        params.width = indicatorWidth;
        params.height = indicatorHeight;
        params.leftMargin = 0;
        return params;
    }

    private void changeViewSize(View view, boolean selected) {
        if (indicatorWidth == indicatorSelectedWidth && indicatorHeight == indicatorSelectedHeight)
            return;

        changeViewSize2(view, selected ? indicatorSelectedWidth : indicatorWidth, selected ? indicatorSelectedHeight : indicatorHeight);
    }

    private void changeViewSize2(View view, int width, int height) {
        if (width == 0 || height == 0)
            return;

        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        if (lp.width != width || lp.height != height) {
            lp.width = width;
            lp.height = height;
            view.setLayoutParams(lp);
        }
    }
}
