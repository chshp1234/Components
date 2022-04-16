package com.basic.network

import android.text.TextUtils
import android.util.Log
import com.basic.network.data.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.ParameterizedType
import java.util.concurrent.TimeUnit

abstract class BaseRepository<DATA, SERVICE> {

    protected val requestService: SERVICE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        getRetrofit().create(getServiceClass())
    }

    private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(60, TimeUnit.SECONDS) //连接 超时时间
            writeTimeout(60, TimeUnit.SECONDS) //写操作 超时时间
            readTimeout(60, TimeUnit.SECONDS) //读操作 超时时间
            retryOnConnectionFailure(true) //错误重连
            // val cacheFile = File(context.cacheDir,"cache")
            // val cache = Cache(cacheFile,1024 *1024 *100)//100Mb
            // cache(cache)

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

    private fun getServiceClass(): Class<SERVICE> {
        var serviceClass: Class<SERVICE>? = null
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val actualTypeArguments = type.actualTypeArguments;
            if (actualTypeArguments.isNotEmpty()) {
                (actualTypeArguments[1] as? Class<SERVICE>)?.let {
                    serviceClass = it
                }

            }
        }

        return serviceClass?.run {
            return this
        } ?: throw ClassNotFoundException("service not found")

    }

    protected abstract fun baseUrl(): String

    protected abstract fun validateData(data: DATA): CustomResult<Any?>

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

    protected suspend fun <T> fetchDataBySuspend(call: suspend SERVICE.() -> DATA): CustomResult<T> {
        val response: DATA
        try {
            response = call(requestService)
        } catch (e: Throwable) {
            return catchException(e)
        }
        return validateData(response) as CustomResult<T>
    }

    protected inline fun <reified T> fetchDataByCall(
        callback: SimpleCallback<T>,
        crossinline call: SERVICE.() -> Call<*>
    ) {
        (call(requestService) as? Call<DATA>)?.enqueue(object : Callback<DATA> {
            override fun onResponse(call: Call<DATA>, response: Response<DATA>) {
                val body = response.body()
                if (body == null) {
                    val invocation = call.request().tag(Invocation::class.java)!!
                    val method = invocation.method()
                    val e = KotlinNullPointerException(
                        "Response from " +
                                method.declaringClass.name +
                                '.' +
                                method.name +
                                " was null but response body type was declared as non-null"
                    )
                    val catchException = `access$catchException`(e)
                    callback.onErr(catchException.code, catchException.msg)
                } else {

                    /*`access$validateData`(body).catch { err ->
                        callback.onErr(err.code, err.msg)
                    }.onResult { data ->
                        callback.onSuccess(data as T)
                    }*/

                    `access$validateData`(body).result {
                        onErr { err ->
                            callback.onErr(err.code, err.msg)
                        }

                        onSuccess { data ->
                            callback.onSuccess(data as T)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<DATA>, t: Throwable) {
                val err = `access$catchException`(t)
                callback.onErr(err.code, err.msg)
            }
        }) ?: throw Throwable("call type not match")
    }


    @PublishedApi
    internal fun `access$validateData`(data: DATA) = validateData(data)

    @PublishedApi
    internal fun `access$catchException`(e: Throwable) = catchException(e)
}
