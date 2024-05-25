package com.dohyun.petmemory.ui.profile

import com.dohyun.domain.pet.Pet

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val pets: List<Pet> = listOf()
)
