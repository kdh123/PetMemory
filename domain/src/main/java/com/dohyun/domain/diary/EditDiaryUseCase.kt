package com.dohyun.domain.diary

import javax.inject.Inject

class EditDiaryUseCase @Inject constructor(private val diaryRepository: DiaryRepository) {
    suspend operator fun invoke(diaryData: DiaryData) {
        diaryRepository.updateDiary(diaryData = diaryData)
    }
}