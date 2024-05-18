package com.dohyun.petmemory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DiaryRepository
import com.dohyun.domain.pet.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel2 @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val petRepository: PetRepository
) : ViewModel() {

    private val _homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch {
                diaryRepository.getAllDiary().combine(petRepository.getAllPet()) { diaries, pets ->
                    HomeUiState.Success(
                        diaries = diaries,
                        pets = pets
                    )
                }.catch {
                    HomeUiState.Fail(
                        message = "데이터 불러오기를 실패하였습니다"
                    )
                }.collectLatest {
                    _homeUiState.value = it
                }
            }
        }
    }
}