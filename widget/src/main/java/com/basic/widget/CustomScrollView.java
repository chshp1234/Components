package com.basic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.appcompat.widget.AppCompatTextView;


public class CustomScrollView extends AppCompatTextView implements View.OnTouchListener {

    private Scroller mScroller;
    private MySimpleOnGestureListener onGestureListener;
    private GestureDetector detector;

    public CustomScrollView(Context context) {
        this(context, null);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        onGestureListener = new MySimpleOnGestureListener();
        detector = new GestureDetector(context, onGestureListener);
        //        setOnTouchListener(this);
    }

    private int lastX;
    private int lastY;

    private int totalMoveX;
    private int totalMoveY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;

                mCanFail = true;
                //                mFixedX = event.getX() - mStartX;
                //                mFixedY = event.getY() - mStartY;
                mSpeedX = 0;
                mSpeedY = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                // 计算移动的距离
                int offsetX = x - lastX;
                int offsetY = y - lastY;

                totalMoveX += offsetX;
                totalMoveY += offsetY;

                // 调用layout方法来重新放置它的位置
                layout(
                        getLeft() + offsetX,
                        getTop() + offsetY,
                        getRight() + offsetX,
                        getBottom() + offsetY);

                break;
            case MotionEvent.ACTION_UP:
                if (!mCanFail) {
                    break;
                }
                mStartX = getLeft();
                mStartY = getTop();
                if (refreshRectByCurrentPoint()) {
                    /*mFixedX = event.getX() - mStartX;
                    mFixedY = event.getY() - mStartY;*/
                }
                break;
        }

        return detector.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        //        super.computeScroll();
        if (mScroller != null && mScroller.computeScrollOffset()) {
            //            setScrollX(mScroller.getCurrX());
            //            setScrollY(mScroller.getCurrY());
            setTranslationX(mScroller.getCurrX());
            setTranslationY(mScroller.getCurrY());
            //                        scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 通过不断的重绘不断的调用computeScroll方法
            invalidate();
        }
    }

    public void smoothScrollTo(int destX, int destY) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int deltaX = destX - scrollX;
        int deltaY = destY - scrollY;
        // 1000秒内滑向destX
        mScroller.startScroll(scrollX, scrollY, deltaX, deltaY, 2000);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        //        mRect.left = getLeft();
        //        mRect.right = getRight();
        //        mRect.top = getTop();
        //        mRect.bottom = getBottom();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private boolean mCanFail = false; // 是否可以拖动

    private float mFixedX = 0; // 修正距离X
    private float mFixedY = 0; // 修正距离Y

    private float mStartX = 0; // 小方块开始位置X
    private float mStartY = 0; // 小方块开始位置Y

    private Boolean mXFixed = false;
    private Boolean mYFixed = false;

    private float mSpeedX = 0; // 像素/s
    private float mSpeedY = 0;

    private int mWidth; // 宽度
    private int mHeight; // 高度
    //    private RectF mRect = new RectF();

    /**
     * 刷新方块位置
     *
     * @return true 表示修正过位置, false 表示没有修正过位置
     */
    private Boolean refreshRectByCurrentPoint() {
        Boolean fixed = false;
        mXFixed = false;
        mYFixed = false;
        // 修正坐标
        if (mStartX < 0) {
            mStartX = 0;
            fixed = true;
            mXFixed = true;
        }
        if (mStartY < 0) {
            mStartY = 0;
            fixed = true;
            mYFixed = true;
        }
        if (mStartX + mWidth > ((ViewGroup) getParent()).getWidth()) {
            mStartX = ((ViewGroup) getParent()).getWidth() - mWidth;
            fixed = true;
            mXFixed = true;
        }
        if (mStartY + mHeight > ((ViewGroup) getParent()).getHeight()) {
            mStartY = ((ViewGroup) getParent()).getHeight() - mHeight;
            fixed = true;
            mYFixed = true;
        }
        //        mRect.left = mStartX;
        //        mRect.top = mStartY;
        //        mRect.right = mStartX + mWidth;
        //        mRect.bottom = mStartY + mHeight;
        return fixed;
    }

    private Runnable mRunnable =
            new Runnable() {
                @Override
                public void run() {

                    // TODO 刷新内容
                    mStartX = mStartX + (mSpeedX / 1000) * 10;
                    mStartY = mStartY + (mSpeedY / 1000) * 10;
                    // mSpeedX = mSpeedX > 0 ? mSpeedX - 10 : mSpeedX + 10;
                    // mSpeedY = mSpeedY > 0 ? mSpeedY - 10 : mSpeedY + 10;
                    mSpeedX *= 0.998;
                    mSpeedY *= 0.998;
                    if (Math.abs(mSpeedX) < 1) {
                        mSpeedX = 0;
                    }
                    if (Math.abs(mSpeedY) < 1) {
                        mSpeedY = 0;
                    }
                    if (refreshRectByCurrentPoint()) {
                        // 转向
                        if (mXFixed) {
                            mSpeedX = -mSpeedX;
                        }
                        if (mYFixed) {
                            mSpeedY = -mSpeedY;
                        }
                    }

                    // 调用layout方法来重新放置它的位置
                    layout(
                            (int) mStartX,
                            (int) mStartY,
                            (int) (mStartX + mWidth),
                            (int) (mStartY + mHeight));
                    if (mSpeedX == 0 && mSpeedY == 0) {
                        removeCallbacks(this);
                        return;
                    }
                    postDelayed(this, 10);
                }
            };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        public MySimpleOnGestureListener() {
            super();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            LogUtils.d("onSingleTapUp: event=" + MotionEventUtils.toFlage(e));
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            LogUtils.d("onLongPress: event=" + MotionEventUtils.toFlage(e));
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            LogUtils.d(
//                    "onScroll: event1="
//                            + MotionEventUtils.toFlage(e1)
//                            + "\n"
//                            + "event2="
//                            + MotionEventUtils.toFlage(e2)
//                            + "\ndistanceX="
//                            + distanceX
//                            + " distanceY="
//                            + distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            LogUtils.d(
//                    "onFling: event1="
//                            + MotionEventUtils.toFlage(e1)
//                            + "\n"
//                            + "event2="
//                            + MotionEventUtils.toFlage(e2)
//                            + "\nvelocityX="
//                            + velocityX
//                            + " velocityY="
//                            + velocityY);
            if (!mCanFail) return false;
            mSpeedX = velocityX;
            mSpeedY = velocityY;
            removeCallbacks(mRunnable);
            postDelayed(mRunnable, 0);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
//            LogUtils.d("onShowPress: event=" + MotionEventUtils.toFlage(e));
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
//            LogUtils.d("onDown: event=" + MotionEventUtils.toFlage(e));
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            LogUtils.d("onDoubleTap: event=" + MotionEventUtils.toFlage(e));
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
//            LogUtils.d("onDoubleTapEvent: event=" + MotionEventUtils.toFlage(e));
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            LogUtils.d("onSingleTapConfirmed: event=" + MotionEventUtils.toFlage(e));
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
//            LogUtils.d("onContextClick: event=" + MotionEventUtils.toFlage(e));
            return super.onContextClick(e);
        }
    }

    public static String toFlage(MotionEvent event) {
        String flag = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                flag = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_UP:
                flag = "ACTION_UP";
                break;
            case MotionEvent.ACTION_MOVE:
                flag = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_CANCEL:
                flag = "ACTION_CANCEL";
                break;
            default:
                flag = event.getAction() + "";
        }
        return flag;
    }
}
