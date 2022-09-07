package com.candle.streams_player_mvvm.di

import com.candle.streams_player_mvvm.database.StreamDao
import com.candle.streams_player_mvvm.database.CacheMapper
import com.candle.streams_player_mvvm.network.StreamApi
import com.candle.streams_player_mvvm.network.StreamApiResponseMapper
import com.candle.streams_player_mvvm.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        streamDao: StreamDao,
        streamApi: StreamApi,
        cacheMapper: CacheMapper,
        streamMapper: StreamApiResponseMapper
    ): MainRepository {
        return MainRepository(streamDao, streamApi, cacheMapper, streamMapper)
    }
}