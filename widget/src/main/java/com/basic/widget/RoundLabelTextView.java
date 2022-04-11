package com.basic.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 *
 * <pre>
 * ===========================================
 * 作    者 : xiexin
 * 邮    箱 : jobs_xie@enable-ets.com
 * 版    本 : 1.0
 * 创建日期 : 2019-07-23
 * 更新日期 : 2019-07-23
 * 描    述 : 带圆角功能的三角标签
 * 参    考 : <a href="https://github.com/NamelessPeople/RoundLabelTextView">RoundLabelTextView</a>
 * ===========================================
 * </pre>
 */
public class RoundLabelTextView extends View {

    public static final int ANGLE_LEFT_TOP = 0;
    public static final int ANGLE_RIGHT_TOP = 90;
    public static final int ANGLE_LEFT_BOTTOM = -90;
    public static final int ANGLE_RIGHT_BOTTOM = 180;

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_ITALIC = 1;
    public static final int STYLE_BOLD = 2;

    /** 标签的背景颜色 */
    private int mLabelBackgroundColor;
    /** 标签的尺寸(长宽一致) */
    private float mLabelSize;
    /** 位于哪个角 */
    private int mOrientation;
    /** 圆角半径 */
    private float mRadius;

    /** Content文字(主要内容) */
    private String mContentText;

    private int mContentTextColor;
    private float mContentTextSize;
    private float mContentMarginStart;
    private float mContentMarginBottom;
    private int mContentTextStyle;

    /** 顶部文字(附加标题) */
    private String mTopText;

    private int mTopTextColor;
    private float mTopTextSize;
    private float mTopMarginStart;
    private float mTopMarginBottom;
    private int mTopTextStyle;

    /** 画笔 */
    private final Paint mPaint = new Paint();
    /** 背景的路径 */
    private final Path mBackgroundPath = new Path();
    /** 圆角矩形圆角的位置 */
    private final float[] mRoundRadii = new float[8];
    /** 圆角矩形的尺寸 */
    private final RectF mRoundRectF = new RectF();
    /** 中心点坐标 */
    private float mCenterX, mCenterY;

    public RoundLabelTextView(Context context) {
        this(context, null);
    }

    public RoundLabelTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundLabelTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundLabelTextView(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundLabelTextView);
            mLabelBackgroundColor =
                    typedArray.getColor(
                            R.styleable.RoundLabelTextView_labelBackgroundColor, Color.TRANSPARENT);
            mLabelSize =
                    typedArray.getDimension(R.styleable.RoundLabelTextView_labelSize, dp2px(20));
            mOrientation =
                    typedArray.getInt(R.styleable.RoundLabelTextView_orientation, ANGLE_LEFT_TOP);
            mRadius = typedArray.getDimension(R.styleable.RoundLabelTextView_radius, 0);

            mContentText = typedArray.getString(R.styleable.RoundLabelTextView_contentText);
            mContentTextColor =
                    typedArray.getColor(
                            R.styleable.RoundLabelTextView_contentTextColor, Color.BLACK);
            mContentTextSize =
                    typedArray.getDimension(
                            R.styleable.RoundLabelTextView_contentTextSize, sp2px(15));
            mContentMarginStart =
                    typedArray.getDimension(R.styleable.RoundLabelTextView_contentMarginStart, 0);
            mContentMarginBottom =
                    typedArray.getDimension(R.styleable.RoundLabelTextView_contentMarginBottom, 0);
            mContentTextStyle =
                    typedArray.getInt(
                            R.styleable.RoundLabelTextView_contentTextStyle, STYLE_NORMAL);

            mTopText = typedArray.getString(R.styleable.RoundLabelTextView_topText);
            mTopTextColor =
                    typedArray.getColor(
                            R.styleable.RoundLabelTextView_topTextColor, mContentTextColor);
            mTopTextSize =
                    typedArray.getDimension(R.styleable.RoundLabelTextView_topTextSize, sp2px(12));
            mTopMarginStart =
                    typedArray.getDimension(R.styleable.RoundLabelTextView_topMarginStart, 0);
            mTopMarginBottom =
                    typedArray.getDimension(R.styleable.RoundLabelTextView_topMarginBottom, 0);
            mTopTextStyle =
                    typedArray.getInt(R.styleable.RoundLabelTextView_topTextStyle, STYLE_NORMAL);

        } finally {
            if (typedArray != null) typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) mLabelSize, (int) mLabelSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mCenterX = mCenterY = mLabelSize / 2;
        drawBackground(canvas); // 绘制背景
        drawText(canvas, true); // 绘制content文本(一定要在top之前绘制)
        drawText(canvas, false); // 绘制top文本
        canvas.restore();

