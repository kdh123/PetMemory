package com.dohyun.data.diary.reopository

import com.dohyun.data.diary.source.DiaryLocalDataSource
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.diary.DiaryRepository
import kotlinx.coroutines.flow.Flow

class DiaryRepositoryImpl(private val localDataSource: DiaryLocalDataSource) : DiaryRepository {

    override suspend fun getDiaryCount(): Int {
        return localDataSource.getDiaryCount()
    }

    override suspend fun getLocationDiaryCount(): Int {
        return localDataSource.getLocationDiaryCount()
    }

    override suspend fun getAllDiary(): Flow<List<DiaryData>> {
        return localDataSource.getAllDiary()
    }

    override suspend fun getDiary(startIndex: Int, offset: Int): List<DiaryData>? {
        return localDataSource.getDiary(startIndex = startIndex, offset = offset)
    }

    override suspend fun getLocationDiary(startIndex: Int, offset: Int): List<DiaryData>? {
        return localDataSource.getLocationDiary(startIndex = startIndex, offset = offset)
    }

    override suspend fun getDiaryInfo(diaryId: String): DiaryData? {
        return localDataSource.getDiaryInfo(diaryId = diaryId)
    }

    override suspend fun saveDiary(diaryData: DiaryData) {
        localDataSource.saveDiary(diaryData = diaryData)
    }

    override suspend fun deleteDiary(diaryId: String) {
        localDataSource.deleteDiary(diaryId = diaryId)
    }

    override suspend fun updateDiary(diaryData: DiaryData) {
        localDataSource.updateDiary(diaryData = diaryData)
    }
}