package com.basic.network.data

/**
 * created by dongdaqing 9/14/21 10:54 AM
 */
sealed class Result<out T> {

    var onErr: ((Err) -> Unit)? = null

    fun catch(onErr: ((Err) -> Unit)? = null): Result<T> {
        if (this is Err) {
            this.onErr = onErr
        }
        return this
    }

    inline fun onResult(onSuccess: (T) -> Unit) {
        when (this) {
            is Success -> onSuccess(result)
            is Err -> onErr?.invoke(this)
        }
    }

    inline fun onResult(
        noinline onErr: ((Err) -> Unit)? = null, crossinline onSuccess: (T) -> Unit
    ) {
        when (this) {
            is Success -> onSuccess(result)
            is Err -> onErr?.invoke(this)
        }
    }
}

class Err(val code: String, val msg: String?) : Result<Nothing>() {
    override fun toString(): String {
        return "errCode=$code,errMsg=$msg"
    }
}

class Success<T : Any>(val result: T) : Result<T>()

