package com.dohyun.petmemory.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel2 @Inject constructor(private val diaryRepository: DiaryRepository) : ViewModel() {

    private val _albumUiState: MutableStateFlow<AlbumUiState> = MutableStateFlow(AlbumUiState.Loading)
    val albumUiState = _albumUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _albumUiState.combine(diaryRepository.getAllDiary()) { state, diaries ->
                when (state) {
                    is AlbumUiState.Loading -> {
                        AlbumUiState.Album(diaries = diaries)
                    }
                    is AlbumUiState.Album -> {
                        state.copy(diaries = diaries)
                    }
                }
            }.catch {

            }.collect {
                _albumUiState.value = it
            }
        }
    }
}