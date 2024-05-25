package com.dohyun.data.pet.source

import com.dohyun.domain.pet.Pet
import kotlinx.coroutines.flow.Flow

interface PetDataSource {
    suspend fun getAllPet(): Flow<List<Pet>>
    suspend fun getPetInfo(petId: Int): Pet?
    suspend fun savePet(pet: Pet)
    suspend fun deletePet(petId: Int)
    suspend fun updatePet(pet: Pet)
}