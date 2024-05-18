package com.dohyun.petmemory.ui.profile

import com.dohyun.domain.pet.Pet

sealed class ProfileState {
    object None : ProfileState()
    object Loading : ProfileState()
    data class SuccessLoad(val petList: List<Pet>) : ProfileState()
    data class SuccessSave(val pet: Pet) : ProfileState()
    data class Fail(val message: String) : ProfileState()
}