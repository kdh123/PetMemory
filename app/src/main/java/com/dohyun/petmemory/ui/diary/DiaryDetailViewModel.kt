package com.dohyun.petmemory.ui.diary

import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DeleteDiaryUseCase
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.diary.DiaryRepository
import com.dohyun.domain.diary.SaveDiaryUseCase
import com.dohyun.domain.diary.EditDiaryUseCase
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.extension.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val saveDiaryUseCase: SaveDiaryUseCase,
    private val editDiaryUseCase: EditDiaryUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase
) : StateViewModel<DiaryState>(DiaryState.None) {

    private val _uiState: MutableStateFlow<DiaryDetailUiState> = MutableStateFlow(DiaryDetailUiState.Loading)
    val uiState: StateFlow<DiaryDetailUiState> = _uiState.asStateFlow()

    private val diaryState = MutableStateFlow<DiaryData?>(null)

    init {
        viewModelScope.launch {
            _uiState.combine(diaryState) { uiState, diary ->
                when (uiState) {
                    is DiaryDetailUiState.Loading -> {
                        DiaryDetailUiState.Success(diary = diary)
                    }

                    is DiaryDetailUiState.Success -> {
                        uiState.copy(diary = diary)
                    }

                    is DiaryDetailUiState.Fail -> {
                        DiaryDetailUiState.Fail(message = "정보를 불러올 수 없습니다")
                    }
                }
            }.catch {
                DiaryDetailUiState.Fail(message = "정보를 불러올 수 없습니다")
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun getDiary(diaryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            diaryState.value = diaryRepository.getDiaryInfo(diaryId = diaryId)
            val a =  "ss"
        }
    }

    fun deleteDiary(diaryData: DiaryData) {
        _state.value = DiaryState.Loading

        viewModelScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                _state.value = DiaryState.Delete(diaryData = deleteDiaryUseCase(diaryData = diaryData))
            })
    }

    fun editDiary(diaryData: DiaryData) {
        viewModelScope.launch(Dispatchers.IO) {
            editDiaryUseCase(diaryData = diaryData)

            _state.value = DiaryState.Edit(diaryData = diaryData)
        }
    }

    suspend fun saveDiary(diaryData: DiaryData, imageNeedSaveToGalleryList: Set<Int>) {
        _state.value = DiaryState.Loading

        val data = saveDiaryUseCase(
            diaryData = diaryData,
            imageNeedSaveToGalleryList = imageNeedSaveToGalleryList
        )

        _state.value = DiaryState.Save(diaryData = data)
    }
}