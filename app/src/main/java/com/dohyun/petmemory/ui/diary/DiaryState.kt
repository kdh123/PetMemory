package com.dohyun.petmemory.ui.diary

import com.dohyun.domain.diary.DiaryData

sealed class DiaryState {
    object None : DiaryState()
    object Loading : DiaryState()
    data class Delete(val diaryData: DiaryData) : DiaryState()
    data class Edit(val diaryData: DiaryData) : DiaryState()
    data class Save(val diaryData: DiaryData) : DiaryState()
    data class Fail(val message: String) : DiaryState()
}