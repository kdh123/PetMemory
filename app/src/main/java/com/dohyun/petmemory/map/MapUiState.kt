package com.dohyun.petmemory.map

import com.dohyun.domain.diary.Diary

sealed interface MapUiState {

    object Loading: MapUiState
    data class Map(val diaries: List<Diary>): MapUiState
    data class Error(val sideEffect: SideEffect): MapUiState
}

sealed interface SideEffect {
    data class Message(val message: String): SideEffect
}