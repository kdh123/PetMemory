package com.dohyun.petmemory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DiaryRepository
import com.dohyun.domain.pet.PetRepository
import com.dohyun.petmemory.util.LocationUtil
import com.naver.maps.geometry.LatLng
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
class HomeViewModel2 @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val petRepository: PetRepository,
    private val locationUtil: LocationUtil
) : ViewModel() {

    private val _homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _sheetAlpha = MutableStateFlow(0f)
    val sheetAlpha = _sheetAlpha.asStateFlow()

    private val _isExpand: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isExpand = _isExpand.asSharedFlow()

    private val _showBottomSheet: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val showBottomSheet = _showBottomSheet.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            homeUiState.combine(diaryRepository.getAllDiary()) { state, diaries ->
                when (state) {
                    is HomeUiState.Loading -> {
                        _isExpand.emit(true)
                        _showBottomSheet.emit(true)

                        HomeUiState.Success(
                            diaries = diaries,
                            pets = listOf(),
                            locations = diaries
                                .filter { it.lat != 0.0 && it.lng != 0.0 }
                                .map { LatLng(it.lat!!, it.lng!!) }
                        )
                    }

                    is HomeUiState.Success -> {
                        _isExpand.emit(true)
                        state.copy(
                            diaries = diaries,
                            locations = diaries
                                .filter { it.lat != 0.0 && it.lng != 0.0 }
                                .map { LatLng(it.lat!!, it.lng!!) },
                        )
                    }

                    is HomeUiState.Fail -> {
                        state
                    }
                }
            }.catch {

            }.collect {
                _homeUiState.value = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            homeUiState.combine(petRepository.getAllPet()) { state, pets ->
                when (state) {
                    is HomeUiState.Loading -> {
                        _isExpand.emit(false)
                        _showBottomSheet.emit(true)

                        HomeUiState.Success(
                            diaries = listOf(),
                            pets = pets,
                            locations = listOf()
                        )
                    }

                    is HomeUiState.Success -> {
                        state.copy(pets = pets)
                    }

                    is HomeUiState.Fail -> {
                        state
                    }
                }
            }.catch {

            }.collect {
                _homeUiState.value = it
            }
        }
    }

    fun setSheetAlpha(offset: Float) {
        _sheetAlpha.value = offset
        viewModelScope.launch {
            if (offset < 0.1) {
                _showBottomSheet.emit(false)
            } else {
                _showBottomSheet.emit(true)
            }
        }
    }
}