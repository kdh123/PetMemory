package com.dohyun.petmemory.ui.diary

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.Diary
import com.dohyun.domain.diary.SaveDiaryUseCase
import com.dohyun.domain.diary.EditDiaryUseCase
import com.dohyun.domain.pet.Pet
import com.dohyun.domain.pet.PetRepository
import com.dohyun.petmemory.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val saveDiaryUseCase: SaveDiaryUseCase,
    private val editDiaryUseCase: EditDiaryUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<DiaryWriteUiState> = MutableStateFlow(DiaryWriteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val pets = petRepository.getAllPet().map { pets ->
                pets.mapIndexed { index, pet ->
                    if (index == 0) {
                        SelectedPet(pet = pet, isSelected = true)
                    } else {
                        SelectedPet(pet = pet, isSelected = false)
                    }
                }
            }.first()

            _uiState.value = _uiState.value.copy(
                pets = pets
            )
        }
    }

    fun onAction(action: DiaryWriteAction) {
        val state = _uiState.value

        when (action) {
            is DiaryWriteAction.Edit -> {
                val diary = action.diary ?: Diary(
                    id = "${System.currentTimeMillis()}",
                    date = DateUtil.todayDate(),
                    imageUrl = listOf(""),
                )
                _uiState.value = state.copy(diary = diary)
            }

            is DiaryWriteAction.Image -> {
                with(action) {
                    if (isEdit) {
                        editImage(index, uri)
                    } else {
                        addImage(uri = uri)
                    }
                }
            }

            is DiaryWriteAction.SelectPet -> {
                selectPet(position = action.position)
            }

            is DiaryWriteAction.Save -> {
                with(action) {
                    saveDiary(title = title, content = content, isEdit = isEdit)
                }
            }
        }
    }

    private fun addImage(uri: Uri?) {
        if (uri == null) {
            return
        }

        val state = _uiState.value
        val images = (state.diary?.imageUrl?.filter { it.isNotEmpty() } ?: listOf()) + listOf(uri.toString())

        _uiState.value = state.copy(diary = state.diary.copy(imageUrl = images))
    }

    private fun editImage(index: Int, uri: Uri?) {
        val state = _uiState.value
        val images = state.diary?.imageUrl?.toMutableList() ?: mutableListOf()

        images[index] = uri.toString()
        _uiState.value = state.copy(diary = state.diary.copy(imageUrl = images))
    }

    private fun selectPet(position: Int) {
        val state = _uiState.value

        val pets = state.pets.mapIndexed { index, selectedPet ->
            if (index == position) {
                selectedPet.copy(isSelected = true)
            } else {
                selectedPet.copy(isSelected = false)
            }
        }

        _uiState.value = state.copy(pets = pets)
    }
    private fun saveDiary(title: String, content: String, isEdit: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = _uiState.value
            val selectedPet = state
                .pets
                .filter { it.isSelected }[0]
                .pet
            val diary = state.diary?.copy(
                title = title,
                content = content,
                pet = selectedPet
            )

            if (isEdit) {
                editDiaryUseCase(diary = diary!!)
            } else {
                saveDiaryUseCase(diary = diary!!)
            }

            _uiState.value = state.copy(isCompleted = true)
        }
    }
}

data class SelectedPet(
    val pet: Pet,
    val isSelected: Boolean
)