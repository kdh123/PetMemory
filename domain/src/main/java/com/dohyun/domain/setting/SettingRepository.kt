package com.dohyun.domain.setting

interface SettingRepository {
    suspend fun getIsLogin(): Boolean
    suspend fun updateIsLogin(isLogin: Boolean)

    suspend fun getIsMapGuideCheck(): Boolean
    suspend fun updateIsMapGuideCheck(isCheck: Boolean)
}