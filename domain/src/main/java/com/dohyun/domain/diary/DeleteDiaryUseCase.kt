package com.dohyun.domain.diary

import javax.inject.Inject

class DeleteDiaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(diary: Diary): Diary {
        diaryRepository.deleteDiary(diaryId = diary.id)

        return diary
    }
}