package com.dohyun.petmemory.ui.diary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage


@Composable
fun DiaryDetailScreen(diaryId: String, viewModel: DiaryDetailViewModel = hiltViewModel()) {
    val detailUiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.getDiary(diaryId = diaryId)
    DiaryPager(uiState = detailUiState)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun DiaryPager(uiState: DiaryDetailUiState) {
    val images: List<String> = if (uiState is DiaryDetailUiState.Success) {
        uiState.diary?.imageUrl?.filter { it.isNotEmpty() } ?: listOf()
    } else {
        listOf()
    }

    val pagerState = rememberPagerState(pageCount = {
        images.size
    })

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            // page content
            GlideImage(
                model = images[page],
                contentDescription = "pet image",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .weight(1f)
                    .aspectRatio(0.8f)
            ) { builder ->
                builder.centerCrop()
            }
        }
    }
}