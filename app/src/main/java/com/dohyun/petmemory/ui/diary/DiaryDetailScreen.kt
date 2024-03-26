package com.dohyun.petmemory.ui.diary

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dohyun.petmemory.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    diaryId: String,
    onNavigateToWrite: () -> Unit,
    onDelete: () -> Unit,
    diaryDetailViewModel: DiaryDetailViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val detailUiState by diaryDetailViewModel.uiState.collectAsStateWithLifecycle()
    val modalSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val diaryDetail = (detailUiState as? DiaryDetailUiState.Success)?.diary
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(true) {
        diaryDetailViewModel.getDiary(diaryId = diaryId)
    }

    LaunchedEffect(true) {
        diaryDetailViewModel.deleteFlow.collect {
            onDelete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                dispatcher?.onBackPressed()
                            }
                            .padding(end = 10.dp)
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = diaryDetail?.title ?: "(제목 없음)",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.bmdohyun_ttf)),
                        color = colorResource(id = R.color.black),
                        textAlign = TextAlign.Center
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                showBottomSheet = !showBottomSheet
                            }
                            .padding(end = 10.dp)
                    )
                }
            })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (diaryDetail != null) {
                DiaryPager(diary = diaryDetail)
            }

            OptionModalBottomSheet(
                diaryId = diaryId,
                padding = innerPadding.calculateTopPadding(),
                modalSheetState = modalSheetState,
                showBottomSheet = showBottomSheet,
                onEdit = {
                    onNavigateToWrite()
                    scope.launch {
                        modalSheetState.hide()
                    }
                },
                onDelete = diaryDetailViewModel::deleteDiary,
                onBottomSheet = {
                    showBottomSheet = !showBottomSheet
                })
        }

        Spacer(
            Modifier.navigationBarsPadding())

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionModalBottomSheet(
    diaryId: String,
    padding: Dp,
    modalSheetState: SheetState,
    showBottomSheet: Boolean,
    onBottomSheet: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onBottomSheet(false)
            },
            windowInsets = WindowInsets(bottom = padding),
            sheetState = modalSheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_revise),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(28.dp)
                            .height(28.dp)
                    )
                    Text(
                        text = "수정",
                        fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md)),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                            .clickable {
                                onEdit()
                            }
                            .padding(end = 10.dp)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(28.dp)
                            .height(28.dp)
                    )
                    Text(
                        text = "삭제",
                        fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md)),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                            .clickable {
                                onDelete(diaryId)
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun DiaryPager(diary: DiaryDetail) {
    val images: List<String> = diary.imageUrl.filter { it.isNotEmpty() }
    val pagerState = rememberPagerState(pageCount = {
        images.size
    })

    var currentPage by remember {
        mutableIntStateOf(1)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPage = page + 1
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                GlideImage(
                    model = images[page],
                    contentDescription = "pet image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                ) { builder ->
                    builder.centerCrop()
                }
            }
            Text(
                text = "$currentPage / ${images.size}",
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = colorResource(id = R.color.transparent_gray))
                    .padding(10.dp),
                color = colorResource(id = R.color.black)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_location), contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .width(24.dp)
                    .height(24.dp)
            )
            Text(
                text = diary.address,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md))
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = diary.content ?: "(내용 없음)",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_lt))
            )
        }
    }
}