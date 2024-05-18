package com.dohyun.petmemory.ui.profile

import com.dohyun.domain.pet.Pet

sealed interface ProfileUiState {
    object Loading: ProfileUiState
    data class Success(val profiles: List<Pet>): ProfileUiState
    data class Fail(val message: String? = null): ProfileUiState
}