package com.basic.network.data

/**
 * created by csp 9/14/21 10:54 AM
 */
sealed class CustomResult<out T> {

    fun catch(onErr: ((Err) -> Unit)? = null): CustomResult<T> {
        if (this is Err) {
            this.err = onErr
        }
        return this
    }

    inline fun onResult(crossinline onSuccess: ((T) -> Unit)) {
        when (this) {
            is Success -> onSuccess.invoke(result)
            is Err -> err?.invoke(this)
        }
    }

    fun getData(): T? {
        return when (this) {
            is Success -> result
            is Err -> null
        }
    }
}

class Err(val code: String, val msg: String?, var errData: Any? = null) : CustomResult<Nothing>() {
    var err: ((Err) -> Unit)? = null

    override fun toString(): String {
        return "errCode=$code,errMsg=$msg"
    }
}

class Success<T>(val result: T) : CustomResult<T>()

class InnerResult<T> {
    var err: ((Err) -> Unit)? = null
    var success: ((T) -> Unit) = DEFAULT_SUCCESS

    fun onErr(err: (Err) -> Unit) {
        this.err = err
    }

    fun onSuccess(success: (T) -> Unit) {
        this.success = success
    }

    private companion object {
        var DEFAULT_SUCCESS: ((Any?) -> Unit) = {}
    }
}

inline fun <T> CustomResult<T>.result(crossinline call: InnerResult<T>.() -> Unit) {
    val innerResult = InnerResult<T>()
    call(innerResult)

    catch(innerResult.err).onResult(innerResult.success)
}