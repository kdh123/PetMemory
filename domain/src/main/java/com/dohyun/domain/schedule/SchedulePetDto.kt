package com.dohyun.domain.schedule

data class SchedulePetDto(
    val petId: Int = 0,
    val petBigType: Int,
    val petType: String,
    val petName: String,
    val petAge: Int,
    val petBirthDay: String,
    val petSinceDay: String,
    val petWeight: Double,
    val petSex: Int,
    val petImageUrl: String,
    val isSelected: Boolean = false
)
