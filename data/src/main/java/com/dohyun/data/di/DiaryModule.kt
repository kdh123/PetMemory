package com.dohyun.data.di

import com.dohyun.data.diary.reopository.DiaryRepositoryImpl
import com.dohyun.data.diary.source.DiaryLocalDataSource
import com.dohyun.data.room.AppDatabase
import com.dohyun.domain.diary.DiaryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiaryModule {

    @Provides
    @Singleton
    fun bindDiaryRepository(localDataSource : DiaryLocalDataSource) : DiaryRepository {
        return DiaryRepositoryImpl(localDataSource = localDataSource)
    }

    @Provides
    @Singleton
    fun bindDiaryDataSource(db : AppDatabase) : DiaryLocalDataSource {
        return DiaryLocalDataSource(db = db)
    }
}