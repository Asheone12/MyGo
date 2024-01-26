package com.muen.mygo.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muen.mygo.source.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    /**
     * 查询歌单中歌曲的数量
     */
    @Query("select count(*) from T_Song")
    fun getSongCount(): Flow<Int>

    /**
     * 查询所有歌曲
     * Flow中的collect本身就是一个挂起函数，所以不需要额外用suspend修饰
     */
    @Query("select * from T_Song")
    fun loadSongs(): Flow<List<SongEntity>>

    /**
     * 查询指定歌曲
     * Flow中的collect本身就是一个挂起函数，所以不需要额外用suspend修饰
     */
    @Query("select * from T_Song where sid = :sid")
    fun selectSong(sid: String): Flow<SongEntity?>

    /**
     * 插入歌曲,key键重复的替换
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(vararg song: SongEntity)
}