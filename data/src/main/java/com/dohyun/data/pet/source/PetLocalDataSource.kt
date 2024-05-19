package com.dohyun.data.pet.source

import com.dohyun.data.room.AppDatabase
import com.dohyun.domain.pet.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PetLocalDataSource @Inject constructor(private val db: AppDatabase) : PetDataSource {

    private val petService = db.petDao()

    override suspend fun getAllPet(): Flow<List<Pet>> {
        return petService.getAllPet().map { list -> list?.map { it.toDto() }?.reversed() ?: listOf() }
    }

    override suspend fun getPetInfo(petId: Int): Pet? {
        return petService.getPetInfo(petId = petId)?.toDto()
    }

    override suspend fun savePet(pet: Pet) {
        val entity = pet.run {
            PetEntity(
                id,
                bigType,
                type,
                name,
                age,
                birthDay,
                sinceDay,
                weight.toDouble(),
                sex,
                imageUrl
            )
        }

        return petService.savePet(petEntity = entity)
    }

    override suspend fun deletePet(petId: Int) {
        petService.deletePet(petId = petId)
    }

    override suspend fun updatePet(pet: Pet) {
        val entity = pet.run {
            PetEntity(
                id,
                bigType,
                type,
                name,
                age,
                birthDay,
                sinceDay,
                weight.toDouble(),
                sex,
                imageUrl
            )
        }
        petService.updatePet(petEntity = entity)
    }
}