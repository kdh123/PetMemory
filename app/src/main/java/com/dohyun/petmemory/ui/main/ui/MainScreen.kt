package com.dohyun.petmemory.ui.main.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dohyun.petmemory.R
import com.dohyun.petmemory.map.navigation.mapNavGraph
import com.dohyun.petmemory.ui.album.navigation.albumNavigation
import com.dohyun.petmemory.ui.diary.detail.navigation.diaryDetailNavGraph
import com.dohyun.petmemory.ui.diary.write.CameraActivity
import com.dohyun.petmemory.ui.diary.write.navigation.diaryWriteNavGraph
import com.dohyun.petmemory.ui.home.navigation.homeNavGraph
import com.dohyun.petmemory.ui.profile.edit.profileEditNavGraph
import com.dohyun.petmemory.ui.profile.profileNavGraph

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Map, Screen.Camera, Screen.Profile)
    val isBottomNavShow = navController.currentBackStackEntryAsState().value?.destination?.route in listOf("home", "map", "profile")
    val context = LocalContext.current
    val onCameraClick = {
        context.startActivity(Intent(context, CameraActivity::class.java))
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        NavHost(modifier = Modifier.fillMaxSize(), navController = navController, startDestination = "home") {
            homeNavGraph(
                onNavigateToDetail = { diaryId ->
                    navController.navigate("diaryDetail/$diaryId")
                },
                onNavigateToAlbum = {
                    navController.navigate("album")
                }
            )

            mapNavGraph { diaryId ->
                navController.navigate("diaryDetail/$diaryId")
            }

            diaryWriteNavGraph {
                navController.popBackStack()
            }

            diaryDetailNavGraph(
                navController = navController,
                onNavigateToEdit = {
                    navController.navigate("diaryEdit")
                },
                onCompleted = {
                    navController.popBackStack()
                }
            )

            albumNavigation(
                onNavigateToDetail = { diaryId ->
                    navController.navigate("diaryDetail/$diaryId")
                },
                onNavigateToWrite = {
                    navController.navigate("diaryWrite")
                }
            )

            profileNavGraph { petId ->
                navController.navigate("profileEdit/$petId")
            }

            profileEditNavGraph {
                navController.popBackStack()
            }
        }

        AnimatedVisibility(
            visible = isBottomNavShow,
            enter = fadeIn() + slideIn { IntOffset(0, it.height) },
            exit = fadeOut() + slideOut { IntOffset(0, it.height) }
        ) {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Bottom),
                backgroundColor = colorResource(id = R.color.brown),
                cutoutShape = RoundedCornerShape(50),
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.screenRoute } == true

                    BottomNavigationItem(
                        icon = {
                            if (isSelected) {
                                Icon(painterResource(id = screen.selected), contentDescription = null, tint = Color.Unspecified)
                            } else {
                                Icon(painterResource(id = screen.unSelected), contentDescription = null, tint = Color.Unspecified)
                            }
                        },
                        label = {
                            if (isSelected) {
                                Text(screen.screenRoute, color = colorResource(id = R.color.primary))
                            } else {
                                Text(screen.screenRoute)
                            }
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.screenRoute } == true,
                        onClick = {
                            if (screen.screenRoute == "camera") {
                                onCameraClick()
                            } else {
                                navController.navigate(screen.screenRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

sealed class Screen(
    val title: String, val selected: Int, val unSelected: Int, val screenRoute: String
) {
    object Home : Screen("홈", R.drawable.ic_home, R.drawable.ic_home_black, "home")
    object Map : Screen("지도", R.drawable.ic_map_primary, R.drawable.ic_map_black, "map")
    object Camera : Screen("카메라", R.drawable.ic_add, R.drawable.ic_add_black_15, "camera")
    object Profile : Screen("프로필", R.drawable.ic_profile_primary, R.drawable.ic_profile_black, "profile")
}