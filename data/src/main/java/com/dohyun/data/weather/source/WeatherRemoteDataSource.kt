package com.dohyun.data.weather.source

import com.dohyun.data.weather.WeatherAPI
import com.dohyun.domain.common.CommonResult
import com.dohyun.domain.common.ErrorResponse
import com.dohyun.domain.weather.WeatherDto
import com.dohyun.domain.weather.WeatherFailEvent
import retrofit2.Retrofit
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRemoteDataSource @Inject constructor(private val service: Retrofit) : WeatherDataSource {

    private val weatherService = service.create(WeatherAPI::class.java)

    override suspend fun getWeatherData(todayDate: String, currentTime: String): CommonResult<WeatherDto, WeatherFailEvent> {
        return weatherService.getWeatherData(baseDate = todayDate, baseTime = currentTime).run {
            if (isSuccessful) {
                body()?.run {
                    CommonResult.Success(data = toDto())
                } ?: kotlin.run {
                    CommonResult.Fail(failEvent = WeatherFailEvent.NoData)
                }
            } else {
                CommonResult.Fail(error = ErrorResponse(errorBody = errorBody()).toError())
            }
        }
    }

    private fun WeatherAPI.WeatherResponse.toDto(): WeatherDto {
        var sky = ""
        var pty = ""

        response.body.items?.item?.forEach { weatherItem ->
            if (weatherItem.category == "SKY") {
                sky = weatherItem.fcstValue
            } else if (weatherItem.category == "PTY") {
                pty = weatherItem.fcstValue
            }
        }

        return WeatherDto(
            sky = sky,
            pty = pty
        )
    }
}