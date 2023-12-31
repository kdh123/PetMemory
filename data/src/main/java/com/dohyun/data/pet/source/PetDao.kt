package com.dohyun.data.pet.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dohyun.domain.pet.PetDto

@Dao
interface PetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePet(petEntity: PetEntity)

    @Query("DELETE FROM pet WHERE petId = :petId")
    fun deletePet(petId: Int)

    @Query("SELECT * FROM pet")
    fun getAllPet(): List<PetEntity>?

    @Query("SELECT * FROM pet WHERE petId = :petId")
    fun getPetInfo(petId: Int): PetEntity?

    @Update(entity = PetEntity::class)
    fun updatePet(petDto: PetDto)
}