package com.muen.mygo.service

import com.muen.mygo.http.CommonHandler
import com.muen.mygo.repo.AppServiceRepo
import com.muen.mygo.source.local.dao.SongDao
import com.muen.mygo.source.network.entity.PaulSong
import com.muen.mygo.source.network.entity.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceModel @Inject constructor(val repo: AppServiceRepo, val songDao: SongDao) {
    /**
     * 获取网易云歌曲信息
     */
    fun getSong(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.getSong(id, object : CommonHandler<Song>() {
                override suspend fun onDataResult(data: Song?) {
                    super.onDataResult(data)
                }
            })

        }

    }

    /**
     * 获取随机网易云歌曲信息
     */
    fun getRandomSong(list: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.getRandomSong(list).enqueue(object : retrofit2.Callback<PaulSong> {
                override fun onResponse(call: Call<PaulSong>, response: Response<PaulSong>) {
                    Timber.tag("handle").d("code = ${response.code()} data = ${response.body()}")
                }

                override fun onFailure(call: Call<PaulSong>, t: Throwable) {

                }

            })
        }
    }
}