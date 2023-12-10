package com.dohyun.data

import com.dohyun.data.diary.source.DiaryEntity
import com.dohyun.domain.diary.DiaryData

fun DiaryData.toDiaryEntity() = DiaryEntity(
    id, title, date, content, imageUrl, lat, lng, pet
)
