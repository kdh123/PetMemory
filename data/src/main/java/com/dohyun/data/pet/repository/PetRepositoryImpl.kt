package com.dohyun.data.pet.repository

import com.dohyun.data.pet.source.PetLocalDataSource
import com.dohyun.domain.pet.Pet
import com.dohyun.domain.pet.PetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor(private val petLocalDataSource: PetLocalDataSource) :
    PetRepository {
    override suspend fun getAllPet(): Flow<List<Pet>> {
        return petLocalDataSource.getAllPet()
    }

    override suspend fun getPetInfo(petId: Int): Pet? {
        return petLocalDataSource.getPetInfo(petId = petId)
    }

    override suspend fun savePet(pet: Pet) {
        petLocalDataSource.savePet(pet = pet)
    }

    override suspend fun deletePet(petId: Int) {
        petLocalDataSource.deletePet(petId = petId)
    }

    override suspend fun updatePet(pet: Pet) {
        petLocalDataSource.updatePet(pet = pet)
    }
}