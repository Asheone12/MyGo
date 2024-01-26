package com.muen.mygo.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "T_Song")
data class SongEntity(
    @PrimaryKey val sid: String,
    val songs: String,
    val sings: String,
    val album: String,
    val cover: String,
    val url: String
)