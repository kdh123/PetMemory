package com.dohyun.data.di

import com.dohyun.data.weather.repository.WeatherRepositoryImpl
import com.dohyun.data.weather.source.WeatherRemoteDataSource
import com.dohyun.domain.weather.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Provides
    @Singleton
    fun bindWeatherRepository(remoteDataSource: WeatherRemoteDataSource) : WeatherRepository {
        return WeatherRepositoryImpl(remoteDataSource = remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideWeatherRemoteDataSource(@RetrofitModule.WeatherUrl service : Retrofit) : WeatherRemoteDataSource {
        return WeatherRemoteDataSource(service = service)
    }
}