package com.dohyun.petmemory.ui.diary.write.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dohyun.petmemory.ui.diary.write.DiaryWriteScreen

fun NavGraphBuilder.diaryWriteNavGraph(
    onCompleted: () -> Unit
) {
    composable("diaryWrite") {
        DiaryWriteScreen(isEdit = false) {
            onCompleted()
        }
    }
}