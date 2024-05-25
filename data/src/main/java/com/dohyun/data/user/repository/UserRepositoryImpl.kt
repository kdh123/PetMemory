package com.dohyun.data.user.repository

import com.dohyun.data.user.source.UserLocalDataSource
import com.dohyun.data.user.source.UserRemoteDataSource
import com.dohyun.domain.user.UserRepository

class UserRepositoryImpl(
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun getUserId(): String {
        return userLocalDataSource.getUserId()
    }

    override suspend fun updateUserId(id: String) {
        userLocalDataSource.updateUserId(id = id)
    }

    override suspend fun getUserNickname(): String {
        return userLocalDataSource.getUserId()
    }

    override suspend fun updateUserNickname(nickname: String) {
        userLocalDataSource.updateUserNickname(nickname)
    }

    override suspend fun getUserImageUrl(): String {
        return userLocalDataSource.getUserId()
    }

    override suspend fun updateUserImageUrl(imageUrl: String) {
        userLocalDataSource.updateUserImageUrl(imageUrl = imageUrl)
    }

    override suspend fun getPetName(): String {
        return userLocalDataSource.getPetName()
    }

    override suspend fun updatePetName(name: String) {
        userLocalDataSource.updatePetName(name)
    }

    override suspend fun getPetBigType(): Int {
        return userLocalDataSource.getPetBigType()
    }

    override suspend fun updatePetBigType(type: Int) {
        userLocalDataSource.updatePetBigType(type)
    }

    override suspend fun getPetType(): String {
        return userLocalDataSource.getPetType()
    }

    override suspend fun updatePetType(type: String) {
        userLocalDataSource.updatePetType(type)
    }

    override suspend fun getPetAge(): Int {
        return userLocalDataSource.getPetAge()
    }

    override suspend fun updatePetAge(age: Int) {
        userLocalDataSource.updatePetAge(age)
    }

    override suspend fun getPetBirthDay(): String {
        return userLocalDataSource.getPetBirthDay()
    }

    override suspend fun updatePetBirtDay(birthDay: String) {
        userLocalDataSource.updatePetBirtDay(birthDay)
    }

    override suspend fun getPetSince(): String {
        return userLocalDataSource.getPetSince()
    }

    override suspend fun updatePetSince(since: String) {
        userLocalDataSource.updatePetSince(since)
    }

    override suspend fun getPetSex(): Int {
        return userLocalDataSource.getPetSex()
    }

    override suspend fun updatePetSex(sex: Int) {
        userLocalDataSource.updatePetSex(sex)
    }

    override suspend fun getPetImageUrl(): String {
        return userLocalDataSource.getPetImageUrl()
    }

    override suspend fun updatePetImageUrl(imageUrl: String) {
        userLocalDataSource.updatePetImageUrl(imageUrl)
    }

    override suspend fun getPetWeight(): String {
        return userLocalDataSource.getPetWeight()
    }

    override suspend fun updatePetWeight(weight: String) {
        userLocalDataSource.updatePetWeight(weight)
    }
}