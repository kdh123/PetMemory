package com.dohyun.petmemory.ui.diary.detail

data class DiaryDetailUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val diary: DiaryDetail = DiaryDetail()
)
