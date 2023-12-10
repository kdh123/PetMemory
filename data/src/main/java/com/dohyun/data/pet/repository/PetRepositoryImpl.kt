package com.dohyun.data.pet.repository

import com.dohyun.data.pet.source.PetLocalDataSource
import com.dohyun.domain.pet.PetDto
import com.dohyun.domain.pet.PetRepository
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor(private val petLocalDataSource: PetLocalDataSource) :
    PetRepository {
    override suspend fun getAllPet(): List<PetDto> {
        return petLocalDataSource.getAllPet()
    }

    override suspend fun getPetInfo(petId: Int): PetDto? {
        return petLocalDataSource.getPetInfo(petId = petId)
    }

    override suspend fun savePet(petDto: PetDto) {
        petLocalDataSource.savePet(petDto = petDto)
    }

    override suspend fun deletePet(petId: Int) {
        petLocalDataSource.deletePet(petId = petId)
    }

    override suspend fun updatePet(petDto: PetDto) {
        petLocalDataSource.updatePet(petDto = petDto)
    }
}