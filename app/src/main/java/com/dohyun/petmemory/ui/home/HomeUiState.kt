package com.dohyun.petmemory.ui.home

import com.dohyun.domain.diary.Diary
import com.dohyun.domain.pet.Pet

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val diaries: List<Diary>,
        val pets: List<Pet>
    ) : HomeUiState
    data class Fail(val message: String?) : HomeUiState
}