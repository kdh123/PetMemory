package com.dohyun.data

import com.dohyun.data.diary.source.DiaryEntity
import com.dohyun.domain.diary.Diary

fun Diary.toDiaryEntity() = DiaryEntity(
    id, title, date, content, imageUrl, lat, lng, pet
)
