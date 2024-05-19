package com.dohyun.petmemory.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.R
import com.dohyun.petmemory.ui.diary.SelectedPet
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAlbum: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val diaries = homeUiState.diaries
    val pets = homeUiState.selectedPets

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
                            .padding(end = 15.dp)
                            .clickable {
                                onNavigateToAlbum()
                            },
                        painter = painterResource(id = R.drawable.ic_album_white),
                        contentDescription = null,
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Profiles(selectedPets = pets, onSelected = viewModel::onAction)
            TimeLine(diaries = diaries, onNavigateToDetail = onNavigateToDetail)
        }
    }
}

@Composable
fun Profiles(selectedPets: List<SelectedPet>, onSelected: (HomeAction) -> Unit) {
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
            items(selectedPets, key = { selectedPet -> selectedPet.pet.id }) {
                Profile(selectedPet = it, onSelected = onSelected)
            }
        }
    }
}

@Composable
fun Profile(selectedPet: SelectedPet, onSelected: (HomeAction) -> Unit) {
    Box {
        GlideImage(
            imageModel = selectedPet.pet.imageUrl,
            contentDescription = "diary image",
            modifier = Modifier
                .clip(CircleShape)
                .width(78.dp)
                .height(78.dp)
                .clickable {
                    onSelected(HomeAction.SelectPet(selectedPet = selectedPet))
                },
            contentScale = ContentScale.Crop
        )

        if (selectedPet.isSelected) {
            Image(
                painter = painterResource(id = R.drawable.ic_check_white), contentDescription = null,
                modifier = Modifier
                    .background(colorResource(id = R.color.dim), shape = CircleShape)
            )
        }
    }
}

@Composable
fun TimeLine(
    diaries: List<Diary>,
    onNavigateToDetail: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        items(items = diaries, key = {
            it.id
        }) {
            Diary(diary = it, onClick = onNavigateToDetail)
        }
    }
}

@Composable
fun Diary(diary: Diary, onClick: (String) -> Unit) {
    val localDensity = LocalDensity.current
    var lineHeight by remember {
        mutableStateOf(0.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick(diary.id)
            }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.width(5.dp))
            GlideImage(
                imageModel = diary.pet?.imageUrl ?: R.drawable.ic_add,
                contentDescription = "profile image",
                modifier = Modifier
                    .clip(CircleShape)
                    .width(48.dp)
                    .height(48.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = diary.date, modifier = Modifier.align(Alignment.CenterVertically))
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
                    AsyncImage(
                        model = diary.imageUrl[0],
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .weight(1f)
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop,
                        onError = {
                        }
                    )

                    Spacer(modifier = Modifier.width(20.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}