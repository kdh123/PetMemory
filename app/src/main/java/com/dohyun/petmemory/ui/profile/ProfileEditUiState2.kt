package com.dohyun.petmemory.ui.profile

import com.dohyun.domain.pet.Pet

data class ProfileEditUiState2(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val pet: Pet = Pet()
)
