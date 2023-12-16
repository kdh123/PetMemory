package com.dohyun.petmemory.ui.diary

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.diary.SaveDiaryUseCase
import com.dohyun.domain.diary.EditDiaryUseCase
import com.dohyun.domain.pet.PetDto
import com.dohyun.domain.pet.PetRepository
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.extension.handle
import com.dohyun.petmemory.util.DateUtil
import com.dohyun.petmemory.util.MediaUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val saveDiaryUseCase: SaveDiaryUseCase,
    private val editDiaryUseCase: EditDiaryUseCase,
    private val mediaUtil: MediaUtil
) : StateViewModel<DiaryState>(DiaryState.None) {

    private val _diaryWriteUiState: MutableStateFlow<DiaryWriteUiState> = MutableStateFlow(
        DiaryWriteUiState.Writing(
            diaryData = DiaryData(
                id = "${System.currentTimeMillis()}",
                date = DateUtil.todayDate()
            )
        )
    )
    val diaryWriteUiState = _diaryWriteUiState.asStateFlow()

    private val diaryState: MutableStateFlow<DiaryData?> = MutableStateFlow(null)
    private val profileState: MutableStateFlow<List<SelectedProfile>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.handle(block = {
            _diaryWriteUiState.combine(diaryState) { uiState, data ->
                if (data == null) {
                    return@combine DiaryWriteUiState.None
                }

                when (uiState) {
                    is DiaryWriteUiState.Loading -> {
                        DiaryWriteUiState.Writing(diaryData = data)
                    }
                    is DiaryWriteUiState.Writing -> {
                        uiState.copy(diaryData = data)
                    }
                    else -> {
                        DiaryWriteUiState.None
                    }
                }
            }.combine(profileState) { uiState, profiles ->
                if (uiState is DiaryWriteUiState.Writing) {
                    if (profiles.isNotEmpty()) {
                        uiState.copy(profiles = profiles)
                    } else {
                        uiState
                    }
                } else {
                    uiState
                }
            }.catch {
                _diaryWriteUiState.value = DiaryWriteUiState.Fail("")
            }.collect {
                _diaryWriteUiState.value = it
            }
        })
    }

    fun initProfileState() {
        viewModelScope.handle(Dispatchers.IO, block = {
            val profiles = petRepository.getAllPet().reversed()
            val initProfiles = profiles.mapIndexed { index, petDto ->
                if (index == 0) {
                    petDto.selected().copy(isSelected = true)
                } else {
                    petDto.selected()
                }
            }

            profileState.value = initProfiles
        })
    }

    fun selectProfile(position: Int) {
        val state = _diaryWriteUiState.value.let { uiState ->
            if (uiState !is DiaryWriteUiState.Writing) {
                return
            } else {
                uiState
            }
        }

        val profiles = state.profiles?.mapIndexed { index, selectedProfile ->
            if (index == position) {
                selectedProfile.copy(isSelected = true)
            } else {
                selectedProfile.copy(isSelected = false)
            }
        } ?: return

        profileState.value = profiles
    }

    fun writing(diaryData: DiaryData?) {
        val data = diaryData ?: kotlin.run {
            DiaryData(
                id = "${System.currentTimeMillis()}",
                date = DateUtil.todayDate(),
                imageUrl = listOf("")
            )
        }

        diaryState.value = data
    }

    fun getPath(uri: Uri): String  {
        return mediaUtil.convertUriToPath(uri) ?: ""
    }

    fun saveDiary(title: String, content: String, isEdit: Boolean) {
        viewModelScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                var state = _diaryWriteUiState.value.let { uiState ->
                    if (uiState !is DiaryWriteUiState.Writing) {
                        return@handle
                    } else {
                        uiState
                    }
                }
                val selectedProfile = state
                    .profiles
                    ?.filter { it.isSelected }
                    ?.get(0)
                    ?.petDto

                state = state.copy(
                    diaryData = state.diaryData.copy(
                        title = title,
                        content = content,
                        pet = selectedProfile
                    )
                )

                if (isEdit) {
                    editDiaryUseCase(diaryData = state.diaryData)
                } else {
                    saveDiaryUseCase(
                        diaryData = state.diaryData,
                        imageNeedSaveToGalleryList = emptySet()
                    )
                }

                _diaryWriteUiState.value = DiaryWriteUiState.Save(state.diaryData)
            })
    }
}

sealed interface DiaryWriteUiState {
    object None : DiaryWriteUiState
    object Loading : DiaryWriteUiState
    data class Writing(val diaryData: DiaryData, val profiles: List<SelectedProfile>? = null) : DiaryWriteUiState
    data class Save(val diaryData: DiaryData) : DiaryWriteUiState
    data class Fail(val message: String?) : DiaryWriteUiState
}

data class SelectedProfile(
    val petDto: PetDto,
    val isSelected: Boolean
)

fun PetDto.selected(): SelectedProfile {
    return SelectedProfile(
        petDto = this,
        isSelected = false
    )
}