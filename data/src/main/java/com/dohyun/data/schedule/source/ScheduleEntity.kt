package com.dohyun.data.schedule.source

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dohyun.domain.schedule.ScheduleDto

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val scheduleId: Int = 0,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "petName") val petName: String,
    @ColumnInfo(name = "petImageUrl") val petImageUrl: String
) {
    fun toDto(): ScheduleDto {
        return ScheduleDto(
            scheduleId = scheduleId,
            date = date,
            title = title,
            content = content.ifEmpty { "(내용 없음)" },
            petName = petName,
            petImageUrl = petImageUrl
        )
    }
}

