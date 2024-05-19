package com.dohyun.petmemory.ui.profile

import com.dohyun.domain.pet.Pet

sealed interface ProfileEditAction {
    data class Load(val petId: Int) : ProfileEditAction
    data class Edit(val pet: Pet, val isCompleted: Boolean = false) : ProfileEditAction
    data class Add(val pet: Pet) : ProfileEditAction
}