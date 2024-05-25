package com.dohyun.data.diary.source

import com.dohyun.data.room.AppDatabase
import com.dohyun.data.toDiaryEntity
import com.dohyun.domain.diary.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiaryLocalDataSource @Inject constructor(private val db: AppDatabase) : DiaryDataSource {

    private val diaryService = db.diaryDao()

    override suspend fun getDiaryCount(): Int {
        return diaryService.getDiaryCount()
    }

    override suspend fun getLocationDiaryCount(): Int {
        return diaryService.getLocationDiaryCount()
    }

    override suspend fun getAllDiary(): Flow<List<Diary>> {
        return diaryService.getAllDiary().map { list ->
            list?.map { it.toDiary() }?.sortedByDescending { it.date } ?: listOf()
        }
    }

    override suspend fun getDiary(startIndex: Int, offset: Int): List<Diary>? {
        return diaryService.getDiary(startIndex = startIndex, offset = offset)?.map {
            it.toDiary()
        }
    }

    override suspend fun getLocationDiary(startIndex: Int, offset: Int): List<Diary>? {
        return diaryService.getLocationDiary(startIndex = startIndex, offset = offset)?.map {
            it.toDiary()
        }
    }

    override suspend fun getDiaryInfo(diaryId: String): Diary? {
        return diaryService.getDiaryInfo(diaryId = diaryId)?.toDiary()
    }

    override suspend fun saveDiary(diary: Diary) {
        diaryService.saveDiary(diary = diary.toDiaryEntity())
    }

    override suspend fun deleteDiary(diaryId: String) {
        diaryService.deleteDiary(diaryId = diaryId)
    }

    override suspend fun updateDiary(diary: Diary) {
        diaryService.updateDiary(diary = diary)
    }
}