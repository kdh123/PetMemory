package com.dohyun.petmemory.ui.diary.write

import android.net.Uri
import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.ui.diary.SelectedPet

sealed interface DiaryWriteAction {
    data class Edit(val diary: Diary? = null, val pets: List<SelectedPet>? = null): DiaryWriteAction
    data class Save(val title: String, val content: String, val isEdit: Boolean) : DiaryWriteAction
    data class Image(val index: Int, val uri: Uri?, val isEdit: Boolean): DiaryWriteAction
    data class SelectPet(val position: Int): DiaryWriteAction
}

