package com.dohyun.petmemory.ui.album

import com.dohyun.domain.diary.DiaryData

sealed interface AlbumUiState {
    object Loading: AlbumUiState
    data class Album(val diaries: List<DiaryData>): AlbumUiState
}