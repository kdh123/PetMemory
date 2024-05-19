package com.dohyun.petmemory.map

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.R
import com.dohyun.petmemory.util.LocationUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.overlay.OverlayImage
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val uiState: MapUiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is MapUiState.Loading -> {

        }

        is MapUiState.Map -> {
            Map(diaries = (uiState as MapUiState.Map).diaries, onNavigateToDetail = onNavigateToDetail)
        }

        is MapUiState.Error -> {

        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalNaverMapApi::class)
@Composable
fun Map(diaries: List<Diary>, onNavigateToDetail: (String) -> Unit) {
    var currentLocation by remember {
        mutableStateOf(LatLng(37.453522, 126.6787955))
    }
    val cameraPositionState = rememberCameraPositionState {
        // 카메라 초기 위치를 설정합니다.
        position = CameraPosition(currentLocation, 11.0)
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
    val pagerState = rememberPagerState(pageCount = {
        diaries.size
    })

    LaunchedEffect(pagerState, diaries) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (diaries.isNotEmpty()) {
                currentLocation = LatLng(diaries[page].lat ?: 37.532600, diaries[page].lng ?: 127.024612)
                cameraPositionState.animate(CameraUpdate.toCameraPosition(CameraPosition(currentLocation, 15.0)))
            }
        }
    }
    /*
    cameraPositionState.move(
        CameraUpdate.toCameraPosition(CameraPosition(currentLocation, 15.0))
    )*/

    Box(Modifier.fillMaxSize()) {
        NaverMap(cameraPositionState = cameraPositionState, properties = mapProperties, uiSettings = mapUiSettings) {
            diaries.forEach { diary ->
                Marker(
                    state = MarkerState(position = LatLng(diary.lat ?: 37.532600, diary.lng ?: 127.024612)),
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

        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 124.dp)
                .height(120.dp)
                .align(Alignment.BottomCenter),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 10.dp
        ) { page ->
            MapCard(diary = diaries[page], onNavigateToDetail = onNavigateToDetail)
        }
    }
}

@Composable
fun MapCard(diary: Diary, onNavigateToDetail: (String) -> Unit) {
    val locationUtil = LocationUtil(LocalContext.current)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = colorResource(id = R.color.white))
            .clickable {
                onNavigateToDetail(diary.id)
            }
    ) {
        GlideImage(
            imageModel = diary.imageUrl[0],
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = diary.title,
                fontFamily = FontFamily(Font(R.font.bmdohyun_ttf)),
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = diary.date,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_lt)),
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = locationUtil.getAddress(diary.lat!!, diary.lng!!),
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_lt)),
                fontSize = 18.sp
            )
        }
    }
}