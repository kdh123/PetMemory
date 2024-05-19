package com.dohyun.petmemory.ui.album

import com.dohyun.domain.diary.Diary

data class AlbumUiState(
    val isLoading: Boolean = false,
    val diaries: List<Diary> = listOf()
)
