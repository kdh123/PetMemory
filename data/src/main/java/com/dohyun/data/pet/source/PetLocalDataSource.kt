package com.dohyun.data.pet.source

import com.dohyun.data.room.AppDatabase
import com.dohyun.domain.pet.PetDto
import javax.inject.Inject

class PetLocalDataSource @Inject constructor(private val db: AppDatabase) : PetDataSource {

    private val petService = db.petDao()

    override suspend fun getAllPet(): List<PetDto> {
        return petService.getAllPet()?.map { it.toDto() } ?: emptyList()
    }

    override suspend fun getPetInfo(petId: Int): PetDto? {
        return petService.getPetInfo(petId = petId)?.toDto()
    }

    override suspend fun savePet(petDto: PetDto) {
        val entity = petDto.run {
            PetEntity(
                petId,
                petBigType,
                petType,
                petName,
                petAge,
                petBirthDay,
                petSinceDay,
                petWeight,
                petSex,
                petImageUrl
            )
        }

        return petService.savePet(petEntity = entity)
    }

    override suspend fun deletePet(petId: Int) {
        petService.deletePet(petId = petId)
    }

    override suspend fun updatePet(petDto: PetDto) {
        petService.updatePet(petDto = petDto)
    }
}