package com.dohyun.domain.diary

data class DiaryResponse(
    val diaryList: List<DiaryData>,
    val indexAndOffset: DiaryIndexAndOffset
)