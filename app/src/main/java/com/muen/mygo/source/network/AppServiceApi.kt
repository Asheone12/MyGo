package com.muen.mygo.source.network

import com.muen.mygo.http.HttpResult
import com.muen.mygo.source.network.entity.ACGImg
import com.muen.mygo.source.network.entity.Song
import retrofit2.http.GET
import retrofit2.http.Query


interface AppServiceApi {

    /**
     * 随机动漫图片
     */
    @GET("acg")
    suspend fun randomACG(@Query("format") format: String): HttpResult<ACGImg>

    /**
     * 获取网易云歌曲信息
     */
    @GET("songinfo")
    suspend fun getSong(@Query("id") id: Long): HttpResult<Song>
}