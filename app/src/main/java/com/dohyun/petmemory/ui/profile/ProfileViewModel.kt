package com.dohyun.petmemory.ui.profile

import androidx.lifecycle.viewModelScope
import com.dohyun.domain.pet.PetDto
import com.dohyun.domain.pet.PetRepository
import com.dohyun.domain.setting.SettingRepository
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.extension.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val settingRepository: SettingRepository
) : StateViewModel<ProfileState>(ProfileState.None) {

    fun getProfile() {
        viewModelScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                val petList = petRepository.getAllPet().reversed()

                _state.value = ProfileState.SuccessLoad(petList = petList)
            })
    }

    fun saveProfile(petDto: PetDto, isUpdate: Boolean) {
        viewModelScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                if (isUpdate) {
                    petRepository.updatePet(petDto = petDto)
                } else {
                    petRepository.savePet(petDto = petDto)
                }

                settingRepository.run {
                    if (!getIsLogin()) {
                        updateIsLogin(isLogin = true)
                    }
                }

                _state.value = ProfileState.SuccessSave(petDto = petDto)
            })
    }
}