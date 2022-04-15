package com.basic.network.data

/**
 * created by dongdaqing 9/14/21 10:54 AM
 */
sealed class CustomResult<out T> {

    var err: ((Err) -> Unit)? = null
    var success: ((Any) -> Unit)? = null

    fun catch(onErr: ((Err) -> Unit)? = null): CustomResult<T> {
        if (this is Err) {
            this.err = onErr
        }
        return this
    }

    inline fun onResult(onSuccess: (T) -> Unit) {
        when (this) {
            is Success -> onSuccess(result)
            is Err     -> err?.invoke(this)
        }
    }

    inline fun onResult(
        noinline onErr: ((Err) -> Unit)? = null, crossinline onSuccess: (T) -> Unit
    ) {
        when (this) {
            is Success -> onSuccess(result)
            is Err     -> onErr?.invoke(this)
        }
    }
}

class Err(val code: String, val msg: String?) : CustomResult<Nothing>() {
    override fun toString(): String {
        return "errCode=$code,errMsg=$msg"
    }
}

class Success<T>(val result: T) : CustomResult<T>()

fun <T> CustomResult<T>.result(call: CustomResult<T>.() -> Unit) {

    call()

    when (this) {
        is Success -> success?.invoke(result as Any)
        is Err     -> err?.invoke(this)
    }
}

fun CustomResult<*>.onErr(err: ((Err) -> Unit)? = null) {
    this.err = err
}

fun <T> CustomResult<T>.onSuccess(success: ((T) -> Unit)? = null) {
    this.success = success as ((Any) -> Unit)?
}