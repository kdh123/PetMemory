package com.dohyun.data.user.source

interface UserDataSource {

    suspend fun getUserId(): String
    suspend fun updateUserId(id: String)

    suspend fun getUserNickname(): String
    suspend fun updateUserNickname(nickname: String)

    suspend fun getUserImageUrl(): String
    suspend fun updateUserImageUrl(imageUrl: String)

    suspend fun getPetName(): String
    suspend fun updatePetName(name: String)

    suspend fun getPetBigType(): Int
    suspend fun updatePetBigType(type: Int)

    suspend fun getPetType(): String
    suspend fun updatePetType(type: String)

    suspend fun getPetAge(): Int
    suspend fun updatePetAge(age: Int)

    suspend fun getPetBirthDay(): String
    suspend fun updatePetBirtDay(birthDay: String)

    suspend fun getPetSince(): String
    suspend fun updatePetSince(since: String)

    suspend fun getPetSex(): Int
    suspend fun updatePetSex(sex: Int)

    suspend fun getPetImageUrl(): String
    suspend fun updatePetImageUrl(imageUrl: String)

    suspend fun getPetWeight(): Double
    suspend fun updatePetWeight(weight: Double)
}