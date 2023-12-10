package com.dohyun.domain.diary

import com.dohyun.domain.pet.PetDto
import java.io.Serializable

data class DiaryData(
    val id: String,
    val title: String = "",
    val date: String,
    val content: String? = "",
    val imageUrl: List<String> = listOf(),
    val lat: Double? = 0.0,
    val lng: Double? = 0.0,
    val pet: PetDto? = null
) : Serializable