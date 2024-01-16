package com.muen.mygo.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muen.mygo.http.CommonHandler
import com.muen.mygo.repo.AppServiceRepo
import com.muen.mygo.source.network.entity.ACGImg
import com.muen.mygo.source.network.entity.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(private val repo: AppServiceRepo) : ViewModel() {
    val acgResult = MutableLiveData<ACGImg?>()
    val songResult = MutableLiveData<Song?>()

    /**
     * 获取随机动漫图片
     */
    fun randomACG() {
        viewModelScope.launch {
            repo.randomACG(object : CommonHandler<ACGImg>() {
                override suspend fun onDataResult(data: ACGImg?) {
                    super.onDataResult(data)
                    if (data != null) {
                        acgResult.value = data
                    }

                }

            })
        }
    }

    /**
     * 获取网易云歌曲信息
     */
    fun getSong(id: Long) {
        viewModelScope.launch {
            repo.getSong(id, object : CommonHandler<Song>() {
                override suspend fun onDataResult(data: Song?) {
                    super.onDataResult(data)
                    if (data != null) {
                        songResult.value = data
                    }
                }
            })
        }
    }
}