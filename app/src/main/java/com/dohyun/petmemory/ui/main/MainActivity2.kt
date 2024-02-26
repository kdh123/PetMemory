package com.dohyun.petmemory.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dohyun.petmemory.R
import com.dohyun.petmemory.ui.diary.CameraActivity
import com.dohyun.petmemory.ui.diary.DiaryDetailScreen
import com.dohyun.petmemory.ui.home.HomeScreen
import com.dohyun.petmemory.ui.home.HomeUiState
import com.dohyun.petmemory.ui.home.HomeViewModel
import com.dohyun.petmemory.ui.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val items = listOf(Screen.Home, Screen.Camera, Screen.Profile)
    val alpha by homeViewModel.sheetAlpha.collectAsStateWithLifecycle()
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    val isShow = navController.currentBackStackEntryAsState().value?.destination?.route in listOf("home", "profile")
    val isBottomSheetShow = if (homeUiState is HomeUiState.Success) {
        (homeUiState as HomeUiState.Success).isBottomSheetShow
    } else {
        true
    }
    val context = LocalContext.current
    val onCameraClick = {
        context.startActivity(Intent(context, CameraActivity::class.java))
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        NavHost(modifier = Modifier.fillMaxSize(), navController = navController, startDestination = "home") {
            composable("home") {
                val onClick: (String) -> Unit = remember {
                    { diaryId ->
                        val des = navController.currentDestination?.id
                        navController.navigate("diaryDetail/$diaryId")
                    }
                }

                HomeScreen(
                    onNavigateToDetail = onClick,
                    viewModel = homeViewModel
                )
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("diaryDetail/{diaryId}") { backStackEntry ->
                DiaryDetailScreen(diaryId = backStackEntry.arguments?.getString("diaryId")!!)
            }
        }

        if (isBottomSheetShow) {
            AnimatedVisibility(
                visible = isShow,
                enter = fadeIn() + slideIn { IntOffset(0, it.height) },
                exit = fadeOut() + slideOut { IntOffset(0, it.height) }
            ) {
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Bottom)
                        .alpha(alpha),
                    backgroundColor = colorResource(id = R.color.brown),
                    cutoutShape = RoundedCornerShape(50),
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(painterResource(id = screen.icon), contentDescription = null) },
                            label = { Text(screen.screenRoute) },
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
}

sealed class Screen(
    val title: String, val icon: Int, val screenRoute: String
) {
    object Home : Screen("홈", R.drawable.ic_home, "home")
    object Camera : Screen("카메라", R.drawable.ic_add, "camera")
    object Profile : Screen("프로필", R.drawable.ic_profile, "profile")
}