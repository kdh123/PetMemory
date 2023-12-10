package com.dohyun.data.setting.repository

import com.dohyun.data.setting.source.SettingLocalDataSource
import com.dohyun.domain.setting.SettingRepository

class SettingRepositoryImpl(private val settingLocalDataSource: SettingLocalDataSource) :
    SettingRepository {
    override suspend fun getIsLogin(): Boolean {
        return settingLocalDataSource.getIsLogin()
    }

    override suspend fun updateIsLogin(isLogin: Boolean) {
        settingLocalDataSource.updateIsLogin(isLogin = isLogin)
    }

    override suspend fun getIsMapGuideCheck(): Boolean {
        return settingLocalDataSource.getIsMapGuideCheck()
    }

    override suspend fun updateIsMapGuideCheck(isCheck: Boolean) {
        settingLocalDataSource.updateIsMapGuideCheck(isCheck = isCheck)
    }
}