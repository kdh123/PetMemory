package com.dohyun.petmemory.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.pet.Pet
import com.dohyun.domain.pet.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val petRepository: PetRepository
): ViewModel() {

    private val _profileDetailUiState: MutableStateFlow<ProfileEditUiState> = MutableStateFlow(ProfileEditUiState.Loading)
    val profileDetailUiState = _profileDetailUiState.asStateFlow()

    private val pet: MutableStateFlow<Pet> = MutableStateFlow(Pet())

    init {
        viewModelScope.launch {
            _profileDetailUiState.combine(pet) { uiState, pet ->
                when (uiState) {
                    is ProfileEditUiState.Loading -> {
                        ProfileEditUiState.Profile(pet = pet)
                    }

                    is ProfileEditUiState.Profile -> {
                        uiState.copy(pet = pet)
                    }
                }
            }.catch {

            }.collect {
                _profileDetailUiState.value = it
            }
        }
    }

    fun getProfile(petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petRepository.getPetInfo(petId = petId)?.let {
                pet.value = it
            }
        }
    }

    fun editProfile(pet: Pet) {

    }

}