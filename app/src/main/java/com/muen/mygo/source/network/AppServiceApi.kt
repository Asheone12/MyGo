package com.muen.mygo.source.network

import com.muen.mygo.http.HttpResult
import com.muen.mygo.source.network.entity.PaulSong
import com.muen.mygo.source.network.entity.Song
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface AppServiceApi {

    /**
     * BASE_URL = https://tenapi.cn/v2/
     * 获取指定网易云歌曲信息
     * 如果用的是Retrofit默认配置的BASE_URL,那Headers可以不加
     */
    @Headers("url_name:tenApi")
    @GET("songinfo")
    suspend fun getSong(@Query("id") id: Long): HttpResult<Song>

    /**
     * BASE_URL = https://api.paugram.com
     * 获取随机网易云动漫歌曲
     */
    @Headers("url_name:paugram")
    @GET("/acgm/")
    fun getRandomSong(@Query("list") list: Int): Call<PaulSong>

}