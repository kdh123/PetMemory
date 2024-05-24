package com.dohyun.petmemory.ui.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dohyun.petmemory.ui.home.HomeScreen

fun NavGraphBuilder.homeNavGraph(onNavigateToDetail: (String) -> Unit, onNavigateToAlbum: () -> Unit) {
    composable("home") {
        HomeScreen(
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToAlbum = onNavigateToAlbum,
        )
    }
}