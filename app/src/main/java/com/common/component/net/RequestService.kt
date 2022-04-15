package com.common.component.net

import com.basic.network.data.Base
import com.basic.network.data.CustomResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * created by dongdaqing 9/14/21 10:48 AM
 */
interface RequestService {

    @POST("clouduser/word/v1/queryWord")
    suspend fun getTranslate(@Body map: Map<String, String>): Base<WordDetail>

    @POST("clouduser/word/v1/queryWord")
    suspend fun getTranslateResult(@Body map: Map<String, String>): CustomResult<WordDetail>

    @FormUrlEncoded
    @POST("clouduser/word/v1/queryWord")
    fun fetchDataCall(@FieldMap fields: Map<String, String>): Call<Base<WordDetail>>
}