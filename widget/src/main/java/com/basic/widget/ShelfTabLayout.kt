package com.basic.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout


class ShelfTabLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    private val bgRadius: Int

    private val paint: Paint
    private val border: Path

    private val clearPaint: Paint

    private var currentChild: View? = null
    private var MODE_CLEAR = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private var MODE_SRC = PorterDuffXfermode(PorterDuff.Mode.SRC)

    private val animator = ValueAnimator.ofInt().apply {
        addUpdateListener {
            setBounds(it.animatedValue as Int)
        }
    }

    private var listener: OnClickListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ShelfTabLayout)
        bgRadius = array.getInt(R.styleable.ShelfTabLayout_bgRadius, 0)

        paint = Paint()
        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#333333")
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
//        paint.setShadowLayer(1f, 1f, -1f, Color.parseColor("#efefef"))

        clearPaint = Paint()

        border = Path()

        array.recycle()
        setWillNotDraw(false)

//        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)

        val param = params as? MyLayoutParam

        param?.run {
            if (checked) {
                currentChild = child
            }
        }
        child?.setOnClickListener(this)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        when {
            childCount == 0 -> {
                params?.width = 0
                params?.height = 0
            }
            orientation == VERTICAL -> {
                params?.height = MATCH_PARENT
            }
            else -> {
                params?.width = MATCH_PARENT
            }
        }

        super.setLayoutParams(params)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    var layerId: Int? = null

    override fun onDraw(canvas: Canvas) {

        (parent as ViewGroup).clipToPadding = false
        (parent as ViewGroup).clipChildren = false

        if (currentChild == null && childCount > 0) {
            currentChild = getChildAt(0)
        }

//        if (layerId == null) {
//            layerId = canvas.saveLayer(
//                (parent as ViewGroup).top.toFloat(), (parent as ViewGroup).top.toFloat(),
//                (parent as ViewGroup).right.toFloat(), (parent as ViewGroup).bottom.toFloat(), null
//            )
//        }

        currentChild?.run {

//            paint.xfermode = MODE_CLEAR
//            canvas.drawRect(0f, 0f, 300f, 300f, paint)
//            paint.xfermode = MODE_SRC

//            layerId?.let {
//                canvas.restoreToCount(it)
//            }

            isSelected = true

            border.reset()
            border.moveTo(0f, (parent as ViewGroup).getChildAt(0).top.toFloat())
            border.lineTo(left.toFloat(), (parent as ViewGroup).getChildAt(0).top.toFloat())
            border.quadTo(
                left.toFloat() + width / 2, -bgRadius * 10f,
                left.toFloat() + width, (parent as ViewGroup).getChildAt(0).top.toFloat()
            )
            border.lineTo(
                (parent as ViewGroup).width.toFloat(),
                (parent as ViewGroup).getChildAt(0).top.toFloat()
            )

            canvas.drawPath(border, paint)
        }

        super.onDraw(canvas)

        //        drawBackGround(canvas)
    }

    override fun onClick(v: View) {
        handleClickEvent(v, false)
    }

    private fun handleClickEvent(v: View, fake: Boolean) {

        currentChild = v

        invalidate()
        /*for (i in 0 until childCount) {
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
        }*/

        listener?.onClick(v)
    }

    private fun setBounds(position: Int) {
        if (width > 0 && position > 0) {
            val left = (width - bgRadius) / 2
            val top = position - bgRadius / 2

            invalidate()
        }
    }

    fun getDrawableRadius(): Int {
        return bgRadius
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return generateMyLayoutParams(attrs = attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): LayoutParams {
        return generateMyLayoutParams(lp = lp)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return generateMyLayoutParams()
    }

    private fun generateMyLayoutParams(
        attrs: AttributeSet? = null,
        lp: ViewGroup.LayoutParams? = null
    ): MyLayoutParam {

        val param: MyLayoutParam =
            if (lp != null && lp is MyLayoutParam) {
                MyLayoutParam(lp)
            } else if (attrs != null) {
                MyLayoutParam(context, attrs)
            } else {
                MyLayoutParam(context, null)
            }

        param.width = MATCH_PARENT
        param.height = MATCH_PARENT
        param.weight = 1f

        return param
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