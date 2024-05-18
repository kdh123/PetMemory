package com.dohyun.petmemory.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    var uiState: MutableStateFlow<MapUiState> = MutableStateFlow(MapUiState.Loading)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            diaryRepository.getAllDiary().map { diaries ->
                diaries.filter { it.lat != null && it.lng != null }
            }.catch {
                uiState.value = MapUiState.Error(sideEffect = SideEffect.Message(message = "지도를 불러울 수 없습니다"))
            }.collect {
                uiState.value = MapUiState.Map(diaries = it)
            }
        }
    }
}