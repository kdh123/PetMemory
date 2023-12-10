package com.dohyun.domain.pet

import com.dohyun.domain.schedule.SchedulePetDto
import java.io.Serializable

data class PetDto(
    val petId: Int = 0,
    val petBigType: Int,
    val petType: String,
    val petName: String,
    val petAge: Int,
    val petBirthDay: String,
    val petSinceDay: String,
    val petWeight: Double,
    val petSex: Int,
    val petImageUrl: String
) : Serializable {
    fun toSchedulePetDto(isSelected: Boolean): SchedulePetDto {
        return SchedulePetDto(
            petId,
            petBigType,
            petType,
            petName,
            petAge,
            petBirthDay,
            petSinceDay,
            petWeight,
            petSex,
            petImageUrl,
            isSelected
        )
    }
}
