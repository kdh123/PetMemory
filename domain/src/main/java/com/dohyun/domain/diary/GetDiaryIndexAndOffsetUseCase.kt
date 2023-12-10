package com.dohyun.domain.diary

import javax.inject.Inject

class GetDiaryIndexAndOffsetUseCase @Inject constructor(private val diaryRepository: DiaryRepository) {

    suspend operator fun invoke(
        diaryListSize: Int,
        currentIndex: Int,
        currentOffset: Int,
        isPaging: Boolean = false
    ): DiaryIndexAndOffset {
        return if (isPaging) {
            pagingData(
                diaryListSize = diaryListSize,
                currentIndex = currentIndex,
                currentOffset = currentOffset
            )
        } else {
            initData(offset = currentOffset)
        }
    }

    suspend fun initData(offset: Int): DiaryIndexAndOffset {
        val diaryTotalCount = diaryRepository.getDiaryCount()

        return if (diaryTotalCount < offset) {
            DiaryIndexAndOffset(index = 0, offset = diaryTotalCount)
        } else {
            DiaryIndexAndOffset(index = diaryTotalCount - offset, offset = offset)
        }
    }

    private suspend fun pagingData(
        diaryListSize: Int,
        currentIndex: Int,
        currentOffset: Int
    ): DiaryIndexAndOffset {
        val diaryTotalCount = diaryRepository.getDiaryCount()
        var index = currentIndex - currentOffset
        var offset = currentOffset

        if (index < 0) {
            index = 0
            offset = diaryTotalCount - diaryListSize
        }

        return DiaryIndexAndOffset(index = index, offset = offset)
    }
}