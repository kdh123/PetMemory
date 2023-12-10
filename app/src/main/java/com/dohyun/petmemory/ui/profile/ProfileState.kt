package com.dohyun.petmemory.ui.profile

import com.dohyun.domain.pet.PetDto

sealed class ProfileState {
    object None : ProfileState()
    object Loading : ProfileState()
    data class SuccessLoad(val petList: List<PetDto>) : ProfileState()
    data class SuccessSave(val petDto: PetDto) : ProfileState()
    data class Fail(val message: String) : ProfileState()
}