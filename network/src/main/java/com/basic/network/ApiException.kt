package com.basic.network

import java.io.IOException
import java.net.SocketException

/**
 * created by dongdaqing 2021/9/14 5:44 下午
 */

open class ApiException(val code: Int, val msg: String) : Exception(msg)

class NetException(msg: String) : ApiException(10003, msg)
class UnknownException(msg: String) : ApiException(20000, msg)

fun transformException(t: Throwable): ApiException {
    return when (t) {
        is SocketException, is IOException -> NetException("network err")
        else -> UnknownException("UnknownException")
    }
}