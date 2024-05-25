package com.dohyun.petmemory.map.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dohyun.petmemory.map.MapScreen

fun NavGraphBuilder.mapNavGraph(onNavigateToDetail: (String) -> Unit) {
    composable("map") {
        MapScreen(
            onNavigateToDetail = onNavigateToDetail
        )
    }
}