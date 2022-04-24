package com.common.component.net

import com.basic.network.BaseRepository
import com.basic.network.MyResultCallAdapterFactory
import com.basic.network.SimpleCallback
import com.basic.network.data.Base
import com.basic.network.data.CustomResult
import com.basic.network.data.Err
import com.basic.network.data.Success
import retrofit2.CallAdapter

/**
 * created by dongdaqing 2022/3/22 2:02 下午
 */
object CustomHttp : BaseRepository<Base<*>, RequestService>() {

    suspend fun getTranslateBySuspendResult(word: String): CustomResult<WordDetail> {
        return fetchDataBySuspendResult {
            getTranslateResult(
                mapOf(
                    "word" to word,
                    "uid" to "U202101294007036503493",
                    "type" to "0",
                    "sourceType" to "0"
                )
            )
        }
    }

    suspend fun getTranslateBySuspend(word: String): CustomResult<WordDetail> {
        return fetchDataBySuspend {
            getTranslate(
                mapOf(
                    "word" to word,
                    "uid" to "U202101294007036503493",
                    "type" to "0",
                    "sourceType" to "0"
                )
            )
        }
    }

    fun getTranslate(word: String, callback: SimpleCallback<WordDetail>) {
        fetchDataByCall(callback) {
            fetchDataCall(
                mapOf(
                    "word" to word,
                    "uid" to "U202101294007036503493",
                    "type" to "0",
                    "sourceType" to "0"
                )
            )
        }
    }

    private suspend fun <T> fetchDataBySuspendResult(call: suspend RequestService.() -> CustomResult<T>): CustomResult<T> {
        return call(requestService)
    }

    override fun baseUrl(): String {
        return "https://qagateway.ellabook.cn/rest/api/service/"
    }

    override fun customCallAdapter(): List<CallAdapter.Factory> {
        return listOf(MyResultCallAdapterFactory())
    }

    override fun validateData(data: Base<*>): CustomResult<Any?> {
        if (data.status != "true") {
            return Err(data.code, data.message)
        }

        return Success(data.data)
    }

    override fun catchException(e: Throwable): Err {
        return Err("00002", e.message)
    }


    /*fun getTranslate(
        word: String,
    ): LiveData<Result<WordDetails>> {

        return fetchData() {
            requestService.getTranslate(git config --list
                mapOf(
                    "word" to word,
                    "uid" to "U202101294007036503493",
                    "type" to "0",
                    "sourceType" to "0"
                )
            )
        }

    }*/

    /*private fun <T> fetchData(
        call: suspend () -> Base<T>
    ): LiveData<Result<T>> {

        return liveData(Dispatchers.IO) {
            latestValue?.let {
                emit(it)
                return@liveData
            }
            val response: Base<T>?

            try {
                response = call()
            } catch (e: Exception) {
                emit(Err("100", e.localizedMessage))
                return@liveData
            }

            response.apply {
                if (code == "1000") {
                    data?.apply {
                        emit(Success(this))
                    } ?: emit(Err("101", "data is null"))
                } else {
                    emit(Err("102", "code!=1000,msg=${response.message}"))
                }
            }
        }
    }*/
}