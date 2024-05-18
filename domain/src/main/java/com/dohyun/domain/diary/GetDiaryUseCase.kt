package com.dohyun.domain.diary

import com.dohyun.domain.pet.Pet
import com.dohyun.domain.user.UserRepository
import javax.inject.Inject

class GetDiaryUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository,
    private val getDiaryIndexAndOffsetUseCase: GetDiaryIndexAndOffsetUseCase
) {

    suspend operator fun invoke(
        currentDiaryListSize: Int,
        currentIndex: Int,
        offset: Int,
        isPaging: Boolean = false
    ): DiaryResponse? {
        val totalCount = diaryRepository.getDiaryCount()
        val currentIndexAndOffset = getDiaryIndexAndOffsetUseCase(
            diaryListSize = currentDiaryListSize,
            currentIndex = currentIndex,
            currentOffset = offset,
            isPaging = isPaging
        )

        diaryRepository.getDiary(
            startIndex = currentIndexAndOffset.index,
            offset = currentIndexAndOffset.offset
        )?.let { diaryList ->
            return if (totalCount == currentDiaryListSize) {
                null
            } else {
                val currentDiaryList = diaryList.map {
                    if (it.pet == null) {
                        it.copy(pet = getPetDto())
                    } else {
                        it.copy()
                    }
                }.distinctBy { it.id }.reversed().toMutableList()

                DiaryResponse(diaryList = currentDiaryList, indexAndOffset = currentIndexAndOffset)
            }
        } ?: kotlin.run {
            return null
        }
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