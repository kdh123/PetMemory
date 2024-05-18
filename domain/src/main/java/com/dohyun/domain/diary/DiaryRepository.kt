package com.dohyun.domain.diary

import kotlinx.coroutines.flow.Flow

interface DiaryRepository {

    suspend fun getDiaryCount(): Int
    suspend fun getLocationDiaryCount(): Int
    suspend fun getAllDiary(): Flow<List<Diary>>
    suspend fun getDiary(startIndex: Int, offset: Int): List<Diary>?
    suspend fun getLocationDiary(startIndex: Int, offset: Int): List<Diary>?
    suspend fun getDiaryInfo(diaryId: String): Diary?
    suspend fun saveDiary(diary: Diary)
    suspend fun deleteDiary(diaryId: String)
    suspend fun updateDiary(diary: Diary)
}