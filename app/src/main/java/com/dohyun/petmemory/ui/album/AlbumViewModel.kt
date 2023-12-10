package com.dohyun.petmemory.ui.album

import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.diary.GetDiaryUseCase
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.extension.handle
import com.dohyun.petmemory.ui.diary.DiaryEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val getDiaryUseCase: GetDiaryUseCase
) : StateViewModel<AlbumState>(AlbumState.None) {

    var currentDiaryList = listOf<DiaryData>()

    private var currentStartIndex = 0
    private var currentOffset = 18

    fun getDiary(isPaging: Boolean = false) {
        _state.value = AlbumState.Loading

        viewModelScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                getDiaryUseCase(
                    currentDiaryListSize = currentDiaryList.size,
                    currentIndex = currentStartIndex,
                    offset = currentOffset,
                    isPaging = isPaging
                )?.run {
                    currentStartIndex = indexAndOffset.index
                    currentOffset = indexAndOffset.offset
                    currentDiaryList = currentDiaryList + diaryList

                    val isLoadMore = currentDiaryList.size >= currentOffset

                    _state.value = AlbumState.Load(diaryList = currentDiaryList, isLoadMore = isLoadMore)
                } ?: kotlin.run {
                    _state.value = AlbumState.Load(diaryList = currentDiaryList, isLoadMore = false)
                }
            })
    }

    fun commitSync(diaryEvent: DiaryEvent, diaryData: DiaryData) {
        _state.value = AlbumState.Loading

        when (diaryEvent) {
            DiaryEvent.Edit -> {
                currentDiaryList = currentDiaryList.map {
                    if (it.id == diaryData.id) {
                        diaryData.copy()
                    } else {
                        it.copy()
                    }
                }
            }

            DiaryEvent.Save -> {
                currentDiaryList = listOf(diaryData) + currentDiaryList.map { it.copy() }
            }

            DiaryEvent.Delete -> {
                currentDiaryList = currentDiaryList.filter { it.id != diaryData.id }
            }

            else -> {
            }
        }
    }
}