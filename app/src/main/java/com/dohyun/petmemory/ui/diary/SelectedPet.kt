package com.dohyun.petmemory.ui.diary

import com.dohyun.domain.pet.Pet

data class SelectedPet(
    val pet: Pet,
    val isSelected: Boolean = false
)
