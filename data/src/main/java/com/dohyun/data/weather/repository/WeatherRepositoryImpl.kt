package com.dohyun.data.weather.repository

import com.dohyun.data.weather.source.WeatherRemoteDataSource
import com.dohyun.domain.common.CommonResult
import com.dohyun.domain.weather.WeatherDto
import com.dohyun.domain.weather.WeatherFailEvent
import com.dohyun.domain.weather.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    override suspend fun getTodayWeather(todayDate: String, currentTime: String): CommonResult<WeatherDto, WeatherFailEvent> {
        return remoteDataSource.getWeatherData(todayDate = todayDate, currentTime = currentTime)
    }
}