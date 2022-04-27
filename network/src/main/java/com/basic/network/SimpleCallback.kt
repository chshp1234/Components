package com.basic.network

import com.basic.network.data.CustomResult

/**
 * created by csp 9/14/21 1:54 PM
 */
fun interface SimpleCallback<T> {

    fun onCallBack(data:CustomResult<T>)

}