//        LogUtils.d("drawBackground: getMatrix=" + getMatrix());
//        LogUtils.d("drawBackground: canvas.getMatrix()=" + canvas.getMatrix());
    }

    /** 绘制背景 */
    private void drawBackground(Canvas canvas) {

        mPaint.reset();
        mBackgroundPath.reset();
        mRoundRectF.setEmpty();

        mPaint.setColor(mLabelBackgroundColor); // 设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL); // 设置画笔模式为填充

        // 绘制圆角
        mRoundRadii[0] = mRoundRadii[1] = mRadius;
        mRoundRectF.set(0F, 0F, mRadius, mRadius);
        mBackgroundPath.addRoundRect(mRoundRectF, mRoundRadii, Path.Direction.CCW);

        // 绘制三角形
        mBackgroundPath.moveTo(0, mRadius);
        mBackgroundPath.lineTo(0, mLabelSize);
        mBackgroundPath.lineTo(mLabelSize, 0);
        mBackgroundPath.lineTo(mRadius, 0);
        mBackgroundPath.close();

        // 旋转画布
        canvas.rotate(mOrientation, mCenterX, mCenterY);
        // 绘制
        canvas.drawPath(mBackgroundPath, mPaint);
    }

    /** 绘制文本 */
    private void drawText(Canvas canvas, boolean isContentText) {
        mPaint.reset();
        mPaint.setAntiAlias(true); // 抗锯齿

        // 获取需要绘制的文本
        String text = isContentText ? mContentText : mTopText;
        if (text == null) text = "";

        // 设置文本绘制样式
        switch (isContentText ? mContentTextStyle : mTopTextStyle) {
            case STYLE_ITALIC:
                mPaint.setTypeface(Typeface.SANS_SERIF);
                break;
            case STYLE_BOLD:
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            default:
                mPaint.setTypeface(Typeface.DEFAULT);
                break;
        }
        mPaint.setColor(isContentText ? mContentTextColor : mTopTextColor); // 文本颜色
        mPaint.setTextSize(isContentText ? mContentTextSize : mTopTextSize); // 文本大小
        mPaint.setTextAlign(Paint.Align.CENTER); // 文字居中对齐
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight = isContentText ? mContentTextSize : mTopTextSize;
        // 获取初始文本坐标
        float x = mCenterX;
        float y = mCenterY + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        /* 标签文本是否需要翻转(标签有四个角, 顶部显示时不需要翻转, 底部显示需要翻转) */
        boolean textFlip = mOrientation == ANGLE_LEFT_BOTTOM || mOrientation == ANGLE_RIGHT_BOTTOM;
        // 文本是否需要旋转
        if (textFlip) {
            if (isContentText) {
                canvas.rotate(135, mCenterX, mCenterY);
                canvas.translate(0, textHeight / 2);
            } else {
                canvas.translate(0, mContentTextSize);
            }
            y += isContentText ? mContentMarginBottom : mTopMarginBottom + mContentMarginBottom;
        } else {
            if (isContentText) {
                canvas.rotate(-45, mCenterX, mCenterY);
                canvas.translate(0, -textHeight / 2);
            } else {
                canvas.translate(0, -mContentTextSize);
            }
            y -= isContentText ? mContentMarginBottom : mTopMarginBottom + mContentMarginBottom;
        }
        x += isContentText ? mContentMarginStart : mTopMarginStart;
        canvas.drawText(text, x, y, mPaint);
    }

    private int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int sp2px(float sp) {
        final float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        this.setLabelBackgroundColor(color);
    }

    @Override
    @Deprecated
    public void setBackgroundResource(int resId) {
        throw new UnsupportedOperationException(
                "disable this feature, use setLabelBackgroundColor()");
    }

    @Override
    @Deprecated
    public void setBackground(Drawable background) {
        throw new UnsupportedOperationException(
                "disable this feature, use setLabelBackgroundColor()");
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        throw new UnsupportedOperationException(
                "disable this feature, use setLabelBackgroundColor()");
    }

    public void setLabelBackgroundColor(@ColorInt int color) {
        mLabelBackgroundColor = color;
    }

    /** 设置标签的大小, 设置完成之后会调用一次布局绘制 */
    public void setLabelSize(float px) {
        mLabelSize = px;
        requestLayout();
    }

    /** 设置圆角弧度 */
    public void setRadius(float radius) {
        mRadius = radius;
    }

    /** 设置标签方向(以顶点为准) */
    public void setOrientation(@Angle int orientation) {
        this.mOrientation = orientation;
    }

    /** 设置Top文字(靠近顶点的文字, 副标题) */
    public void setTopText(@StringRes int id) {
        setTopText(getContext().getString(id));
    }

    /** 设置Top文字(靠近顶点的文字, 副标题) */
    public void setTopText(String text) {
        mTopText = text;
    }

    public void setTopTextColor(@ColorInt int color) {
        mTopTextColor = color;
    }

    public void setTopTextSize(float size) {
        mTopTextSize = size;
    }

    public void setTopMarginStart(float margin) {
        mTopMarginStart = margin;
    }

    public void setTopMarginBottom(float margin) {
        mTopMarginBottom = margin;
    }

    public void setTopTextStyle(@TextStyle int style) {
        mTopTextStyle = style;
    }

    public void setContentText(@StringRes int id) {
        setContentText(getContext().getString(id));
    }

    public void setContentText(String text) {
        mContentText = text;
    }

    public void setContentTextColor(@ColorInt int color) {
        mContentTextColor = color;
    }

    public void setContentTextSize(float size) {
        mContentTextSize = size;
    }

    public void setContentMarginStart(float margin) {
        mContentMarginStart = margin;
    }

    public void setContentMarginBottom(float margin) {
        mContentMarginBottom = margin;
    }

    public void setContentTextStyle(@TextStyle int style) {
        mContentTextStyle = style;
    }

    /** 设置完成之后需要调用方法刷新, 进行重绘, 否则变更不会生效 */
    public void refresh() {
        postInvalidate();
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef({ANGLE_LEFT_TOP, ANGLE_RIGHT_TOP, ANGLE_LEFT_BOTTOM, ANGLE_RIGHT_BOTTOM})
    private @interface Angle {}

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef({STYLE_NORMAL, STYLE_ITALIC, STYLE_BOLD})
    private @interface TextStyle {}
}
