package com.common.component

import android.os.Bundle
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

        findViewById<ShelfTabLayout>(R.id.shelfTabLayout).onChildClickListener =
            View.OnClickListener {
                findViewById<DrawingView>(R.id.drawView).clear()

                BottomDialog(this).show()
            }
    }
}