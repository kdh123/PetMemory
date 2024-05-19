package com.dohyun.petmemory.ui.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.pet.Pet
import com.dohyun.domain.pet.PetRepository
import com.dohyun.domain.setting.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
}