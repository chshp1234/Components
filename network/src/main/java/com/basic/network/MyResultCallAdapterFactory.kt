package com.basic.network

import com.basic.network.data.Base
import com.basic.network.data.Err
import com.basic.network.data.Success
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.reflect.*
import java.util.*
import com.basic.network.data.CustomResult

/**
 * created by dongdaqing 2021/9/26 5:26 下午
 */


class MyResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                "Result return type must be parameterized as Call<Foo> or Call<out Foo>"
            )
        }

        val resultType = getParameterUpperBound(0, returnType)

        if (CustomResult::class.java != getRawType(resultType)) {
            return null
        }

        val responseType = getParameterUpperBound(0, resultType as ParameterizedType)
        return MyResultAdapter(responseType)
    }
}


class MyResultAdapter(type: Type) : CallAdapter<Base<*>, Call<*>> {

    private val responseType: ParameterizedTypeImpl =
        ParameterizedTypeImpl(null, Base::class.java, type)

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<Base<*>>): Call<*> {
        return ApiResultCall(call)
    }

}

class ParameterizedTypeImpl(
    ownerType: Type?,
    rawType: Type,
    vararg typeArguments: Type
) : ParameterizedType {
    private val ownerType: Type?
    private val rawType: Type
    private val typeArguments: Array<out Type>

    init {
        // Require an owner type if the raw type needs it.
        if (rawType is Class<*> && ownerType == null != (rawType.enclosingClass == null)) {
            throw IllegalArgumentException()
        }
        for (typeArgument in typeArguments) {
            Objects.requireNonNull(typeArgument, "typeArgument == null")
            if (typeArgument is Class<*> && typeArgument.isPrimitive) {
                throw IllegalArgumentException()
            }
        }
        this.ownerType = ownerType
        this.rawType = rawType
        this.typeArguments = typeArguments.clone()
    }

    override fun getActualTypeArguments(): Array<out Type> {
        return typeArguments.clone()
    }

    override fun getRawType(): Type {
        return rawType
    }

    override fun getOwnerType(): Type? {
        return ownerType
    }

    override fun equals(other: Any?): Boolean {
        return other is ParameterizedType && equals(this, other)
    }

    override fun hashCode(): Int {
        return (typeArguments.contentHashCode()
                xor rawType.hashCode()
                xor (ownerType?.hashCode() ?: 0))
    }

    override fun toString(): String {
        if (typeArguments.isEmpty()) {
            return typeToString(rawType)
        }
        val result = StringBuilder(30 * (typeArguments.size + 1))
        result.append(typeToString(rawType))
        result.append("<").append(typeToString(typeArguments[0]))
        for (i in 1 until typeArguments.size) {
            result.append(", ").append(typeToString(typeArguments[i]))
        }
        return result.append(">").toString()
    }

    private fun typeToString(type: Type): String {
        return if (type is Class<*>) type.name else type.toString()
    }

    fun equals(a: Type, b: Type): Boolean {
        return if (a === b) {
            true // Also handles (a == null && b == null).
        } else if (a is Class<*>) {
            a == b // Class already specifies equals().
        } else if (a is ParameterizedType) {
            if (b !is ParameterizedType) return false
            val pa = a
            val pb = b
            val ownerA: Any? = pa.ownerType
            val ownerB: Any = pb.ownerType
            ((ownerA === ownerB || ownerA != null && ownerA == ownerB)
                    && pa.rawType == pb.rawType && Arrays.equals(
                pa.actualTypeArguments,
                pb.actualTypeArguments
            ))
        } else if (a is GenericArrayType) {
            if (b !is GenericArrayType) return false
            equals(a.genericComponentType, b.genericComponentType)
        } else if (a is WildcardType) {
            if (b !is WildcardType) return false
            val wa = a
            val wb = b
            (Arrays.equals(wa.upperBounds, wb.upperBounds)
                    && Arrays.equals(wa.lowerBounds, wb.lowerBounds))
        } else if (a is TypeVariable<*>) {
            if (b !is TypeVariable<*>) return false
            val va = a
            val vb = b
            (va.genericDeclaration === vb.genericDeclaration
                    && va.name == vb.name)
        } else {
            false // This isn't a type we support!
        }
    }

}

class ApiResultCall(private val delegate: Call<Base<*>>) : Call<CustomResult<*>> {
    /**
     * 该方法会被Retrofit处理suspend方法的代码调用，并传进来一个callback,如果你回调了callback.onResponse，那么suspend方法就会成功返回
     * 如果你回调了callback.onFailure那么suspend方法就会抛异常
     *
     * 所以我们这里的实现是永远回调callback.onResponse,只不过在请求成功的时候返回的是ApiResult.Success对象，
     * 在失败的时候返回的是ApiResult.Failure对象，这样外面在调用suspend方法的时候就不会抛异常，一定会返回ApiResult.Success 或 ApiResult.Failure
     */
    override fun enqueue(callback: Callback<CustomResult<*>>) {
        //delegate 是用来做实际的网络请求的Call<T>对象，网络请求的成功失败会回调不同的方法
        delegate.enqueue(object : Callback<Base<*>> {

            /**
             * 网络请求成功返回，会回调该方法（无论status code是不是200）
             */
            override fun onResponse(call: Call<Base<*>>, response: Response<Base<*>>) {
                if (response.isSuccessful) {//http status 是200+
                    //这里担心response.body()可能会为null(还没有测到过这种情况)，所以做了一下这种情况的处理，
                    // 处理了这种情况后还有一个好处是我们就能保证我们传给ApiResult.Success的对象就不是null，这样外面用的时候就不用判空了

                    val apiResult = when {
                        response.body() == null -> {
                            Err("10001", "body is null")
                        }
                        response.body()!!.code != "1000" -> {
                            Err(
                                "10002", """code is not 1000
                                | msg=${response.body()!!.message}""".trimMargin()
                            )
                        }
                        response.body()!!.data == null -> {
                            Success("body is null")
                        }
                        else -> {
                            Success(response.body()!!.data as Any)
                        }
                    }

                    callback.onResponse(this@ApiResultCall, Response.success(apiResult))
                } else {//http status错误

                    val failureApiResult = Err(
                        response.code().toString(),
                        response.errorBody().toString()
                    )
                    callback.onResponse(this@ApiResultCall, Response.success(failureApiResult))
                }

            }

            /**
             * 在网络请求中发生了异常，会回调该方法
             *
             * 对于网络请求成功，但是业务失败的情况，我们也会在对应的Interceptor中抛出异常，这种情况也会回调该方法
             */
            override fun onFailure(call: Call<Base<*>>, t: Throwable) {

                val exception = transformException(t)

                callback.onResponse(
                    this@ApiResultCall,
                    Response.success(Err(exception.code.toString(), exception.msg))
                )
            }

        })
    }

    override fun clone(): Call<CustomResult<*>> = ApiResultCall(delegate.clone())

    override fun execute(): Response<CustomResult<*>> {
        throw UnsupportedOperationException("ApiResultCall does not support synchronous execution")
    }


    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}


fun create(): MyResultCallAdapterFactory {
    return MyResultCallAdapterFactory()
}