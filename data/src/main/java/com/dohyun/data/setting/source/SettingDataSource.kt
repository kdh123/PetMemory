package com.dohyun.data.setting.source

interface SettingDataSource {
    suspend fun getIsLogin(): Boolean
    suspend fun updateIsLogin(isLogin: Boolean)

    suspend fun getIsMapGuideCheck(): Boolean
    suspend fun updateIsMapGuideCheck(isCheck: Boolean)
}