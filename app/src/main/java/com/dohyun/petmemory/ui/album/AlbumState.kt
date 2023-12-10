package com.dohyun.petmemory.ui.album

import com.dohyun.domain.diary.DiaryData

sealed class AlbumState {
    object None : AlbumState()
    object Loading : AlbumState()
    data class Load(
        val diaryList: List<DiaryData>?,
        val isLoadMore: Boolean
    ) : AlbumState()
    data class Fail(val message: String) : AlbumState()
}