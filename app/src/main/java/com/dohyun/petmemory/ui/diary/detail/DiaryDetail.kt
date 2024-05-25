package com.dohyun.petmemory.ui.diary.detail

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.dohyun.domain.diary.Diary

@Stable
@kotlinx.parcelize.Parcelize
data class DiaryDetail(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val content: String = "",
    val imageUrl: List<String> = listOf(),
    val lat: Double? = null,
    val lng: Double? = null,
    val address: String = "",
): Parcelable {
    fun toDiary() = Diary(
        id = id,
        title = title,
        date = date,
        content = content,
        imageUrl = imageUrl,
        lat = lat,
        lng = lng
    )
}
