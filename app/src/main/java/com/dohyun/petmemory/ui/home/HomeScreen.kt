@file:OptIn(ExperimentalNaverMapApi::class)

package com.dohyun.petmemory.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.pet.PetDto
import com.dohyun.petmemory.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val defaultLocation = LatLng(37.532600, 127.024612)
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        // 카메라 초기 위치를 설정합니다.
        position = CameraPosition(defaultLocation, 11.0)
    }

    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(backgroundColor = colorResource(id = R.color.black)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "petmemory",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.bmdohyun_ttf)),
                        color = colorResource(id = R.color.white)
                    )

                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 15.dp),
                        painter = painterResource(id = R.drawable.ic_album_white),
                        contentDescription = null,
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
            TimeLine(state = homeUiState, cameraPositionState = cameraPositionState, onChangeSheetAlpha = viewModel::setSheetOffset, onNavigateToDetail = onNavigateToDetail)
            PetProfile(state = homeUiState)
        }
    }
}

@Composable
fun PetProfile(state: HomeUiState) {
    val pets = if (state is HomeUiState.Success) {
        state.pets
    } else {
        listOf()
    }

    var xOffset by remember {
        mutableIntStateOf(0)
    }

    var yOffset by remember {
        mutableIntStateOf(0)
    }

    val color = colorResource(id = R.color.gray)

    Box(modifier = Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, yOffset.toFloat()),
            end = Offset(xOffset.toFloat(), yOffset.toFloat()),
            strokeWidth = 3f
        )
    }) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    xOffset = it.size.width
                    yOffset = it.size.height
                }
                .wrapContentHeight(align = Alignment.Top)
                .background(colorResource(id = R.color.white)),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
        ) {
            items(pets, key = { pet -> pet.petId }) {
                Pet(pet = it)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Pet(pet: PetDto) {
    GlideImage(
        model = pet.petImageUrl,
        contentDescription = "diary image",
        modifier = Modifier
            .clip(CircleShape)
            .width(78.dp)
            .height(78.dp)
            .aspectRatio(1f)
    ) { builder ->
        builder.centerCrop()
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeLine(
    state: HomeUiState,
    cameraPositionState: CameraPositionState,
    onChangeSheetAlpha: (Float) -> Unit,
    onNavigateToDetail: (String) -> Unit) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val diaries = if (state is HomeUiState.Success) {
        scope.launch {
            scaffoldState.bottomSheetState.apply {
                expand()
            }
        }
        state.diaries
    } else {
        listOf()
    }
    var sheetHeight by remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current

    Log.e("dhkim", "sss : $sheetHeight")

    BottomSheetScaffold(
        modifier = Modifier.onGloballyPositioned {
            sheetHeight = with(localDensity) { it.size.height.toDp() - 56.dp }

            //onChangeSheetAlpha(it.size.height - scaffoldState.bottomSheetState.requireOffset())

            //Log.d("dhkim", "hihi : ${(it.size.height - scaffoldState.bottomSheetState.requireOffset()) / it.size.height}")
        },
        scaffoldState = scaffoldState,
        sheetContent = {
            Column(modifier = Modifier.height(sheetHeight)) {
                Text(
                    text = "목록", modifier = Modifier
                        .padding(vertical = 10.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .wrapContentHeight(),
                    fontSize = 24.sp
                )

                LazyColumn {
                    items(items = diaries, key = {
                        it.id
                    }) {
                        Diary(data = it)
                    }
                }
            }
        },
        sheetPeekHeight = 128.dp,
        sheetContainerColor = colorResource(id = R.color.white),
        sheetDragHandle = {
            Log.e("dhkim", "height : $sheetHeight")
        }
    ) {
        Map(state = state, cameraPositionState = cameraPositionState, onNavigateToDetail = onNavigateToDetail)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Diary(data: DiaryData) {
    val localDensity = LocalDensity.current

    var lineHeight by remember {
        mutableStateOf(0.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        Row(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.width(5.dp))

            GlideImage(
                model = data.pet?.petImageUrl ?: R.drawable.ic_check_white,
                contentDescription = "profile image",
                modifier = Modifier
                    .clip(CircleShape)
                    .width(48.dp)
                    .height(48.dp)
            ) { builder ->
                builder.centerCrop()
            }

            Spacer(modifier = Modifier.width(5.dp))

            Text(text = data.date, modifier = Modifier.align(Alignment.CenterVertically))
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(25.dp))

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(lineHeight)
                    .fillMaxHeight()
                    .background(color = colorResource(id = R.color.gray))
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    lineHeight = with(localDensity) { it.size.height.toDp() }
                }) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GlideImage(
                        model = data.imageUrl[0],
                        contentDescription = "diary image",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .weight(1f)
                            .aspectRatio(1f)
                    ) { builder ->
                        builder.centerCrop()
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun Map(state: HomeUiState, cameraPositionState: CameraPositionState, onNavigateToDetail: (String) -> Unit) {
    val diaries = if (state is HomeUiState.Success) {
        state.diaries.filter { it.lat != 0.0 && it.lng != 0.0 }.ifEmpty { listOf() }
    } else {
        listOf()
    }

    val currentLocation = if (diaries.isEmpty()) {
        LatLng(37.532600, 127.024612)
    } else {
        LatLng(diaries[0].lat ?: 37.532600, diaries[0].lng ?: 127.024612)
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(maxZoom = 20.0, minZoom = 5.0)
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(isLocationButtonEnabled = false)
        )
    }

    cameraPositionState.move(
        CameraUpdate.toCameraPosition(CameraPosition(currentLocation , 15.0))
    )

    Box(Modifier.fillMaxSize()) {
        NaverMap(cameraPositionState = cameraPositionState, properties = mapProperties, uiSettings = mapUiSettings) {
            diaries.forEach { diary ->
                Marker(
                    state = MarkerState(position = LatLng(diary.lat ?: 37.532600, diary.lng ?:127.024612)),
                    icon = OverlayImage.fromResource(R.drawable.img_map_pin),
                    width = 42.dp,
                    height = 42.dp,
                    onClick = {
                        onNavigateToDetail(diary.id)
                        true
                    }
                )
            }
        }
    }
}