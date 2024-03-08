package com.muen.mygo.service

import android.util.Log
import com.muen.mygo.http.CommonHandler
import com.muen.mygo.repo.AppServiceRepo
import com.muen.mygo.source.local.dao.SongDao
import com.muen.mygo.source.network.entity.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceModel @Inject constructor(val repo: AppServiceRepo, val songDao: SongDao) {
    /**
     * 获取网易云歌曲信息
     */
    fun getSong(id: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            repo.getSong(id, object : CommonHandler<Song>() {
                override suspend fun onDataResult(data: Song?) {
                    super.onDataResult(data)
                    if (data != null) {
                        Log.d("http","这首歌曲是 = $data")
                    }
                }
            })
        }
    }
}