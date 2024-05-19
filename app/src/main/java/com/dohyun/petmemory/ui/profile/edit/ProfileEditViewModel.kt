package com.dohyun.petmemory.ui.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.pet.Pet
import com.dohyun.domain.pet.PetRepository
import com.dohyun.domain.setting.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileEditUiState> = MutableStateFlow(ProfileEditUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect: MutableSharedFlow<ProfileEditSideEffect> = MutableStateFlow(ProfileEditSideEffect.None)
    val sideEffect = _sideEffect.asSharedFlow()

    fun onAction(action: ProfileEditAction) {
        when (action) {
            is ProfileEditAction.Load -> {
                getPetInfo(petId = action.petId)
            }

            is ProfileEditAction.Edit -> {
                if (action.isCompleted) {
                    editPet(pet = action.pet, isEdit = true)
                } else {
                    _uiState.value = _uiState.value.copy(pet = action.pet)
                }
            }

            is ProfileEditAction.Add -> {
                editPet(pet = action.pet, isEdit = false)
            }
        }
    }

    private fun getPetInfo(petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val pet = petRepository.getPetInfo(petId = petId)
            _uiState.value = _uiState.value.copy(pet = pet!!)
        }
    }

    private fun editPet(pet: Pet, isEdit: Boolean) {
        if (!isInputCompleted(pet = pet)) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (isEdit) {
                petRepository.updatePet(pet = pet)
            } else {
                petRepository.savePet(pet = pet)
            }

            settingRepository.run {
                if (!getIsLogin()) {
                    updateIsLogin(isLogin = true)
                }
            }

            _uiState.value = _uiState.value.copy(isCompleted = true)
        }
    }

    private fun isInputCompleted(pet: Pet): Boolean {
        val message = with(pet) {
            if (imageUrl.isEmpty()) {
                "이미지를 등록해주세요"
            } else if (type.isEmpty()) {
                "종을 입력해주세요"
            } else if (name.isEmpty()) {
                "이름을 입력해주세요"
            } else if (birthDay.isEmpty()) {
                "생일을 입력해주세요"
            } else if (sinceDay.isEmpty()) {
                "가족이 된 날짜를 입력해주세요"
            } else if (weight.isEmpty()) {
                "몸무게를 입력해주세요"
            } else if (weight.toDouble() <= 0.0) {
                "몸무게를 0보다 큰 값으로 입력해주세요"
            } else {
                ""
            }
        }

        return if (message.isEmpty()) {
            true
        } else {
            viewModelScope.launch {
                _sideEffect.emit(ProfileEditSideEffect.Message(message = message))
            }
            false
        }
    }
}