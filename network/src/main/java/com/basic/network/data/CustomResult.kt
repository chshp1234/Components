package com.basic.network.data

/**
 * created by dongdaqing 9/14/21 10:54 AM
 */
sealed class CustomResult<out T> {

    private var err: ((Err) -> Unit)? = null

    fun catch(onErr: ((Err) -> Unit)? = null): CustomResult<T> {
        if (this is Err) {
            this.err = onErr
        }
        return this
    }

    fun onResult(onErr: ((Err) -> Unit)? = null, onSuccess: ((T?) -> Unit)? = null) {
        when (this) {
            is Success -> onSuccess?.invoke(result)
            is Err     -> onErr?.invoke(this) ?: err?.invoke(this)
        }
    }
}

class Err(val code: String, val msg: String?) : CustomResult<Nothing>() {
    override fun toString(): String {
        return "errCode=$code,errMsg=$msg"
    }
}

class Success<T>(val result: T?) : CustomResult<T>()

class InnerResult<T> {
    var err: ((Err) -> Unit)? = null
    var success: ((T?) -> Unit)? = null

    fun onErr(err: (Err) -> Unit) {
        this.err = err
    }

    fun onSuccess(success: (T?) -> Unit) {
        this.success = success
    }
}

inline fun <T> CustomResult<T>.result(crossinline call: InnerResult<T>.() -> Unit) {
    val innerResult = InnerResult<T>()
    call(innerResult)

    onResult(innerResult.err, innerResult.success)
}