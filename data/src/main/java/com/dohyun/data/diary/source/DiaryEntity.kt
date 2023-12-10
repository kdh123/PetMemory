package com.dohyun.data.diary.source

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.pet.PetDto

@Entity(tableName = "diary")
data class DiaryEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "imageUrl") val imageUrl: List<String>,
    @ColumnInfo(name = "lat") val lat: Double? = null,
    @ColumnInfo(name = "lng") val lng: Double? = null,
    @ColumnInfo(name = "pet") val petDto: PetDto? = null
) {
    fun toDto(): DiaryData {
        return DiaryData(
            id = id,
            title = title.ifEmpty {
                "(제목 없음)"
            },
            date = date,
            content = content,
            imageUrl = imageUrl,
            lat = lat ?: 0.0,
            lng = lng ?: 0.0,
            pet = petDto
        )
    }
}
