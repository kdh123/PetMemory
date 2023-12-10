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

    private val _profileUiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.Loading)
    val profileUiState = _profileUiState.asStateFlow()

    fun initState(diaryData: DiaryData? = null) {
        _diaryWriteUiState.value = diaryData?.let {
            DiaryWriteUiState.Writing(diaryData = it.copy(imageUrl = it.imageUrl + ""))
        } ?: kotlin.run {
            val diaryData = DiaryData(
                id = "${System.currentTimeMillis()}",
                date = DateUtil.todayDate(),
                imageUrl = listOf("")
            )
            DiaryWriteUiState.Writing(diaryData = diaryData)
        }
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

            _profileUiState.value = ProfileUiState.Selected(profiles = initProfiles)
        })
    }

    fun selectProfile(position: Int) {
        val state = _profileUiState.value.let { uiState ->
            if (uiState !is ProfileUiState.Selected) {
                return
            } else {
                uiState
            }
        }

        val petList = state.profiles.mapIndexed { index, selectedProfile ->
            if (index == position) {
                selectedProfile.copy(isSelected = true)
            } else {
                selectedProfile.copy(isSelected = false)
            }
        }

        _profileUiState.value = state.copy(profiles = petList)
    }

    fun addImageList(imageUrl: String) {
        val state = _diaryWriteUiState.value.let { uiState ->
            if (uiState !is DiaryWriteUiState.Writing) {
                return
            } else {
                uiState
            }
        }
        val path = mediaUtil.convertUriToPath(Uri.parse(imageUrl)) ?: return
        val imageList = listOf(path) + state.diaryData.imageUrl

        _diaryWriteUiState.value = state.copy(diaryData = state.diaryData.copy(imageUrl = imageList))
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
                val selectedProfile = (_profileUiState.value as? ProfileUiState.Selected)
                    ?.profiles
                    ?.filter { it.isSelected }
                    ?.get(0)?.petDto

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
    data class Writing(val diaryData: DiaryData) : DiaryWriteUiState
    data class Save(val diaryData: DiaryData) : DiaryWriteUiState
    data class Fail(val message: String?) : DiaryWriteUiState
}

sealed interface ProfileUiState {
    object None : ProfileUiState
    object Loading : ProfileUiState
    data class Selected(val profiles: List<SelectedProfile>) : ProfileUiState
    data class Fail(val message: String?) : ProfileUiState
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