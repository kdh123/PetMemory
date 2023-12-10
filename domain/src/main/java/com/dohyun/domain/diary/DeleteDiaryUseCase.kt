package com.dohyun.domain.diary

import javax.inject.Inject

class DeleteDiaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(diaryData: DiaryData): DiaryData {
        diaryRepository.deleteDiary(diaryId = diaryData.id)

        return diaryData
    }
}