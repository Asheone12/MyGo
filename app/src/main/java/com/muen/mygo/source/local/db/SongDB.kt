package com.muen.mygo.source.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.muen.mygo.source.local.dao.SongDao
import com.muen.mygo.source.local.entity.SongEntity

@Database(version = 1, entities = [SongEntity::class])
abstract class SongDB : RoomDatabase(){
    abstract fun songDao(): SongDao

    companion object {
        private var instance: SongDB? = null

        @Synchronized
        fun getDatabase(context: Context): SongDB {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                SongDB::class.java,
                "song.db"
            ).createFromAsset("database/song.db")        //如果需要手动添加数据库文件，加上这一句话
                .build().apply {
                    instance = this
                }
        }
    }
}