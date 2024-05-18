package com.dohyun.petmemory.ui.profile

import com.dohyun.domain.pet.Pet

sealed interface ProfileEditUiState {
    object Loading: ProfileEditUiState
    data class Profile(val pet: Pet): ProfileEditUiState
}