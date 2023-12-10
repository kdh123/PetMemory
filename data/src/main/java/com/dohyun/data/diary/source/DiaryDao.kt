package com.dohyun.data.diary.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dohyun.domain.diary.DiaryData

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDiary(diary: DiaryEntity)

    @Query("DELETE FROM diary WHERE id = :diaryId")
    fun deleteDiary(diaryId: String)

    @Query("SELECT * FROM diary")
    fun getAllDiary(): List<DiaryEntity>?

    @Query("SELECT * FROM diary LIMIT :offset OFFSET :startIndex")
    fun getDiary(startIndex: Int, offset: Int): List<DiaryEntity>?

    @Query("SELECT * FROM diary WHERE lat != 0.0 AND lng != 0.0 LIMIT :offset OFFSET :startIndex ")
    fun getLocationDiary(startIndex: Int, offset: Int): List<DiaryEntity>?

    @Query("SELECT * FROM diary WHERE id = :diaryId")
    fun getDiaryInfo(diaryId: String): DiaryEntity?

    @Query("SELECT * FROM diary WHERE date = :startDate OR date = :endDate")
    fun getDiaryInDate(startDate: String, endDate: String): List<DiaryEntity>

    @Query("SELECT COUNT(*) FROM diary")
    fun getDiaryCount(): Int

    @Query("SELECT COUNT(*) FROM diary WHERE lat != 0.0 AND lng != 0.0")
    fun getLocationDiaryCount(): Int

    @Update(entity = DiaryEntity::class)
    fun updateDiary(diaryData: DiaryData)
}