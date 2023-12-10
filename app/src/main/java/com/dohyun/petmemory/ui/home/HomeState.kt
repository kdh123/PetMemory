package com.dohyun.petmemory.ui.home

import com.dohyun.domain.diary.DiaryData

sealed class HomeState {
    object None : HomeState()
    object Loading : HomeState()
    data class Load(
        val diaryList: List<DiaryData>?,
        val isLoadMore: Boolean
    ) : HomeState()
    data class Fail(val message : String) : HomeState()
}