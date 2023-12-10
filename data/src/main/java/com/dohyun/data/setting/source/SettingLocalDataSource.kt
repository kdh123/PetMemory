package com.dohyun.data.setting.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.dohyun.data.Util.PREF_KEY_IS_LOGIN
import com.dohyun.data.Util.PREF_KEY_IS_MAP_GUIDE_CHECK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingLocalDataSource(private val dataStore: DataStore<Preferences>) : SettingDataSource {
    override suspend fun getIsLogin(): Boolean {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_IS_LOGIN] ?: false
            }.first()
    }

    override suspend fun updateIsLogin(isLogin: Boolean) {
        dataStore.edit { settings ->
            settings[PREF_KEY_IS_LOGIN] = isLogin
        }
    }

    override suspend fun getIsMapGuideCheck(): Boolean {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_IS_MAP_GUIDE_CHECK] ?: false
            }.first()
    }

    override suspend fun updateIsMapGuideCheck(isCheck: Boolean) {
        dataStore.edit { settings ->
            settings[PREF_KEY_IS_MAP_GUIDE_CHECK] = isCheck
        }
    }
}