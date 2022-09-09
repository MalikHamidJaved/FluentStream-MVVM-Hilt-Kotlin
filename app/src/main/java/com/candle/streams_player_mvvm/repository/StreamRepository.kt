package com.candle.streams_player_mvvm.repository

import com.candle.streams_player_mvvm.database.StreamDao
import com.candle.streams_player_mvvm.database.CacheMapper
import com.candle.streams_player_mvvm.database.StreamCacheEntity
import com.candle.streams_player_mvvm.model.LoggedInUser
import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.network.StreamApi
import com.candle.streams_player_mvvm.network.StreamApiResponseMapper
import com.candle.streams_player_mvvm.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StreamRepository
constructor(
    private val streamDao: StreamDao,
    private val streamApi: StreamApi,
    private val cacheMapper: CacheMapper,
    private val streamApiResponseMapper: StreamApiResponseMapper) {
    suspend fun getStream(user: LoggedInUser): Flow<DataState<List<Stream>>> = flow {
        emit(DataState.Loading)
        try {
            val networkStream = streamApi.get()
            if(networkStream.size > 0){
                val streams = streamApiResponseMapper.mapFromEntityList(networkStream)
                for (stream in streams) {
                    streamDao.insert(cacheMapper.mapToEntity(stream))
                }

            }

            if(user.isAdmin){
                val cachedStream = streamDao.get()
                emit(DataState.Success(cacheMapper.mapFromEntityList(cachedStream)))
            }else{
                val cachedStream = streamDao.getForFilteredUser(user.userId)
                emit(DataState.Success(cacheMapper.mapFromEntityList(cachedStream)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    suspend fun getStreamFromLocal(query: String, userData: LoggedInUser): Flow<DataState<List<Stream>>> = flow {
        emit(DataState.Loading)
        try {


            if(userData.isAdmin){
                var cachedStream = streamDao.get(query)
                emit(DataState.Success(cacheMapper.mapFromEntityList(cachedStream)))
            }else{
                var cachedStream = streamDao.getForFilteredUser(query,userData.userId)
                emit(DataState.Success(cacheMapper.mapFromEntityList(cachedStream)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }
}