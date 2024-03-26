package com.dohyun.petmemory.ui.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dohyun.domain.diary.DiaryData
import com.dohyun.petmemory.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToWrite: () -> Unit,
    albumViewModel: AlbumViewModel2 = hiltViewModel()
) {
    val uiState by albumViewModel.albumUiState.collectAsStateWithLifecycle()
    val diaries = (uiState as? AlbumUiState.Album)?.diaries ?: listOf()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "앨범",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.bmdohyun_ttf)),
                        color = colorResource(id = R.color.white)
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.black)))
        }
    ) {
        AlbumContent(
            modifier = Modifier.padding(top = it.calculateTopPadding()),
            diaries = diaries,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToWrite = onNavigateToWrite
        )
    }
}

@Composable
fun AlbumContent(
    modifier: Modifier = Modifier,
    diaries: List<DiaryData>,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToWrite: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .padding(15.dp)
                    .align(Alignment.CenterEnd)
                    .clickable {
                        onNavigateToWrite()
                    },
                text = "업로드",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.bmdohyun_ttf)),
                color = colorResource(id = R.color.black)
            )
        }
        AlbumGrid(diaries = diaries, onNavigateToDetail)
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumContentPreview() {
    val list = mutableListOf<DiaryData>()

    for (i in 0..100) {
        list.add(
            DiaryData(
                id = "$i",
                date = "hi",
                imageUrl = listOf("")
            )
        )
    }
    AlbumContent(diaries = list, onNavigateToDetail = {}) {

    }
}

@Composable
fun AlbumGrid(diaries: List<DiaryData>, onNavigateToDetail: (String) -> Unit) {
    LazyVerticalStaggeredGrid(
        contentPadding = PaddingValues(horizontal = 4.dp),
        columns = StaggeredGridCells.Fixed(3),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(items = diaries,
                key = {
                    it.id
                }) {
                Diary(it, onNavigateToDetail)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun AlbumGridPreview() {
    val list = mutableListOf<DiaryData>()

    for (i in 0..100) {
        list.add(
            DiaryData(
                id = "$i",
                date = "hi",
                imageUrl = listOf("")
            )
        )
    }

    AlbumGrid(diaries = list) {

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Diary(diary: DiaryData, onNavigateToDetail: (String) -> Unit) {
    GlideImage(
        model = diary.imageUrl[0],
        contentDescription = null,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable {
                onNavigateToDetail(diary.id)
            },
        contentScale = ContentScale.Crop,
    )
}