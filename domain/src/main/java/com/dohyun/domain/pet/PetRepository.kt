package com.dohyun.domain.pet

interface PetRepository {
    suspend fun getAllPet(): List<PetDto>
    suspend fun getPetInfo(petId: Int): PetDto?
    suspend fun savePet(petDto: PetDto)
    suspend fun deletePet(petId: Int)
    suspend fun updatePet(petDto: PetDto)
}