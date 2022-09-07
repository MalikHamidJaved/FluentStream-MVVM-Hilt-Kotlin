package com.candle.streams_player_mvvm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StreamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogEntity: StreamCacheEntity): Long

    @Query("SELECT * FROM stream")
    suspend fun get(): List<StreamCacheEntity>

    @Query("SELECT * FROM stream where username_from like :query || '%'")
    suspend fun get(query:String): List<StreamCacheEntity>
}