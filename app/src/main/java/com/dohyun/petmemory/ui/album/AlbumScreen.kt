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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.R
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToWrite: () -> Unit,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val uiState by viewModel.albumUiState2.collectAsStateWithLifecycle()
    val diaries = uiState.diaries

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
    diaries: List<Diary>,
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
    val list = mutableListOf<Diary>()

    for (i in 0..7) {
        list.add(
            Diary(
                id = "$i",
                date = "hi",
                imageUrl = listOf(""),
                lat = null,
                lng = null
            )
        )
    }
    AlbumContent(diaries = list, onNavigateToDetail = {}) {

    }
}

@Composable
fun AlbumGrid(diaries: List<Diary>, onNavigateToDetail: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = diaries, key = { it.id }) {
            Diary(it, onNavigateToDetail)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumGridPreview() {
    val list = mutableListOf<Diary>()

    for (i in 1..8) {
        list.add(
            Diary(
                id = "dummy $i",
                date = "hi",
                imageUrl = listOf(""),
                lat = null,
                lng = null
            )
        )
    }

    AlbumGrid(diaries = list) {

    }
}

@Composable
fun Diary(diary: Diary, onNavigateToDetail: (String) -> Unit) {
    GlideImage(
        imageModel = diary.imageUrl[0],
        contentDescription = null,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable {
                if (diary.imageUrl[0].isNotEmpty()) {
                    onNavigateToDetail(diary.id)
                }
            },
        contentScale = ContentScale.Crop
    )
}