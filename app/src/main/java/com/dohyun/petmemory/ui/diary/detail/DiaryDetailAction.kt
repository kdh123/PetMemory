package com.dohyun.petmemory.ui.diary.detail

sealed interface DiaryDetailAction {
    data class Load(val diaryId: String) : DiaryDetailAction
    data class Delete(val diaryId: String) : DiaryDetailAction

}