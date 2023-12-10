package com.dohyun.data.di

import androidx.datastore.core.DataStore
import com.dohyun.domain.user.UserRepository
import com.dohyun.data.user.repository.UserRepositoryImpl
import com.dohyun.data.user.source.UserLocalDataSource
import com.dohyun.data.user.source.UserRemoteDataSource
import com.dohyun.petmemory.User
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Singleton
    @Provides
    fun bindUserRepository(
        userLocalDataSource: UserLocalDataSource,
        userRemoteDataSource: UserRemoteDataSource
    ): UserRepository {
        return UserRepositoryImpl(
            userLocalDataSource = userLocalDataSource,
            userRemoteDataSource = userRemoteDataSource
        )
    }

    @Singleton
    @Provides
    fun bindUserLocalDataSource(dataStore: DataStore<User.UserInfoData>): UserLocalDataSource {
        return UserLocalDataSource(dataStore = dataStore)
    }

    @Singleton
    @Provides
    fun bindUserRemoteDataSource(): UserRemoteDataSource {
        return UserRemoteDataSource()
    }
}