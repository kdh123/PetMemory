package com.dohyun.data.diary.source

import com.dohyun.domain.diary.DiaryData

interface DiaryDataSource {
    suspend fun getDiaryCount(): Int
    suspend fun getLocationDiaryCount(): Int
    suspend fun getAllDiary(): List<DiaryData>?
    suspend fun getDiary(startIndex: Int, offset: Int): List<DiaryData>?
    suspend fun getLocationDiary(startIndex: Int, offset: Int): List<DiaryData>?
    suspend fun getDiaryInfo(diaryId: String): DiaryData?
    suspend fun saveDiary(diaryData: DiaryData)
    suspend fun deleteDiary(diaryId: String)

    suspend fun updateDiary(diaryData: DiaryData)
}