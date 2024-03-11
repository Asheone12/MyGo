package com.muen.mygo.repo

import com.muen.mygo.http.CommonHandler
import com.muen.mygo.http.httpCollect
import com.muen.mygo.http.httpFlow
import com.muen.mygo.source.network.AppServiceApi
import com.muen.mygo.source.network.entity.PaulSong
import com.muen.mygo.source.network.entity.Song
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppServiceRepo @Inject constructor(
    private val service: AppServiceApi
) {

    /**
     * 获取网易云歌曲信息
     */
    suspend fun getSong(id: Long, handler: CommonHandler<Song>) {
        httpFlow {
            service.getSong(id)
        }.httpCollect(handler)
    }

    /**
     * 获取随机的音乐
     */
    fun getRandomSong(list: Int): Call<PaulSong> {
        return service.getRandomSong(list)
    }
}