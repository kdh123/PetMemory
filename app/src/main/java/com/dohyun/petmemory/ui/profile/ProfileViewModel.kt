package com.dohyun.petmemory.ui.profile

import androidx.lifecycle.viewModelScope
import com.dohyun.domain.pet.PetDto
import com.dohyun.domain.pet.PetRepository
import com.dohyun.domain.setting.SettingRepository
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.extension.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val settingRepository: SettingRepository
) : StateViewModel<ProfileState>(ProfileState.None) {

    private val _profileUiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.Loading)
    val profileUiState = _profileUiState.asStateFlow()

    private val profiles: MutableStateFlow<List<PetDto>> = MutableStateFlow(listOf())

    init {
        viewModelScope.launch {
            _profileUiState.combine(profiles) { uiState, profiles ->
                when (uiState) {
                    is ProfileUiState.Loading -> {
                        ProfileUiState.Success(profiles)
                    }

                    is ProfileUiState.Success -> {
                        uiState.copy(profiles = profiles)
                    }

                    else -> {
                        uiState
                    }
                }
            }.catch {
                _profileUiState.value = ProfileUiState.Fail(message = "프로필 정보를 불러올 수 없습니다")
            }.collect {
                _profileUiState.value = it
            }
        }

        viewModelScope.launch {
            getProfiles()
        }
    }

    fun getProfile() {
        viewModelScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                val petList = petRepository.getAllPet().reversed()

                _state.value = ProfileState.SuccessLoad(petList = petList)
            })
    }

    fun getProfiles() {
        viewModelScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                profiles.value = petRepository.getAllPet().reversed()
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