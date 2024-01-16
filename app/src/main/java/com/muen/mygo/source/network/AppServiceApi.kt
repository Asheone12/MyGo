package com.muen.mygo.source.network

import com.muen.mygo.http.HttpResult
import com.muen.mygo.source.network.entity.ACGImg
import retrofit2.http.GET
import retrofit2.http.Query


interface AppServiceApi {

    /**
     * 随机动漫图片
     */
    @GET("acg")
    suspend fun randomACG(@Query("format") format: String): HttpResult<ACGImg>
}