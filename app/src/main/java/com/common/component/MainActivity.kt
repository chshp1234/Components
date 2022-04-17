package com.common.component

import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.blankj.utilcode.util.ToastUtils

/**
 * created by dongdaqing 2022/4/1 4:50 下午
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
        viewModel.getData("aaa")
    }
}