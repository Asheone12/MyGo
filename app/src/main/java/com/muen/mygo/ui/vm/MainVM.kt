package com.muen.mygo.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muen.mygo.http.CommonHandler
import com.muen.mygo.repo.AppServiceRepo
import com.muen.mygo.source.local.dao.SongDao
import com.muen.mygo.source.local.entity.SongEntity
import com.muen.mygo.source.network.entity.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(private val repo: AppServiceRepo, private val songDao: SongDao) : ViewModel() {
    val songResult = MutableLiveData<Song?>()

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


    /**
     * 查询数据库中所有的歌曲,不能用suspend修饰，否则在其他地方不能直接使用
     */
    fun loadSongs(handler: (List<SongEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            songDao.loadSongs().collect {
                handler.invoke(it)
            }
        }
    }

    /**
     * 查询数据库中所有的歌曲,不能用suspend修饰，否则在其他地方不能直接使用
     */
    fun selectSong(sid: String,handler: (SongEntity?) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            songDao.selectSong(sid).collect {
                handler.invoke(it)
            }
        }
    }

    /**
     * 向数据库中插入一首歌曲,不能用suspend修饰，否则在其他地方不能直接使用
     */
    fun insertWord(song: SongEntity) {
        viewModelScope.launch(Dispatchers.Main) {
            songDao.insertSong(song)
        }
    }
}