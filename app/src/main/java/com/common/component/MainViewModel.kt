package com.common.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basic.network.SimpleCallback
import com.basic.network.data.Err
import com.basic.network.data.result
import com.blankj.utilcode.util.LogUtils
import com.common.component.net.CustomHttp
import com.common.component.net.WordDetail
import com.common.utils.ScreenShotUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * created by dongdaqing 2021/9/23 5:12 下午
 */
class MainViewModel : ViewModel() {

    private val onDataSuccess: MutableLiveData<WordDetail?> = MutableLiveData()
    private val onDataErr: MutableLiveData<Err> = MutableLiveData()

    val successLiveData: LiveData<WordDetail?> = onDataSuccess
    val errLiveData: LiveData<Err> = onDataErr

    fun getData(word: String) {
        viewModelScope.launch {
            //1.
            CustomHttp.getTranslateBySuspend(word)
                .catch { onDataErr.value = it }
                .onResult { onDataSuccess.value = it }

            //2.
            CustomHttp.getTranslate(word, object : SimpleCallback<WordDetail> {
                override fun onSuccess(data: WordDetail?) {
                    onDataSuccess.value = data
                }

                override fun onErr(code: String, msg: String?) {

                    onDataErr.value = Err(code, msg)
                }
            })

            //3.
            CustomHttp.getTranslateBySuspend(word).result {
                onErr {
                    onDataErr.value = it
                }

                onSuccess {
                    onDataSuccess.value = it
                }
            }

            //4.
            CustomHttp.getTranslateBySuspend(word).onResult({ onDataErr.value = it }) {
                onDataSuccess.value = it
            }
        }
    }

    fun test() {
        ScreenShotUtils.getInstance().beginScreenShot()
        LogUtils.d("test start")
        viewModelScope.launch {
            LogUtils.d("coroutine start")
            //            launch {
            //                block()
            //            }
            launch {
                changeContext()
            }
            delay(1000)
            LogUtils.d("coroutine end")
        }
        LogUtils.d("test end")
    }

    suspend fun block(): Int {
        LogUtils.d("block start")
        //模拟阻塞
        Thread.sleep(3000)
        LogUtils.d("block end")
        return 3
    }

    suspend fun changeContext() {
        withContext(Dispatchers.IO) {
            LogUtils.d("current Thread=${Thread.currentThread()}")
            val result = async {
                withContext(Dispatchers.IO) {
                    LogUtils.d("current Thread=${Thread.currentThread()}")
                    block()
                }
            }

            launch {
                withContext(Dispatchers.IO) {
                    LogUtils.d("current Thread=${Thread.currentThread()}")
                }
            }

            LogUtils.d("current Thread=${Thread.currentThread()} compute result")
            LogUtils.d("current Thread=${Thread.currentThread()} result=${result.await()}")

            flow<Int> {

            }.catch { }
                .collect { }
        }

        val c = coroutineScope {
            1
            cancel()
        }

        runBlocking {

        }
    }
}