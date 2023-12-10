package com.dohyun.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.dohyun.petmemory.User.UserInfoData
import java.io.InputStream
import java.io.OutputStream

object PetUserSerializer : Serializer<UserInfoData> {
    override val defaultValue: UserInfoData = UserInfoData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserInfoData {
        try {
            return UserInfoData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: UserInfoData,
        output: OutputStream
    ) = t.writeTo(output)
}