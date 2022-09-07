package com.candle.streams_player_mvvm.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stream")
data class StreamCacheEntity(



    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val recording: String?,
    val timestamp: String?,
    val username_from: String?,
    val username_to: String?


)