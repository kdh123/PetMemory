package com.dohyun.domain.diary

import com.dohyun.domain.pet.Pet
import com.dohyun.domain.user.UserRepository
import javax.inject.Inject

class SaveDiaryUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(diary: Diary): Diary {
        var data = diary

        if (data.pet == null) {
           data = data.copy(pet = getPetDto())
        }

        diaryRepository.saveDiary(diary = data.copy(imageUrl = data.imageUrl.filter { it.isNotEmpty() }))

        return data
    }

    private suspend fun getPetDto(): Pet {
        return userRepository.run {
            Pet(
                petBigType = getPetBigType(),
                petType = getPetType(),
                petName = getPetName(),
                petAge = getPetAge(),
                petBirthDay = getPetBirthDay(),
                petSinceDay = getPetSince(),
                petWeight = getPetWeight(),
                petSex = getPetSex(),
                petImageUrl = getPetImageUrl()
            )
        }
    }
}