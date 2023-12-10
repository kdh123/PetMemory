package com.dohyun.domain.weather

import com.dohyun.domain.common.CommonResult


interface WeatherRepository {
    suspend fun getTodayWeather(todayDate: String, currentTime: String): CommonResult<WeatherDto, WeatherFailEvent>
}