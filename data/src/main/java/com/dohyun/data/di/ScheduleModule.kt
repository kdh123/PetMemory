package com.dohyun.data.di

import com.dohyun.data.room.AppDatabase
import com.dohyun.data.schedule.repository.ScheduleRepositoryImpl
import com.dohyun.data.schedule.source.ScheduleLocalDataSource
import com.dohyun.domain.schedule.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScheduleModule {

    @Provides
    @Singleton
    fun bindScheduleRepository(localDataSource : ScheduleLocalDataSource) : ScheduleRepository {
        return ScheduleRepositoryImpl(localDataSource = localDataSource)
    }

    @Provides
    @Singleton
    fun bindScheduleDataSource(db : AppDatabase) : ScheduleLocalDataSource {
        return ScheduleLocalDataSource(db = db)
    }
}