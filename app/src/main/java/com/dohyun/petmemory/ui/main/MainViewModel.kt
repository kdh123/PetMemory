package com.dohyun.petmemory.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dohyun.domain.pet.PetDto
import com.dohyun.domain.pet.PetRepository
import com.dohyun.domain.user.UserRepository
import com.dohyun.petmemory.extension.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun migrationPetProfile() {
        viewModelScope.handle(block = {
            val petProfiles = petRepository.getAllPet().first()

            if (petProfiles.isEmpty()) {
                val petDto = userRepository.run {
                    PetDto(
                        0,
                        getPetBigType(),
                        getPetType(),
                        getPetName(),
                        getPetAge(),
                        getPetBirthDay(),
                        getPetSince(),
                        getPetWeight(),
                        getPetSex(),
                        getPetImageUrl()
                    )
                }
                petRepository.savePet(petDto = petDto)
            }
        })
    }
}