package com.basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatTextView;

public class MaskableTextView extends AppCompatTextView {
    private       boolean         touchEffect             = true;
    @ColorInt
    private       int             maskColor;
    @ColorInt
    private       int             originalColor;
    @ColorInt
    private       int             originalBgColor;
    private       PorterDuff.Mode maskMode;
    private final PorterDuff.Mode DEFAULT_PORTERDUFF_MODE = PorterDuff.Mode.MULTIPLY;
    private final String          DEFAULT_COLOR           = "#E0E0E0";

    public MaskableTextView(Context context) {
        this(context, null);
    }

    public MaskableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        originalColor = getCurrentTextColor();
        Drawable background = getBackground();
        if (background instanceof ColorDrawable) {
            originalBgColor = ((ColorDrawable) background).getColor();
        }

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // 读取配置
        TypedArray array =
                context.obtainStyledAttributes(attributeSet, R.styleable.MaskableTextView);
        maskColor =
                array.getColor(
                        R.styleable.MaskableTextView_mask_color, Color.parseColor(DEFAULT_COLOR));
        maskMode = getPorterDuffMode(array);
        //        LogUtils.d("init: maskColor=" + maskColor);
        //        LogUtils.d("init: maskMode=" + maskMode);
        array.recycle();
    }

    private PorterDuff.Mode getPorterDuffMode(TypedArray array) {
        switch (array.getInt(R.styleable.MaskableImageView_mask_mode, -1)) {
            case 0:
                return PorterDuff.Mode.CLEAR;
            case 1:
                return PorterDuff.Mode.SRC;
            case 2:
                return PorterDuff.Mode.DST;
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 4:
                return PorterDuff.Mode.DST_OVER;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 6:
                return PorterDuff.Mode.DST_IN;
            case 7:
                return PorterDuff.Mode.SRC_OUT;
            case 8:
                return PorterDuff.Mode.DST_OUT;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 10:
                return PorterDuff.Mode.DST_ATOP;
            case 11:
                return PorterDuff.Mode.XOR;
            case 12:
                return PorterDuff.Mode.ADD;
            case 13:
                return PorterDuff.Mode.MULTIPLY;
            case 14:
                return PorterDuff.Mode.SCREEN;
            case 15:
                return PorterDuff.Mode.OVERLAY;
            case 16:
                return PorterDuff.Mode.DARKEN;
            case 17:
                return PorterDuff.Mode.LIGHTEN;
            default:
                return DEFAULT_PORTERDUFF_MODE;
        }
    }

    @Override
    public void setPressed(boolean pressed) {
        updateView(pressed);
        super.setPressed(pressed);
    }

    /**
     * 根据是否按下去来刷新bg和src created by minghao.zl at 2014-09-18
     *
     * @param pressed
     */
    private void updateView(boolean pressed) {
        // 如果没有点击效果
        if (!touchEffect) {
            return;
        } // end if
        if (pressed) { // 点击
            /** 通过设置滤镜来改变图片亮度@minghao */
            setFilter();
        } else { // 未点击
            removeFilter();
        }
    }

    /** 设置滤镜 */
    private void setFilter() {
        // 先获取设置的src图片
        Drawable drawable = getBackground();
        // 当src图片为Null，设置text颜色
        if (drawable == null) {
            setTextColor(maskColor);
            return;
            //            drawable = getBackground();
        }

        if (drawable instanceof ColorDrawable) {
            Drawable mutate = drawable.mutate();
            ((ColorDrawable) mutate).setColor(maskColor);
        } else {
            // 设置滤镜
            //        drawable.setColorFilter(getContext().getColor(R.color.bg_press),
            // PorterDuff.Mode.MULTIPLY);
            drawable.setColorFilter(new PorterDuffColorFilter(maskColor, maskMode));
            //        drawable.setColorFilter(maskColor, maskMode);
        }
    }

    /** 清除滤镜 */
    private void removeFilter() {
        // 先获取设置的src图片
        Drawable drawable = getBackground();
        // 当src图片为Null，设置text颜色
        if (drawable == null) {
            setTextColor(originalColor);
            return;
        }
        if (drawable instanceof ColorDrawable) {
            Drawable mutate = drawable.mutate();
            ((ColorDrawable) mutate).setColor(originalBgColor);
        } else {
            // 清除滤镜
            //            clearColorFilter();
            drawable.clearColorFilter();
        }
    }

    public boolean isTouchEffect() {
        return touchEffect;
    }

    public void setTouchEffect(boolean touchEffect) {
        this.touchEffect = touchEffect;
    }
}
