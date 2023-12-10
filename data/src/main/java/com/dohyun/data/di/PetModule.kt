package com.dohyun.data.di

import com.dohyun.data.pet.repository.PetRepositoryImpl
import com.dohyun.data.pet.source.PetLocalDataSource
import com.dohyun.data.room.AppDatabase
import com.dohyun.domain.pet.PetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PetModule {

    @Provides
    @Singleton
    fun bindPetRepository(localDataSource : PetLocalDataSource) : PetRepository {
        return PetRepositoryImpl(petLocalDataSource = localDataSource)
    }

    @Provides
    @Singleton
    fun bindPetDataSource(db : AppDatabase) : PetLocalDataSource {
        return PetLocalDataSource(db = db)
    }
}