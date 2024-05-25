package com.dohyun.petmemory.ui.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileNavGraph(
    onNavigateToEdit: (Int) -> Unit
) {
    composable("profile") {
        ProfileScreen(onNavigateToEditScreen = onNavigateToEdit)
    }
}