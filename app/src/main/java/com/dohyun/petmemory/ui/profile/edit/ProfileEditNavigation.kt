package com.dohyun.petmemory.ui.profile.edit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileEditNavGraph(
    onCompleted: () -> Unit
) {
    composable("profileEdit/{petId}") { backStackEntry ->
        val petId = backStackEntry.arguments?.getString("petId") ?: "-1"
        ProfileEditScreen(petId = petId.toInt()) {
            onCompleted()
        }
    }
}