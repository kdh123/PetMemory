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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.dohyun.petmemory.R
import com.dohyun.petmemory.map.MapScreen
import com.dohyun.petmemory.ui.album.AlbumScreen
import com.dohyun.petmemory.ui.diary.CameraActivity
import com.dohyun.petmemory.ui.diary.DiaryDetailScreen
import com.dohyun.petmemory.ui.diary.DiaryDetailUiState
import com.dohyun.petmemory.ui.diary.DiaryDetailViewModel
import com.dohyun.petmemory.ui.diary.DiaryWriteScreen
import com.dohyun.petmemory.ui.home.HomeScreen
import com.dohyun.petmemory.ui.home.HomeViewModel2
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
    val homeViewModel: HomeViewModel2 = hiltViewModel()
    val items = listOf(Screen.Home, Screen.Map, Screen.Camera, Screen.Profile)
    val isShow = navController.currentBackStackEntryAsState().value?.destination?.route in listOf("home", "map", "profile")
    var showBottomSheet by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val onCameraClick = {
        context.startActivity(Intent(context, CameraActivity::class.java))
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        NavHost(modifier = Modifier.fillMaxSize(), navController = navController, startDestination = "home") {
            composable("home") {
                val onNavigateToDetail: (String) -> Unit = remember {
                    { diaryId ->
                        navController.navigate("diaryDetail/$diaryId")
                    }
                }
                val onNavigateToAlbum: () -> Unit = remember {
                    {
                        navController.navigate("album")
                    }
                }

                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToAlbum = onNavigateToAlbum,
                )
            }
            composable("map") {
                val onNavigateToDetail: (String) -> Unit = remember {
                    { diaryId ->
                        navController.navigate("diaryDetail/$diaryId")
                    }
                }

                MapScreen(
                    onNavigateToDetail = onNavigateToDetail
                )
            }
            composable("album") {
                val onNavigateToDetail: (String) -> Unit = remember {
                    { diaryId ->
                        navController.navigate("diaryDetail/$diaryId")
                    }
                }
                val onNavigateToWrite = remember {
                    {
                        navController.navigate("diaryWrite") {
                            restoreState = true
                        }
                    }
                }
                AlbumScreen(
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToWrite = onNavigateToWrite
                )
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("diaryWrite") {
                val onFinish = remember {
                    {
                        navController.popBackStack()
                    }
                }

                DiaryWriteScreen(isEdit = false) {
                    onFinish()
                }
            }
            navigation(
                startDestination = "diaryDetail/{diaryId}",
                route = "diaryDetail"
            ) {
                composable("diaryDetail/{diaryId}") { backStackEntry ->
                    val viewModel = backStackEntry.sharedViewModel<DiaryDetailViewModel>(navController = navController)
                    val onDelete: () -> Unit = remember {
                        {
                            navController.popBackStack()
                        }
                    }

                    DiaryDetailScreen(
                        diaryId = backStackEntry.arguments?.getString("diaryId") ?: "",
                        diaryDetailViewModel = viewModel,
                        onNavigateToWrite = {
                            navController.navigate("diaryEdit")
                        },
                        onDelete = onDelete
                    )
                }
                composable("diaryEdit") { backStackEntry ->
                    val viewModel = backStackEntry.sharedViewModel<DiaryDetailViewModel>(navController = navController)
                    val diaryDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val diary = (diaryDetailUiState as? DiaryDetailUiState.Success)?.diary

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

        AnimatedVisibility(
            visible = isShow,
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

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val entry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(entry)
}

sealed class Screen(
    val title: String, val icon: Int, val screenRoute: String
) {
    object Home : Screen("홈", R.drawable.ic_home, "home")
    object Map : Screen("지도", R.drawable.ic_map, "map")
    object Camera : Screen("카메라", R.drawable.ic_add, "camera")
    object Profile : Screen("프로필", R.drawable.ic_profile, "profile")
}