package com.basic.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.widget.AppCompatTextView;

public class FoldedTextView extends AppCompatTextView {
    public FoldedTextView(Context context) {
        super(context);
    }

    public FoldedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FoldedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int limitLines = 3;
    private CharSequence last = null;

    private boolean needReplace = false;
    private boolean needCal = true;
    private boolean draw = false;

    private String open = "查看全部";
    private float length;

    private Paint paint;

    private RectF rect = new RectF();
    private PointF location = new PointF();

    class SmallOnClickListener implements OnClickListener {
        private OnClickListener listener;

        public SmallOnClickListener(OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (rect.contains(location.x, location.y)) {
                listener.onClick(v);
            }
        }
    }

    {

        paint = new Paint();
        paint.setTextSize(getTextSize());
        paint.setColor(Color.parseColor("#3CBFFF"));
        length = paint.measureText(open);
        super.setOnClickListener(new SmallOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                FoldedTextView.super.setOnClickListener(null);
                needCal = false;
                needReplace = false;
                draw = false;
                setMaxLines(Integer.MAX_VALUE);
                setText(last);
            }
        }));
    }

    public void setLimitLines(int limitLines) {
        this.limitLines = limitLines;
    }

    private ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            getViewTreeObserver().removeOnPreDrawListener(this);
            if (needReplace) {
                needReplace = false;
                if (getLayout() != null && getLayout().getLineCount() > limitLines) {
                    setMaxLines(limitLines);
                    draw = true;
                    needCal = false;
                    int end = getLayout().getLineEnd(limitLines - 1);
                    setText(last.subSequence(0, end - 5) + "…");
                    return false;
                } else {
                    draw = false;
                }
            }
            return true;
        }
    };

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (needCal) {
            getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            last = text;
            needReplace = true;
        }
        needCal = true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (draw) {
            try {

                float t = getLayout().getLineTop(limitLines - 1);
                float b = getLayout().getLineBottom(limitLines - 1);
                float r = getLayout().getLineRight(limitLines - 1);
                float baseline = getLayout().getLineBaseline(limitLines - 1);
                rect.set(r, t, r + length, b);

                canvas.drawText(open, r, baseline, paint);

            } catch (Exception e) {

            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            location.set(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

}


