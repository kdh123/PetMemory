package com.dohyun.petmemory.ui.album

import com.dohyun.domain.diary.Diary

sealed interface AlbumUiState {
    object Loading: AlbumUiState
    data class Album(val diaries: List<Diary>): AlbumUiState
}