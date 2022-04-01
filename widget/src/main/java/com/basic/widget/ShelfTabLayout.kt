package com.basic.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout


class ShelfTabLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    private val bgRadius: Float

    private val animator = ValueAnimator.ofInt().apply {
        addUpdateListener {
            setBounds(it.animatedValue as Int)
        }
    }

    var listener: OnClickListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ShelfTabLayout)
        bgRadius = array.getDimension(R.styleable.ShelfTabLayout_bgRadius, 0f)
        array.recycle()
        setWillNotDraw(false)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        child?.setOnClickListener(this)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        var child: View? = null
        for (index in 0 until childCount) {
            child = getChildAt(index)
            val param = child.layoutParams as MyLayoutParam
            if (param.checked) {
                break
            }
        }

        if (child == null && childCount > 0) {
            child = getChildAt(0)
        }

        child?.let {
            child.isSelected = true
            setBounds((child.top + child.bottom) / 2)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        drawBackGround(canvas)
    }

    override fun onClick(v: View) {
        handleClickEvent(v, false)
    }

    private fun handleClickEvent(v: View, fake: Boolean) {

        var selected: View? = null
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.isSelected)
                selected = child
        }

        val target = (v.top + v.bottom) / 2
        if (selected == null) {
            setBounds(target)
        } else {
            if (animator.isRunning) {
                animator.cancel()
            }
            if (animator.animatedValue == null) {
                animator.setIntValues((selected.top + selected.bottom) / 2, target)
            } else {
                animator.setIntValues(animator.animatedValue as Int, target)
            }
            animator.start()
        }

        if (!fake && selected == v)
            return

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.isSelected = child == v
        }

        listener?.onClick(v)
    }

    private fun setBounds(position: Int) {
        if (width > 0 && position > 0) {
            val left = (width - bgRadius) / 2
            val top = position - bgRadius / 2

            invalidate()
        }
    }

    fun getDrawableRadius(): Float {
        return bgRadius
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MyLayoutParam(context, attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): LayoutParams {
        lp?.run {
            (lp as? MyLayoutParam)?.run {
                return MyLayoutParam(lp)
            }
        }
        return MyLayoutParam(lp)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MyLayoutParam()
    }
}


class MyLayoutParam : LinearLayout.LayoutParams {
    var checked = false

    constructor() : super(WRAP_CONTENT, WRAP_CONTENT)

    constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
        val a = c.obtainStyledAttributes(attrs, R.styleable.ShelfTabLayout)
        checked = a.getBoolean(R.styleable.ShelfTabLayout_checked, false)
        a.recycle()
    }

    constructor(source: MyLayoutParam) : super(source) {
        checked = source.checked
    }

    constructor(source: ViewGroup.LayoutParams?) : super(source)
}