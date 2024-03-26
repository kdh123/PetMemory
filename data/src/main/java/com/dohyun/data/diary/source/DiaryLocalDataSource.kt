package com.dohyun.data.diary.source

import com.dohyun.data.room.AppDatabase
import com.dohyun.data.toDiaryEntity
import com.dohyun.domain.diary.DiaryData
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

    override suspend fun getAllDiary(): Flow<List<DiaryData>> {
        return diaryService.getAllDiary().map { list ->
            list?.map { it.toDto() }?.sortedByDescending { it.date } ?: listOf()
        }
    }

    override suspend fun getDiary(startIndex: Int, offset: Int): List<DiaryData>? {
        return diaryService.getDiary(startIndex = startIndex, offset = offset)?.map {
            it.toDto()
        }
    }

    override suspend fun getLocationDiary(startIndex: Int, offset: Int): List<DiaryData>? {
        return diaryService.getLocationDiary(startIndex = startIndex, offset = offset)?.map {
            it.toDto()
        }
    }

    override suspend fun getDiaryInfo(diaryId: String): DiaryData? {
        return diaryService.getDiaryInfo(diaryId = diaryId)?.toDto()
    }

    override suspend fun saveDiary(diaryData: DiaryData) {
        diaryService.saveDiary(diary = diaryData.toDiaryEntity())
    }

    override suspend fun deleteDiary(diaryId: String) {
        diaryService.deleteDiary(diaryId = diaryId)
    }

    override suspend fun updateDiary(diaryData: DiaryData) {
        diaryService.updateDiary(diaryData = diaryData)
    }
}