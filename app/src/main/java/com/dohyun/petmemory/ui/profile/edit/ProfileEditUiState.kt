package com.dohyun.petmemory.ui.profile.edit

import com.dohyun.domain.pet.Pet

data class ProfileEditUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val pet: Pet = Pet()
)
