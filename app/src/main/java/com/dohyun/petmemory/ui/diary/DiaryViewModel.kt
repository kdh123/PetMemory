package com.dohyun.petmemory.ui.diary

import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DeleteDiaryUseCase
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.diary.SaveDiaryUseCase
import com.dohyun.domain.diary.EditDiaryUseCase
import com.dohyun.domain.pet.PetDto
import com.dohyun.domain.pet.PetRepository
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.extension.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val saveDiaryUseCase: SaveDiaryUseCase,
    private val editDiaryUseCase: EditDiaryUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase
) : StateViewModel<DiaryState>(DiaryState.None) {

    private var _petList = listOf<PetDto>()
    val petList
        get() = _petList

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

    suspend fun getPetList(): List<PetDto> {
        _petList = petRepository.getAllPet().reversed()

        return _petList
    }
}