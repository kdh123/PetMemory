package com.dohyun.domain.diary

data class DiaryResponse(
    val diaryList: List<Diary>,
    val indexAndOffset: DiaryIndexAndOffset
)