package com.dohyun.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dohyun.data.diary.source.DiaryDao
import com.dohyun.data.diary.source.DiaryEntity
import com.dohyun.data.pet.source.PetDao
import com.dohyun.data.pet.source.PetEntity

@Database(entities = [DiaryEntity::class, PetEntity::class], version = 6)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun petDao(): PetDao
}