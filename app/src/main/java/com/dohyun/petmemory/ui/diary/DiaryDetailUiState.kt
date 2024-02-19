package com.dohyun.petmemory.ui.diary

import com.dohyun.domain.diary.DiaryData

sealed interface DiaryDetailUiState {
    object Loading: DiaryDetailUiState
    data class Success(val diary: DiaryData?): DiaryDetailUiState
    data class Fail(val message: String? = null): DiaryDetailUiState
}