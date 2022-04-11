package com.basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class RoundCornerLayout extends FrameLayout {

    private Context     mContext;
    private boolean     isAttached;
    private RectF       pathArc;
    private RectF       viewBound;
    private Path        boundPath;
    private Paint       borderPaint;
    private RoundParams roundParams;
    // 定义 Region，即内容区域
    private Region      mAreaRegion;

    public RoundCornerLayout(Context context) {
        this(context, null);
    }

    public RoundCornerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCornerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        isAttached = false;
        pathArc = new RectF();
        viewBound = new RectF();
        boundPath = new Path();
        borderPaint = new Paint();
        roundParams = new RoundParams();
        mAreaRegion = new Region();

        initRoundCornerAttrs(context, attrs);
    }

    public void initRoundCornerAttrs(@NonNull Context ctx, @NonNull AttributeSet attr) {
        makeVpCanDraw();

        // 读取配置
        TypedArray array = ctx.obtainStyledAttributes(attr, R.styleable.RoundCornerLayout);
        roundParams.roundAsCircle =
                array.getBoolean(
                        R.styleable.RoundCornerLayout_roundAsCircle, RoundParams.DEFAULT_AS_CIRCLE);
        roundParams.roundCornerRadius =
                array.getDimension(
                        R.styleable.RoundCornerLayout_roundedCornerRadius,
                        RoundParams.DEFAULT_RADIUS
                                  );
        roundParams.roundTopLeft =
                array.getDimension(
                        R.styleable.RoundCornerLayout_roundTopLeft,
                        RoundParams.DEFAULT_RADIUS_TOP_LEFT
                                  );
        roundParams.roundTopRight =
                array.getDimension(
                        R.styleable.RoundCornerLayout_roundTopRight,
                        RoundParams.DEFAULT_RADIUS_TOP_RIGHT
                                  );
        roundParams.roundBottomLeft =
                array.getDimension(
                        R.styleable.RoundCornerLayout_roundBottomLeft,
                        RoundParams.DEFAULT_RADIUS_BOTTOM_LEFT
                                  );
        roundParams.roundBottomRight =
                array.getDimension(
                        R.styleable.RoundCornerLayout_roundBottomRight,
                        RoundParams.DEFAULT_RADIUS_BOTTOM_RIGHT
                                  );
        roundParams.roundingBorderWidth =
                array.getDimension(
                        R.styleable.RoundCornerLayout_roundingBorderWidth,
                        RoundParams.DEFAULT_BORDER_WIDTH
                                  );
        roundParams.roundingBorderColor =
                array.getColor(
                        R.styleable.RoundCornerLayout_roundingBorderColor,
                        Color.parseColor(RoundParams.DEFAULT_BORDER_COLOR)
                              );

        if (roundParams.roundTopLeft == RoundParams.DEFAULT_RADIUS_TOP_LEFT) {
            roundParams.roundTopLeft = roundParams.roundCornerRadius;
        }
        if (roundParams.roundTopRight == RoundParams.DEFAULT_RADIUS_TOP_RIGHT) {
            roundParams.roundTopRight = roundParams.roundCornerRadius;
        }
        if (roundParams.roundBottomLeft == RoundParams.DEFAULT_RADIUS_BOTTOM_LEFT) {
            roundParams.roundBottomLeft = roundParams.roundCornerRadius;
        }
        if (roundParams.roundBottomRight == RoundParams.DEFAULT_RADIUS_BOTTOM_RIGHT) {
            roundParams.roundBottomRight = roundParams.roundCornerRadius;
        }

        //        LogUtils.d("initRoundCornerAttrs: roundParams=" + roundParams);

        //        updateView();

        array.recycle();
    }

    private void makeVpCanDraw() {
        setWillNotDraw(false);
    }

    public float getMinSize() {
        int var1 = this.getWidth();
        int var2 = this.getHeight();
        return (float) Math.min(var1, var2);
    }

    public RectF getViewBound() {
        this.viewBound.set(
                (float) this.getPaddingLeft(),
                (float) this.getPaddingTop(),
                (float) (this.getWidth() - this.getPaddingRight()),
                (float) (this.getHeight() - this.getPaddingBottom())
                          );
        return this.viewBound;
    }

    public void setViewBound(RectF viewBound) {
        this.viewBound = viewBound;
    }

    public RoundParams getRoundParams() {
        return roundParams;
    }

    public void setRoundParams(RoundParams roundParams) {
        this.roundParams = roundParams;
        updateView();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        getViewBound();
        //        LogUtils.i("RoundCornerLayout.draw(): getViewBound=" + viewBound);

        canvas.saveLayer(new RectF(0, 0, getWidth(), getHeight()), null, Canvas.ALL_SAVE_FLAG);
        beforeDraw(canvas);

        super.draw(canvas);

        afterDraw(canvas);

        canvas.restore();

        //        LogUtils.d("draw: getMatrix=" + getMatrix());
        //        LogUtils.d("draw: canvas.getMatrix()=" + canvas.getMatrix());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*getViewBound();
        LogUtils.i("draw: getViewBound=" + viewBound);
        beforeDraw(canvas);
        afterDraw(canvas);*/
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mAreaRegion.contains((int) ev.getX(), (int) ev.getY())) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void beforeDraw(Canvas canvas) {
        //        start(canvas);
        obtainBounds();
        applyBound(canvas);
    }

    private void afterDraw(Canvas canvas) {
        completed(canvas);
    }

    private void completed(Canvas canvas) {

        borderPaint.setColor(roundParams.roundingBorderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        //        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(roundParams.roundingBorderWidth * 2);
        // 混合模式为 DST_IN, 即仅显示当前绘制区域和背景区域交集的部分，并仅显示背景内容。
        //        borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawPath(boundPath, borderPaint);

        /*// 描边
        if (roundParams.roundingBorderWidth > 0) {
            borderPaint.setXfermode(null);
            borderPaint.setColor(roundParams.roundingBorderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            //        borderPaint.setStrokeJoin(Paint.Join.ROUND);
            borderPaint.setAntiAlias(true);
            borderPaint.setStrokeWidth(roundParams.roundingBorderWidth * 2);
            // 混合模式为 DST_IN, 即仅显示当前绘制区域和背景区域交集的部分，并仅显示背景内容。
            //        borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawPath(boundPath, borderPaint);
        }
        // 剪裁
        borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        borderPaint.setStrokeWidth(0);
        borderPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(boundPath, borderPaint);*/
        //        canvas.restore();
        boundPath.reset();
    }

    private void start(Canvas canvas) {
        canvas.save();
    }

    private Path obtainBounds() {
        if (ifRoundAsCircle(boundPath)) {
            return boundPath;
        }

        //        LogUtils.i("obtainBounds: roundTopLeft=" + roundParams.roundTopLeft);
        //        LogUtils.i("obtainBounds: roundTopRight=" + roundParams.roundTopRight);
        //        LogUtils.i("obtainBounds: roundBottomLeft=" + roundParams.roundBottomLeft);
        //        LogUtils.i("obtainBounds: roundBottomRight=" + roundParams.roundBottomRight);
        //        LogUtils.i("obtainBounds: viewBound=" + viewBound);

        halfBorderWidth = roundParams.roundingBorderWidth / 2;

        whenTopLeftRound(roundParams.roundTopLeft);
        whenTopRightRound(roundParams.roundTopRight);
        whenBottomRightRound(roundParams.roundBottomRight);
        whenBottomLeftRound(roundParams.roundBottomLeft);
        closePath(roundParams.roundTopLeft);

        Region clip = new Region();
        clip.set(
                this.getPaddingLeft(),
                this.getPaddingTop(),
                (this.getWidth() - this.getPaddingRight()),
                (this.getHeight() - this.getPaddingBottom())
                );
        mAreaRegion.setPath(boundPath, clip);

        return boundPath;
    }

    private void applyBound(Canvas canvas) {
        if (!boundPath.isEmpty()) {
            canvas.clipPath(boundPath);
        }
    }

    private boolean ifRoundAsCircle(Path path) {
        if (roundParams.roundAsCircle) {
            float center = getMinSize() / 2;
            float radius = center;
            if (roundParams.roundingBorderWidth != 0f) {
                radius -= roundParams.roundingBorderWidth;
            }
            path.addCircle(center, center, radius, Path.Direction.CW);
            return true;
        }
        return false;
    }

    float halfBorderWidth;

    private void whenTopLeftRound(float radius) {
        if (radius > 0) {
            pathArc.set(
                    viewBound.left,
                    viewBound.top,
                    viewBound.left + (2 * radius),
                    viewBound.top + (2 * radius)
                       );
            //            LogUtils.d("whenTopLeftRound: pathArc=" + pathArc);
            boundPath.arcTo(pathArc, 180.0F, 90.0F);

            pathArc.set(
                    viewBound.left + halfBorderWidth,
                    viewBound.top + halfBorderWidth,
                    viewBound.left + (2 * radius) + halfBorderWidth,
                    viewBound.top + (2 * radius) + halfBorderWidth
                       );
            //            LogUtils.d("whenTopLeftRound: pathArc=" + pathArc);
        } else {
            boundPath.moveTo(viewBound.left, viewBound.top);
        }
    }

    private void whenTopRightRound(Float radius) {
        if (radius > 0) {
            boundPath.lineTo(viewBound.right - radius, viewBound.top);
            pathArc.set(
                    viewBound.right - (2 * radius),
                    viewBound.top,
                    viewBound.right,
                    viewBound.top + (2 * radius)
                       );
            //            LogUtils.d("whenTopRightRound: pathArc=" + pathArc);
            boundPath.arcTo(pathArc, 270f, 90f);
        } else {
            boundPath.lineTo(viewBound.right, viewBound.top);
        }
    }

    private void whenBottomRightRound(Float radius) {
        if (radius > 0) {
            boundPath.lineTo(viewBound.right, viewBound.bottom - radius);
            pathArc.set(
                    viewBound.right - (2 * radius),
                    viewBound.bottom - (2 * radius),
                    viewBound.right,
                    viewBound.bottom
                       );

            //            LogUtils.d("whenBottomRightRound: pathArc=" + pathArc);
            boundPath.arcTo(pathArc, 0f, 90f);
        } else {
            boundPath.lineTo(viewBound.right, viewBound.bottom);
        }
    }

    private void whenBottomLeftRound(Float radius) {
        if (radius > 0) {
            boundPath.lineTo(viewBound.left + radius, viewBound.bottom);
            pathArc.set(
                    viewBound.left,
                    viewBound.bottom - (2 * radius),
                    viewBound.left + (2 * radius),
                    viewBound.bottom
                       );
            //            LogUtils.d("whenBottomLeftRound: pathArc=" + pathArc);
            boundPath.arcTo(pathArc, 90f, 90f);
        } else {
            boundPath.lineTo(viewBound.left, viewBound.bottom);
        }
    }

    private void closePath(Float radius) {
        if (radius > 0) {
            boundPath.lineTo(viewBound.left, viewBound.top + radius);
        } else {
            boundPath.lineTo(viewBound.left, viewBound.top);
        }
    }

    public static class RoundParams {

        private boolean roundAsCircle;
        private float   roundCornerRadius;
        private float   roundTopLeft;
        private float   roundTopRight;
        private float   roundBottomLeft;
        private float   roundBottomRight;
        private float   roundingBorderWidth;
        private int     roundingBorderColor;

        public static final boolean DEFAULT_AS_CIRCLE           = false;
        public static final float   DEFAULT_RADIUS              = 0;
        public static final float   DEFAULT_RADIUS_TOP_LEFT     = 0;
        public static final float   DEFAULT_RADIUS_TOP_RIGHT    = 0;
        public static final float   DEFAULT_RADIUS_BOTTOM_LEFT  = 0;
        public static final float   DEFAULT_RADIUS_BOTTOM_RIGHT = 0;
        public static final float   DEFAULT_BORDER_WIDTH        = 0;
        public static final String  DEFAULT_BORDER_COLOR        = "#000000";

        public RoundParams() {
            super();
        }

        @Override
        public String toString() {
            return "RoundParams{"
                   + "roundAsCircle="
                   + roundAsCircle
                   + ", roundCornerRadius="
                   + roundCornerRadius
                   + ", roundTopLeft="
                   + roundTopLeft
                   + ", roundTopRight="
                   + roundTopRight
                   + ", roundBottomLeft="
                   + roundBottomLeft
                   + ", roundBottomRight="
                   + roundBottomRight
                   + ", roundingBorderWidth="
                   + roundingBorderWidth
                   + ", roundingBorderColor="
                   + Integer.toHexString(roundingBorderColor)
                   + '}';
        }
    }

    private void updateView() {
        //        if (isAttached) {
        invalidate();
        //        }
    }
}
