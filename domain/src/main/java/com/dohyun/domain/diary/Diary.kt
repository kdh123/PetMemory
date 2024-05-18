package com.dohyun.domain.diary

import com.dohyun.domain.pet.Pet
import java.io.Serializable

data class Diary(
    val id: String,
    val title: String = "",
    val date: String,
    val content: String = "",
    val imageUrl: List<String> = listOf(),
    val lat: Double? = null,
    val lng: Double? = null,
    val pet: Pet? = null
) : Serializable