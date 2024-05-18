package com.dohyun.domain.diary

import javax.inject.Inject

class EditDiaryUseCase @Inject constructor(private val diaryRepository: DiaryRepository) {
    suspend operator fun invoke(diary: Diary) {
        diaryRepository.updateDiary(diary = diary)
    }
}