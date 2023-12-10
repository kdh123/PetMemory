package com.dohyun.data.weather

import com.dohyun.data.Util
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    /**
     * http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?
     * serviceKey=UNIJQ7Xe7FsqLn87Ah3aGJAzhbzFeyKOrDjJKV75DWZq6F2iwTM6UDguB2jWsvK3c1ZMzFtB%2B55Wi5bpvboiwQ%3D%3D
     * &numOfRows=10&pageNo=1&base_date=20230624&base_time=2000&nx=37&ny=126&dataType=JSON
     * */

    @GET("getVilageFcst?serviceKey=${Util.WEATHER_API_KEY}&numOfRows=10&pageNo=1&nx=37&ny=126&dataType=JSON")
    suspend fun getWeatherData(
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
    ): Response<WeatherResponse?>

    data class WeatherResponse(
        val response: WeatherResponseData,
    )

    data class WeatherResponseData(
        val header: WeatherHeader,
        val body: WeatherBody
    )

    data class WeatherHeader(
        val resultCode: String,
        val resultMsg: String
    )

    data class WeatherBody(
        val dataType: String?,
        val items: WeatherItems?,
        val pageNo: Int?,
        val numOfRows: Int?,
        val totalCount: Int?
    )

    data class WeatherItems(
        val item: List<WeatherItem>
    )
    /**
     * {"baseDate":"20230624","baseTime":"2000","category":"UUU","fcstDate":"20230624","fcstTime":"2100","fcstValue":"2.7","nx":37,"ny":126}
     * */

    data class WeatherItem(
        val baseData: String,
        val baseTime: String,
        val category: String,
        val fcstDate: String,
        val fcstTime: String,
        val fcstValue: String,
        val nx: Int,
        val ny: Int
    )
}