package com.dohyun.domain.pet

import java.io.Serializable

data class Pet(
    val petId: Int = 0,
    val petBigType: Int = 0,
    val petType: String = "",
    val petName: String = "",
    val petAge: Int = 0,
    val petBirthDay: String = "",
    val petSinceDay: String = "",
    val petWeight: Double = 0.0,
    val petSex: Int = 0,
    val petImageUrl: String = ""
) : Serializable
