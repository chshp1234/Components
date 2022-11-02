package com.basic.widget

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
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

    private val paint: Paint
    private val border: Path
    private var line: Path

    private var currentChild: View? = null
    private var currentPosition = 0f

    //顶点(第二个曲线)控制点高度
    private val bgRadius: Float

    //第一个曲线和第二个曲线连接点的角度斜率
    private var conjunctionAngleRate: Double = 0.0

    //第一个曲线曲度比例
    private val conjunctionQuadRate = 3

    //第一个曲线高度比例
    private val conjunctionHeightRate = 0.4

    //第一个曲线高度
    private var conjunctionHeight = 0f

    private var bgColor = 0

    private val animator = ValueAnimator.ofFloat().apply {
        addUpdateListener {
            updatePosition(it.animatedValue as Float)
        }
    }

    private var onChildClick: ((currentChild: View, lastChild: View?) -> Unit)? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ShelfTabLayout)
        val bgh = array.getDimension(R.styleable.ShelfTabLayout_bgRadius, 0f)
        val borderStroke = array.getDimension(R.styleable.ShelfTabLayout_borderStroke, 0f)
        bgColor = array.getColor(R.styleable.ShelfTabLayout_bgColor, 0)
        val borderColor = array.getColor(R.styleable.ShelfTabLayout_borderColor, 0)

        paint = Paint()
        paint.strokeWidth = borderStroke.dp2px()
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND

        border = Path()

        line = Path()

        array.recycle()
        setWillNotDraw(false)

        conjunctionHeight = (bgh * conjunctionHeightRate).toFloat()
        bgRadius = (bgh - conjunctionHeight) * 2 + conjunctionHeight
    }

    fun setOnChildClickListener(onClickListener: (currentChild: View, lastChild: View?) -> Unit) {
        onChildClick = onClickListener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //离开窗口时移除动画
        animator.pause()
        animator.cancel()
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        val param = child?.layoutParams as? MyLayoutParam

        param?.run {
            if (checked) {
                currentChild = child

            }
        }
        child?.setOnClickListener(this)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        when {
            childCount == 0         -> {
                params?.width = 0
                params?.height = 0
            }
            orientation == VERTICAL -> {
                params?.height = MATCH_PARENT
            }
            else                    -> {
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
        if (currentChild == null && childCount > 0) {
            currentChild = getChildAt(0)
        }

        currentChild?.run {
            currentPosition = left.toFloat() + width / 2
        }

        (parent as ViewGroup).clipToPadding = false
        (parent as ViewGroup).clipChildren = false
    }

    override fun onDraw(canvas: Canvas) {

        currentChild?.run {

            //计算第一个曲线和第二个曲线连接点的角度斜率
            conjunctionAngleRate = (width / conjunctionQuadRate) / bgRadius.toDouble()
            isSelected = true

            border.reset()
            line.reset()

            border.moveTo(0f, 0f)
            border.lineTo(currentPosition - width / 2, 0f)

            //第一个曲线的控制点x坐标
            var x = currentPosition - width / conjunctionQuadRate
            //x + w=第一个曲线结束点x坐标
            val w = conjunctionHeight * conjunctionAngleRate

            //第一个曲线
            border.quadTo(x, 0f, (x + w).toFloat(), -conjunctionHeight)

            x = currentPosition + width / conjunctionQuadRate

            //第二个曲线
            border.quadTo(currentPosition, -bgRadius, (x - w).toFloat(), -conjunctionHeight)

            //第三个曲线
            border.quadTo(x, 0f, currentPosition + width / 2, 0f)

            border.lineTo((parent as ViewGroup).width.toFloat(), 0f)

            line.set(border)
            canvas.drawPath(line, paint)

            border.lineTo(
                (parent as ViewGroup).width.toFloat(),
                (parent as ViewGroup).height.toFloat()
            )

            border.lineTo(0f, (parent as ViewGroup).height.toFloat())

            border.lineTo(0f, 0f)

            canvas.save()
            if (bgColor != 0) {
                canvas.clipPath(border)
                canvas.drawColor(bgColor)
            }
            canvas.drawPath(line, paint)
            canvas.restore()
        }

        super.onDraw(canvas)
    }

    override fun onClick(v: View) {
        handleClickEvent(v, false)
    }

    private fun handleClickEvent(v: View, fake: Boolean) {
        onChildClick?.invoke(v, currentChild)

        currentChild = v

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

    fun Float.dp2px(): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return this * scale + 0.5f
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    fun Float.px2dp(): Float {
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