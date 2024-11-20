package com.common.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.basic.widget.DrawingView
import com.basic.widget.ShelfTabLayout
import com.blankj.utilcode.util.ToastUtils

/**
 * created by csp 2022/4/1 4:50 下午
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get()
        viewModel.errLiveData.observe(this) {
            ToastUtils.showShort(it.toString())
        }
        viewModel.successLiveData.observe(this) {
            ToastUtils.showShort(it?.toString())
        }
        viewModel.getData("aaa")

        /*findViewById<ShelfTabLayout>(R.id.shelfTabLayout).setOnChildClickListener { _, _ ->
            findViewById<DrawingView>(R.id.drawView).clear()
        }*/

    }
}

class TestQuadView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : View(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    private val paint: Paint
    private val topLine: Path
    private val border: Path

    init {

        paint = Paint()
        paint.strokeWidth = 4F
        paint.color = Color.parseColor("#DDDDDD")
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND

        topLine = Path()
        border = Path()

    }

    override fun onDraw(canvas: Canvas) {
        border.moveTo(200f, height.toFloat())
        border.quadTo((width / 2).toFloat(), 0f, width.toFloat() - 200, height.toFloat())
        canvas.drawPath(border, paint)
        Message.obtain()
        topLine.moveTo(0f, (height / 2).toFloat())
        topLine.lineTo(width.toFloat(), (height / 2).toFloat())
        canvas.drawPath(topLine, paint)
    }
}