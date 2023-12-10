package com.dohyun.domain.diary

import java.io.Serializable

data class DiaryItem(
    val id: String,
    val title: String,
    val date: String,
    val content: String,
    val imageUrl: List<String>,
    val lat: Double?,
    val lng: Double?
): Serializable {

}
