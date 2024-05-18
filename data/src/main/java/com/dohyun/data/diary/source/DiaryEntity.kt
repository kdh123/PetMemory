package com.dohyun.data.diary.source

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dohyun.domain.diary.Diary
import com.dohyun.domain.pet.Pet

@Entity(tableName = "diary")
data class DiaryEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "imageUrl") val imageUrl: List<String>,
    @ColumnInfo(name = "lat") val lat: Double? = null,
    @ColumnInfo(name = "lng") val lng: Double? = null,
    @ColumnInfo(name = "pet") val pet: Pet? = null
) {
    fun toDiary(): Diary {
        return Diary(
            id = id,
            title = title.ifEmpty {
                "(제목 없음)"
            },
            date = date,
            content = content?.ifEmpty {
                "(내용 없음)"
            } ?: "(내용 없음)",
            imageUrl = imageUrl,
            lat = lat,
            lng = lng,
            pet = pet
        )
    }
}
