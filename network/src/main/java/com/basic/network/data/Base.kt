package com.basic.network.data

/**
 * created by dongdaqing 2022/3/22 3:28 下午
 */
data class Base<T>(
    val code: String,
    val `data`: T?,
    val message: String,
    val remark: String,
    val status: String
)