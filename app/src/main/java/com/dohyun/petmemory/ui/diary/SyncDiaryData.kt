package com.dohyun.petmemory.ui.diary

import com.dohyun.domain.diary.DiaryData
import java.io.Serializable

enum class DiaryEvent{
    None,
    Edit,
    Delete,
    Save
}

data class SyncDiaryData(
    val diaryData: DiaryData,
    val event: DiaryEvent
): Serializable {
    companion object {
        const val KEY_SYNC_EVENT = "key_sync_event"
    }
}
