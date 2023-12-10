package com.dohyun.domain.diary

import androidx.core.net.toUri
import com.dohyun.domain.MediaUtil
import com.dohyun.domain.pet.PetDto
import com.dohyun.domain.user.UserRepository
import javax.inject.Inject

class SaveDiaryUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository,
    private val mediaUtil: MediaUtil
) {
    suspend operator fun invoke(diaryData: DiaryData, imageNeedSaveToGalleryList: Set<Int>): DiaryData {
        var data = diaryData

        if (data.pet == null) {
           data = data.copy(pet = getPetDto())
        }

        data.imageUrl.filter { it.isNotEmpty() }.mapIndexed { index, url ->
            if (imageNeedSaveToGalleryList.contains(index)) {
                with(mediaUtil) {
                    val bitmap = getBitmapFromUri(uri = url.toUri())
                    saveImageToGallery(bitmap = bitmap)
                }
            }
        }
        diaryRepository.saveDiary(diaryData = data.copy(imageUrl = data.imageUrl.filter { it.isNotEmpty() }))

        return data
    }

    private suspend fun getPetDto(): PetDto {
        return userRepository.run {
            PetDto(
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