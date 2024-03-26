package com.dohyun.domain.pet

import kotlinx.coroutines.flow.Flow

interface PetRepository {
    suspend fun getAllPet(): Flow<List<PetDto>>
    suspend fun getPetInfo(petId: Int): PetDto?
    suspend fun savePet(petDto: PetDto)
    suspend fun deletePet(petId: Int)
    suspend fun updatePet(petDto: PetDto)
}