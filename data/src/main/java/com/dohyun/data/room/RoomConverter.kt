package com.dohyun.data.room

import androidx.room.TypeConverter
import com.dohyun.domain.pet.PetDto
import com.google.gson.Gson

class RoomConverter {
    @TypeConverter
    fun listToJson(value: List<String>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String>? {
        return Gson().fromJson(value, Array<String>::class.java)?.toList()
    }

    @TypeConverter
    fun petDtoToJson(value: PetDto?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToPetDto(value: String?): PetDto? {
        return if (value != null) {
            Gson().fromJson(value, PetDto::class.java)
        } else {
            null
        }
    }
}