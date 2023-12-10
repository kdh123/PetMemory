package com.dohyun.domain.weather

data class WeatherDto(
    val sky: String,
    val pty: String
) {
    companion object {
        const val SKY_GOOD = "1"
        const val SKY_CLOUDY = "3"
        const val SKY_BAD = "4"

        const val PTY_NO = "0"
        const val PTY_RAIN = "1"
        const val PTY_RAIN_OR_SNOW = "2"
        const val PTY_SNOW = "3"
        const val PTY_RAIN_LITTLE = "5"
        const val PTY_RAIN_SPREAD = "6"
        const val PTY_SNOW_SPREAD = "7"
    }
}