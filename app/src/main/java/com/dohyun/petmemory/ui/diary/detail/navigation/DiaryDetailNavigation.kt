package com.dohyun.petmemory.ui.diary.detail.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dohyun.petmemory.ui.diary.detail.DiaryDetailScreen
import com.dohyun.petmemory.ui.diary.detail.DiaryDetailViewModel
import com.dohyun.petmemory.ui.diary.write.DiaryWriteScreen
import com.dohyun.petmemory.ui.extension.sharedViewModel

fun NavGraphBuilder.diaryDetailNavGraph(
    navController: NavHostController,
    onNavigateToEdit: () -> Unit,
    onCompleted: () -> Unit
) {
    navigation(
        startDestination = "diaryDetail/{diaryId}",
        route = "diaryDetail"
    ) {
        composable("diaryDetail/{diaryId}") { backStackEntry ->
            val viewModel = backStackEntry.sharedViewModel<DiaryDetailViewModel>(navController = navController)

            DiaryDetailScreen(
                diaryId = backStackEntry.arguments?.getString("diaryId") ?: "",
                viewModel = viewModel,
                onNavigateToWrite = onNavigateToEdit,
                onDelete = onCompleted
            )
        }
        composable("diaryEdit") { backStackEntry ->
            val viewModel = backStackEntry.sharedViewModel<DiaryDetailViewModel>(navController = navController)
            val diaryDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
            val diary = diaryDetailUiState.diary

            DiaryWriteScreen(
                onFinish = { data ->
                    navController.navigate("diaryDetail/${data!!.id}") {
                        popUpTo("diaryEdit") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                isEdit = true,
                diaryDetail = diary
            )
        }
    }
}