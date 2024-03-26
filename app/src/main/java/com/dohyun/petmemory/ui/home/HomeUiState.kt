package com.dohyun.petmemory.ui.home

import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.pet.PetDto
import com.naver.maps.geometry.LatLng

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val diaries: List<DiaryData>,
        val pets: List<PetDto>,
        val locations: List<LatLng>
    ) : HomeUiState
    data class Fail(val message: String?) : HomeUiState
}