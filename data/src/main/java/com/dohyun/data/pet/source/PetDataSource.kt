package com.dohyun.data.pet.source

import com.dohyun.domain.pet.PetDto
import kotlinx.coroutines.flow.Flow

interface PetDataSource {
    suspend fun getAllPet(): Flow<List<PetDto>>
    suspend fun getPetInfo(petId: Int): PetDto?
    suspend fun savePet(petDto: PetDto)
    suspend fun deletePet(petId: Int)
    suspend fun updatePet(petDto: PetDto)
}