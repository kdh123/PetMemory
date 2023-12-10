package com.dohyun.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dohyun.data.setting.repository.SettingRepositoryImpl
import com.dohyun.data.setting.source.SettingLocalDataSource
import com.dohyun.domain.setting.SettingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingModule {

    @Singleton
    @Provides
    fun bindSettingRepository(
        settingLocalDataSource: SettingLocalDataSource
    ): SettingRepository {
        return SettingRepositoryImpl(
            settingLocalDataSource = settingLocalDataSource
        )
    }

    @Singleton
    @Provides
    fun bindSettingLocalDataSource(dataStore: DataStore<Preferences>): SettingLocalDataSource {
        return SettingLocalDataSource(dataStore = dataStore)
    }
}