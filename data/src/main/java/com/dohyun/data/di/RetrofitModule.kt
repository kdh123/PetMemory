package com.dohyun.data.di

import com.dohyun.data.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WeatherUrl

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ServerUrl

    private const val SERVER_URL = "https://www.naver.com/"
    private const val WEATHER_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

    @ServerUrl
    @Provides
    @Singleton
    fun serverBuilder(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
            .addConverterFactory (gsonConverterFactory)
            .build()
    }


    @WeatherUrl
    @Provides
    @Singleton
    fun weatherBuilder(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_URL)
            .client(client)
            .addConverterFactory (gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun client() : OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
        .build()

    @Provides
    @Singleton
    fun gsonConverterFactory(gson : Gson) : GsonConverterFactory = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun gson() : Gson = GsonBuilder()
        .setLenient()
        .create()
}