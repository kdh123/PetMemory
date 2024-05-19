package com.dohyun.data.pet.source

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dohyun.domain.pet.Pet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "pet")
data class PetEntity(
    @PrimaryKey(autoGenerate = true) val petId: Int = 0,
    @ColumnInfo(name = "petBigType") val petBigType: Int,
    @ColumnInfo(name = "petType") val petType: String,
    @ColumnInfo(name = "petName") val petName: String,
    @ColumnInfo(name = "petAge") val petAge: Int,
    @ColumnInfo(name = "petBirthDay") val petBirthDay: String,
    @ColumnInfo(name = "petSinceDay") val petSinceDay: String,
    @ColumnInfo(name = "petWeight") val petWeight: Double,
    @ColumnInfo(name = "petSex") val petSex: Int,
    @ColumnInfo(name = "petImageUrl") val petImageUrl: String
) {
    fun toDto() : Pet{
        val thisYear = todayDate("yyyy").toInt()
        val birthYear = petBirthDay.substring(0, 4).toInt()

        return Pet(
            id = petId,
            bigType = petBigType,
            type = petType,
            name = petName,
            birthDay = petBirthDay,
            sinceDay = petSinceDay,
            weight = petWeight,
            sex = petSex,
            imageUrl = petImageUrl,
            age = thisYear - birthYear
        )
    }

    fun todayDate(pattern: String = "yyyy-MM-dd HH:mm"): String {
        val date = Date()
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
}

