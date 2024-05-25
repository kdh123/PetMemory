package com.dohyun.petmemory.ui.home

import com.dohyun.petmemory.ui.diary.SelectedPet

sealed interface HomeAction {
    data class SelectPet(val selectedPet: SelectedPet): HomeAction
}