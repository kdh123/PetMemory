package com.dohyun.petmemory.ui.diary

import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.util.DateUtil

data class DiaryWriteUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val diary: Diary = Diary(
        id = "${System.currentTimeMillis()}",
        date = DateUtil.todayDate(),
        imageUrl = listOf("")
    ),
    val pets: List<SelectedPet> = listOf()
)
