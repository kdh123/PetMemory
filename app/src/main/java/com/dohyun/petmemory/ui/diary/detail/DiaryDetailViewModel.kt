package com.dohyun.petmemory.ui.diary.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DiaryRepository
import com.dohyun.petmemory.util.LocationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val locationUtil: LocationUtil
) : ViewModel() {

    private val _uiState: MutableStateFlow<DiaryDetailUiState> = MutableStateFlow(DiaryDetailUiState())
    val uiState: StateFlow<DiaryDetailUiState> = _uiState.asStateFlow()

    fun onAction(action: DiaryDetailAction) {
        when (action) {
            is DiaryDetailAction.Load -> {
                getDiary(diaryId = action.diaryId)
            }
            is DiaryDetailAction.Delete -> {
                deleteDiary(diaryId = action.diaryId)
            }
        }
    }

    private fun getDiary(diaryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val diary = diaryRepository.getDiaryInfo(diaryId = diaryId)!!
            val detail = diary.run {
                DiaryDetail(
                    id = diaryId,
                    title = title,
                    date = date,
                    content = content,
                    imageUrl = imageUrl,
                    lat = lat,
                    lng = lng,
                    address = locationUtil.getAddress(lat, lng )
                )
            }
            _uiState.value = _uiState.value.copy(diary = detail)
        }
    }

    private fun deleteDiary(diaryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            diaryRepository.deleteDiary(diaryId = diaryId)
            _uiState.value = _uiState.value.copy(isCompleted = true)
        }
    }
}