package com.dohyun.petmemory.ui.album.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dohyun.petmemory.ui.album.AlbumScreen

fun NavGraphBuilder.albumNavigation(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToWrite: () -> Unit
) {
    composable("album") {
        AlbumScreen(
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToWrite = onNavigateToWrite
        )
    }
}