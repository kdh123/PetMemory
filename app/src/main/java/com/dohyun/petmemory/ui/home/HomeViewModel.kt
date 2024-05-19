package com.dohyun.petmemory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.Diary
import com.dohyun.domain.diary.DiaryRepository
import com.dohyun.domain.pet.Pet
import com.dohyun.domain.pet.PetRepository
import com.dohyun.petmemory.ui.diary.SelectedPet
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
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val petRepository: PetRepository
) : ViewModel() {

    private val _homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private var currentDiaries: List<Diary> = listOf()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch {
                diaryRepository.getAllDiary().combine(petRepository.getAllPet()) { diaries, pets ->
                    val selectedPets = if (_homeUiState.value.selectedPets.isEmpty()) {
                        pets.map {
                            SelectedPet(isSelected = true, pet = it)
                        }
                    } else {
                        pets.map { pet ->
                            val isSelected = try {
                                _homeUiState.value.selectedPets.filter { it.pet.id == pet.id }[0].isSelected
                            } catch (e: Exception) {
                                true
                            }
                            SelectedPet(isSelected = isSelected, pet = pet)
                        }
                    }

                    currentDiaries = diaries.map { diary ->
                        val petList = pets.filter { it.id == diary.pet?.id }
                        val pet = if (petList.isNotEmpty()) {
                            petList[0]
                        } else {
                            Pet()
                        }

                        diary.copy(pet = pet)
                    }

                    _homeUiState.value.copy(diaries = currentDiaries, selectedPets = selectedPets)
                }.catch {
                }.collectLatest {
                    _homeUiState.value = it
                }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.SelectPet -> {
                val state = _homeUiState.value
                val currentSelectedPets = state.selectedPets.map {
                    if (it.pet.id == action.selectedPet.pet.id) {
                        it.copy(isSelected = !it.isSelected)
                    } else {
                        it.copy()
                    }
                }
                val selectedPetIds = currentSelectedPets.filter { it.isSelected }.map { it.pet.id }
                val currentDiaries = currentDiaries.filter {
                    selectedPetIds.contains(it.pet?.id)
                }

                _homeUiState.value = state.copy(diaries = currentDiaries, selectedPets = currentSelectedPets)
            }
        }
    }
}