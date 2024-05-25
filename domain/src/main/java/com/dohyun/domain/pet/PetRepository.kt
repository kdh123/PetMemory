package com.dohyun.domain.pet

import kotlinx.coroutines.flow.Flow

interface PetRepository {
    suspend fun getAllPet(): Flow<List<Pet>>
    suspend fun getPetInfo(petId: Int): Pet?
    suspend fun savePet(pet: Pet)
    suspend fun deletePet(petId: Int)
    suspend fun updatePet(pet: Pet)
}