package com.dohyun.petmemory.diary

import com.dohyun.domain.diary.Diary
import com.dohyun.domain.diary.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.lang.Error

class FakeDiaryRepository : DiaryRepository {

    private val diaries = mutableListOf<Diary>().apply {
        repeat(10) {
            val diary = if (it % 3 != 0) {
                Diary(
                    "id $it",
                    "title $it",
                    "date $it",
                    "content $it",
                    listOf("imageUrl $it"),
                    1.0,
                    1.0,
                )
            } else {
                Diary(
                    "id $it",
                    "title $it",
                    "date $it",
                    "content $it",
                    listOf("imageUrl $it"),
                )
            }
            add(diary)
        }
    }

    private var isError = false

    fun setError() {
        isError = true
    }

    override suspend fun getDiaryCount(): Int {
        return diaries.size
    }

    override suspend fun getLocationDiaryCount(): Int {
        return diaries.filter { it.lat != null && it.lng != null }.size
    }

    override suspend fun getAllDiary(): Flow<List<Diary>> {
        return flow {
            if (!isError) {
                emit(diaries)
            } else {
                throw Exception()
            }
        }
    }

    override suspend fun getDiary(startIndex: Int, offset: Int): List<Diary>? {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationDiary(startIndex: Int, offset: Int): List<Diary>? {
        TODO("Not yet implemented")
    }

    override suspend fun getDiaryInfo(diaryId: String): Diary? {
        TODO("Not yet implemented")
    }

    override suspend fun saveDiary(diary: Diary) {
        diaries.add(diary)
    }

    override suspend fun deleteDiary(diaryId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDiary(diary: Diary) {
        TODO("Not yet implemented")
    }
}