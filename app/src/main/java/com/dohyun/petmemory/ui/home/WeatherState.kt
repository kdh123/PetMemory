package com.dohyun.petmemory.ui.home

sealed class WeatherState {
    object None : WeatherState()
    data class Success(val weather: String) : WeatherState()
    data class Fail(val message: String?) : WeatherState()
}
