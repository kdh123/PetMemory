package com.dohyun.data.weather.source

import com.dohyun.domain.common.CommonResult
import com.dohyun.domain.weather.WeatherDto
import com.dohyun.domain.weather.WeatherFailEvent

interface WeatherDataSource {
    suspend fun getWeatherData(todayDate: String, currentTime: String): CommonResult<WeatherDto, WeatherFailEvent>
}