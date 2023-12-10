package com.dohyun.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dohyun.data.PetUserSerializer
import com.dohyun.petmemory.User
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private val Context.settings: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

    @Provides
    @Singleton
    fun dataStoreBuilder(@ApplicationContext context: Context): DataStore<User.UserInfoData> {
        return DataStoreFactory.create(
            serializer = PetUserSerializer,
            produceFile = { context.dataStoreFile("pet_user.pb") },
        )
    }

    @Provides
    @Singleton
    fun provideDataStorePreferences(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.settings
    }
}