package com.dohyun.petmemory.ui.home

import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.ui.diary.SelectedPet

data class HomeUiState(
    val isLoading: Boolean = false,
    val diaries: List<Diary> = listOf(),
    val selectedPets: List<SelectedPet> = listOf()
)
