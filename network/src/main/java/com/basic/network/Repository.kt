package com.basic.network

import android.text.TextUtils
import android.util.Log
import com.basic.network.data.Err
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.basic.network.data.Result

abstract class BaseRepository<DATA, SERVICE> {

    protected val requestService: SERVICE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        getRetrofit().create(serviceClass())
    }

    private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(60, TimeUnit.SECONDS)//连接 超时时间
            writeTimeout(60, TimeUnit.SECONDS)//写操作 超时时间
            readTimeout(60, TimeUnit.SECONDS)//读操作 超时时间
            retryOnConnectionFailure(true)//错误重连
//            val cacheFile = File(context.cacheDir,"cache")
//            val cache = Cache(cacheFile,1024 *1024 *100)//100Mb
//            cache(cache)

            if (com.basic.network.BuildConfig.DEBUG) {
                val httpInterceptor = HttpLoggingInterceptor {
                    if (TextUtils.isEmpty(it)) {
                        return@HttpLoggingInterceptor
                    }
                    val len: Int = it.length
                    var readLen = 0
                    while (readLen < len) {
                        var end = readLen + 3500
                        if (readLen + 3500 > len) {
                            end = len
                        }
                        Log.d("HttpLoggingInterceptor", it.substring(readLen, end))
                        readLen = end
                    }
                }
                httpInterceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(httpInterceptor)
            }

            customHttpInterceptor()?.forEach {
                addInterceptor(it)
            }

            customApplicationInterceptor()?.forEach {
                addNetworkInterceptor(it)
            }
        }

        return builder
    }

    private fun getRetrofit(): Retrofit {
        val builder = getOkHttpClientBuilder()
        // 创建Retrofit
        val client = Retrofit.Builder().client(builder.build())

        customConverterFactory()?.forEach {
            client.addConverterFactory(it)
        }

        customCallAdapter()?.forEach {
            client.addCallAdapterFactory(it)
        }

        return client
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl())
            .build()
    }

    protected abstract fun baseUrl(): String

    protected abstract fun serviceClass(): Class<SERVICE>

    protected abstract fun <T> validateData(data: DATA?): Result<T>

    protected abstract fun catchException(e: Throwable): Err

    protected open fun customCallAdapter(): List<CallAdapter.Factory>? {
        return null
    }

    protected open fun customConverterFactory(): List<Converter.Factory>? {
        return null
    }

    protected open fun customHttpInterceptor(): List<Interceptor>? {
        return null
    }

    protected open fun customApplicationInterceptor(): List<Interceptor>? {
        return null
    }

    protected suspend fun <T> fetchDataBySuspend(call: suspend SERVICE.() -> DATA): Result<T> {
        val response: DATA
        try {
            response = call(requestService)
        } catch (e: Throwable) {
            return catchException(e)
        }
        return validateData(response)
    }

    protected fun <T> fetchDataByCall(
        callback: SimpleCallback<T>,
        call: SERVICE.() -> Call<*>
    ) {
        (call(requestService) as? Call<DATA>)?.enqueue(object : Callback<DATA> {
            override fun onResponse(call: Call<DATA>, response: Response<DATA>) {

                validateData<T>(response.body()).catch {
                    callback.onErr(it.code, it.msg)
                }.onResult {
                    callback.onSuccess(it)
                }
            }

            override fun onFailure(call: Call<DATA>, t: Throwable) {
                val err = catchException(t)
                callback.onErr(err.code, err.msg)
            }
        }) ?: throw Throwable("call type not match")
    }

}