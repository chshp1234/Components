package com.basic.widget

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import kotlin.math.sqrt


class ShelfTabLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    private val bgRadius: Float

    private val paint: Paint
    private val border: Path

    private val clearPaint: Paint

    private var currentChild: View? = null
    private var currentPosition = 0f

    private var conjunctionAngleRate: Double = 0.0
    private val conjunctionQuadRate = 3
    private val conjunctionHeightRate = 6
    private var conjunctionHeight = 0f

    private val animator = ValueAnimator.ofFloat().apply {
        addUpdateListener {
            updatePosition(it.animatedValue as Float)
        }
    }

    private var listener: OnClickListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ShelfTabLayout)
        bgRadius = array.getInt(R.styleable.ShelfTabLayout_bgRadius, 0).dp2px()

        paint = Paint()
        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#DDDDDD")
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND

        //        paint.setShadowLayer(2f, 1f, 1f, Color.parseColor("#efefef"))

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

    override fun onDraw(canvas: Canvas) {

        (parent as ViewGroup).clipToPadding = false
        (parent as ViewGroup).clipChildren = false

        if (currentChild == null && childCount > 0) {
            currentChild = getChildAt(0)
        }

        currentChild?.run {

            if (currentPosition == 0f) {
                currentPosition = left.toFloat() + width / 2
                conjunctionAngleRate = (width / conjunctionQuadRate) / bgRadius.toDouble()
                conjunctionHeight = bgRadius / conjunctionHeightRate
            }

            isSelected = true

            border.reset()
            border.moveTo(0f, (parent as ViewGroup).getChildAt(0).top.toFloat())
            border.lineTo(
                currentPosition - width / 2, (parent as ViewGroup).getChildAt(0).top.toFloat()
            )

            var x = currentPosition - width / conjunctionQuadRate
            val w = conjunctionHeight * conjunctionAngleRate

            border.quadTo(
                x,
                (parent as ViewGroup).getChildAt(0).top.toFloat(),
                (x + w).toFloat(),
                (parent as ViewGroup).getChildAt(0).top.toFloat() - conjunctionHeight
            )

            x = currentPosition + width / conjunctionQuadRate

            border.quadTo(
                currentPosition,
                -bgRadius,
                (x - w).toFloat(),
                (parent as ViewGroup).getChildAt(0).top.toFloat() - conjunctionHeight
            )

            border.quadTo(
                x,
                (parent as ViewGroup).getChildAt(0).top.toFloat(),
                currentPosition + width / 2,
                (parent as ViewGroup).getChildAt(0).top.toFloat()
            )

            border.lineTo(
                (parent as ViewGroup).width.toFloat(),
                (parent as ViewGroup).getChildAt(0).top.toFloat()
            )

            canvas.drawPath(border, paint)
        }

        super.onDraw(canvas)
    }

    override fun onClick(v: View) {
        handleClickEvent(v, false)
    }

    private fun handleClickEvent(v: View, fake: Boolean) {

        currentChild = v

        invalidate()

        val target = v.left.toFloat() + v.width / 2

        if (animator.isRunning) {
            animator.cancel()
        }

        if (animator.animatedValue == null) {
            animator.setFloatValues(currentPosition, target)
        } else {
            animator.setFloatValues(animator.animatedValue as Float, target)
        }
        animator.start()

        listener?.onClick(v)
    }

    private fun updatePosition(position: Float) {
        currentPosition = position

        invalidate()

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

    fun Int.dp2px(): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return this * scale + 0.5f
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    fun Int.px2dp(): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return this / scale + 0.5f
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