package com.dohyun.petmemory.ui.diary

sealed interface DiaryDetailUiState {
    object Loading: DiaryDetailUiState
    data class Success(val diary: DiaryDetail?): DiaryDetailUiState
    data class Fail(val message: String? = null): DiaryDetailUiState
}