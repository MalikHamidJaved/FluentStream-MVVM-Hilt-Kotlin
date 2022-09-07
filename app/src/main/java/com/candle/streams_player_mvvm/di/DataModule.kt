package com.candle.streams_player_mvvm.di

import android.content.Context
import androidx.room.Room
import com.candle.streams_player_mvvm.database.StreamDao
import com.candle.streams_player_mvvm.database.StreamDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideBlogDb(@ApplicationContext context: Context): StreamDatabase {
        return Room.databaseBuilder(
            context, StreamDatabase::class.java,
            StreamDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideStreamDAO(streamDatabase: StreamDatabase): StreamDao {
        return streamDatabase.streamDao()
    }
}