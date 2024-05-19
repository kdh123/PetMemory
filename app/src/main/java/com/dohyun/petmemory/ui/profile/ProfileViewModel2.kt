package com.dohyun.petmemory.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.pet.Pet
import com.dohyun.domain.pet.PetRepository
import com.dohyun.domain.setting.SettingRepository
import com.dohyun.petmemory.extension.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel2 @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileUiState2> = MutableStateFlow(ProfileUiState2())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            petRepository.getAllPet().catch {

            }.collect {
                _uiState.value = _uiState.value.copy(pets = it)
            }
        }
    }
}