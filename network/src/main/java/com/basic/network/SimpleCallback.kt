package com.basic.network

/**
 * created by dongdaqing 9/14/21 1:54 PM
 */
fun interface SimpleCallback<T> {

    fun onSuccess(data: T)

    fun onErr(code: String, msg: String?) {}
}