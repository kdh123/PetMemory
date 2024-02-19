package com.dohyun.petmemory.ui.profile

import com.dohyun.domain.pet.PetDto

sealed interface ProfileUiState {
    object Loading: ProfileUiState
    data class Success(val profiles: List<PetDto>): ProfileUiState
    data class Fail(val message: String? = null): ProfileUiState
}