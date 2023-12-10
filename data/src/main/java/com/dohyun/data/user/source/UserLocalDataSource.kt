package com.dohyun.data.user.source

import androidx.datastore.core.DataStore
import com.dohyun.petmemory.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserLocalDataSource(private val dataStore: DataStore<User.UserInfoData>) : UserDataSource {

    override suspend fun getUserId(): String {
        return dataStore.data.map { user ->
            user.userId
        }.first()
    }

    override suspend fun updateUserId(id: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setUserId(id)
                .build()
        }
    }

    override suspend fun getUserNickname(): String {
        return dataStore.data.map { user ->
            user.userNickname
        }.first()
    }

    override suspend fun updateUserNickname(nickname: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setUserNickname(nickname)
                .build()
        }
    }

    override suspend fun getUserImageUrl(): String {
        return dataStore.data.map { user ->
            user.userImageUrl
        }.first()
    }

    override suspend fun updateUserImageUrl(imageUrl: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setUserImageUrl(imageUrl)
                .build()
        }
    }

    override suspend fun getPetName(): String {
        return dataStore.data.map { user ->
            user.petName
        }.first()
    }

    override suspend fun updatePetName(name: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetName(name)
                .build()
        }
    }

    override suspend fun getPetBigType(): Int {
        return dataStore.data.map { user ->
            user.petBigType
        }.first()
    }

    override suspend fun updatePetBigType(type: Int) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetBigType(type)
                .build()
        }
    }

    override suspend fun getPetType(): String {
        return dataStore.data.map { user ->
            user.petType
        }.first()
    }

    override suspend fun updatePetType(type: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetType(type)
                .build()
        }
    }

    override suspend fun getPetAge(): Int {
        return dataStore.data.map { user ->
            user.petAge
        }.first()
    }

    override suspend fun updatePetAge(age: Int) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetAge(age)
                .build()
        }
    }

    override suspend fun getPetBirthDay(): String {
        return dataStore.data.map { user ->
            user.petBirthDay
        }.first()
    }

    override suspend fun updatePetBirtDay(birthDay: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetBirthDay(birthDay)
                .build()
        }
    }

    override suspend fun getPetSex(): Int {
        return dataStore.data.map { user ->
            user.petSex
        }.first()
    }

    override suspend fun updatePetSex(sex: Int) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetSex(sex)
                .build()
        }
    }

    override suspend fun getPetImageUrl(): String {
        return dataStore.data.map { user ->
            user.petImageUrl
        }.first()
    }

    override suspend fun updatePetImageUrl(imageUrl: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetImageUrl(imageUrl)
                .build()
        }
    }

    override suspend fun getPetSince(): String {
        return dataStore.data.map { user ->
            user.petSince
        }.first()
    }

    override suspend fun updatePetSince(since: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetSince(since)
                .build()
        }
    }

    override suspend fun getPetWeight(): Double {
        return dataStore.data.map { user ->
            user.petWeight
        }.first()
    }

    override suspend fun updatePetWeight(weight: Double) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setPetWeight(weight)
                .build()
        }
    }
}