package com.dohyun.petmemory.ui.diary

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.diary.DiaryRepository
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.util.LocationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val locationUtil: LocationUtil
) : StateViewModel<DiaryState>(DiaryState.None) {

    private val _uiState: MutableStateFlow<DiaryDetailUiState> = MutableStateFlow(DiaryDetailUiState.Loading)
    val uiState: StateFlow<DiaryDetailUiState> = _uiState.asStateFlow()

    private val _deleteFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val deleteFlow = _deleteFlow.asSharedFlow()

    private val diaryState = MutableStateFlow<DiaryDetail?>(null)

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
            val diary = diaryRepository.getDiaryInfo(diaryId = diaryId)!!
            val detail = diary.run {
                DiaryDetail(
                    id = diaryId,
                    title = title,
                    date = date,
                    content = content,
                    imageUrl = imageUrl,
                    lat = lat ?: 0.0,
                    lng = lng ?: 0.0,
                    address = locationUtil.getAddress(lat ?: 0.0, lng ?: 0.0)
                )
            }
            diaryState.value = detail
        }
    }

    fun deleteDiary(diaryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            diaryRepository.deleteDiary(diaryId = diaryId)
            _deleteFlow.emit(true)
        }
    }
}

@Stable
@kotlinx.parcelize.Parcelize
data class DiaryDetail(
    val id: String,
    val title: String = "",
    val date: String,
    val content: String? = "",
    val imageUrl: List<String> = listOf(),
    val lat: Double,
    val lng: Double,
    val address: String,
): Parcelable {
    fun toDiary() = DiaryData(
        id = id,
        title = title,
        date = date,
        content = content,
        imageUrl = imageUrl,
        lat = lat,
        lng = lng
    )
}