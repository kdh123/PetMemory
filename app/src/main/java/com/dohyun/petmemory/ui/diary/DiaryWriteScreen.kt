package com.dohyun.petmemory.ui.diary

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
fun DiaryWriteScreen(
    viewModel: DiaryWriteViewModel = hiltViewModel(),
    isEdit: Boolean,
    onBackClick: () -> Unit,
    onComplete: (DiaryData) -> Unit
) {
    val diaryWriteUiState = viewModel.diaryWriteUiState.collectAsStateWithLifecycle()
    val profileUiState = viewModel.profileUiState.collectAsStateWithLifecycle()
    val title = remember {
        val state = diaryWriteUiState.value
        val str = if (state is DiaryWriteUiState.Writing) {
            state.diaryData.title
        } else {
            ""
        }

        mutableStateOf(str)
    }
    val content = remember {
        val state = diaryWriteUiState.value
        val str = if (state is DiaryWriteUiState.Writing) {
            state.diaryData.content ?: ""
        } else {
            ""
        }

        mutableStateOf(str)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.addImageList(imageUrl = uri.toString())
    }

    val state = diaryWriteUiState.value

    if (state is DiaryWriteUiState.Save) {
        onComplete(state.diaryData)
    }

    if (state !is DiaryWriteUiState.Writing) {
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        AppBar(onBackClick = onBackClick) {
            viewModel.saveDiary(title = title.value, content = content.value, isEdit = isEdit)
        }

        TextField(
            value = title.value,
            onValueChange = {
                title.value = it
            },
            placeholder = {
                Text("제목을 입력해주세요 (선택사항)", fontSize = 18.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = colorResource(id = R.color.white))
        )

        TextField(
            value = content.value,
            onValueChange = {
                content.value = it
            },
            placeholder = {
                Text("내용 입력해주세요 (선택사항)", fontSize = 18.sp)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = TextFieldDefaults.textFieldColors(containerColor = colorResource(id = R.color.white))
        )
        Text(
            text = "사진 등록 ${state.diaryData.imageUrl.filter { it.isNotEmpty() }.size} / 10",
            fontSize = 14.sp,
            modifier = Modifier.padding(10.dp)
        )
        PetImageList(launcher = launcher, uiState = diaryWriteUiState.value)
        Spacer(modifier = Modifier.height(10.dp))

        ProfileList(uiState = profileUiState.value) { position ->
            viewModel.selectProfile(position)
        }
    }
}

@Composable
fun AppBar(onBackClick: () -> Unit, onSaveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 5f
                )
            }
            .padding(10.dp),

        ) {
        Image(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back",
            modifier = Modifier
                .clickable {
                    onBackClick()
                }
        )

        Box(modifier = Modifier.weight(1f)) {
            Text(text = "추억 쌓기", fontSize = 18.sp, modifier = Modifier.align(Alignment.Center))
        }

        Image(
            painter = painterResource(id = R.drawable.ic_check_black),
            contentDescription = "save",
            modifier = Modifier
                .clickable {
                    onSaveClick()
                }
        )
    }
}


@Composable
fun PetImageList(launcher: ManagedActivityResultLauncher<String, Uri?>, uiState: DiaryWriteUiState) {
    when (uiState) {
        is DiaryWriteUiState.Writing -> {
            LazyRow(modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)) {
                items(uiState.diaryData.imageUrl) {
                    if (it.isEmpty()) {
                        PetImage(
                            path = R.drawable.ic_add_gray,
                            modifier = Modifier.border(
                                width = 1.dp,
                                color = colorResource(id = R.color.light_gray),
                                shape = RoundedCornerShape(10.dp)
                            ),
                            onCLick = {
                                launcher.launch("image/*")
                            }
                        )
                    } else {
                        PetImage(
                            path = it,
                            onCLick = {
                                launcher.launch("image/*")
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }

        else -> {

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PetImage(path: Any, modifier: Modifier = Modifier, onCLick: () -> Unit) {
    GlideImage(
        model = path,
        contentDescription = "pet image",
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .width(140.dp)
            .height(140.dp)
            .clickable {
                onCLick()
            }
    ) { builder ->
        builder.centerCrop()
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileImage(path: Any, modifier: Modifier = Modifier, onCLick: () -> Unit) {
    GlideImage(
        model = path,
        contentDescription = "profile image",
        modifier = modifier
            .clip(CircleShape)
            .width(78.dp)
            .height(78.dp)
            .clickable {
                onCLick()
            }
    ) { builder ->
        builder.centerCrop()
    }
}

@Composable
fun ProfileList(uiState: ProfileUiState, onClick: (Int) -> Unit) {
    when (uiState) {
        is ProfileUiState.Selected -> {
            LazyRow(modifier = Modifier.padding(10.dp)) {
                itemsIndexed(uiState.profiles) { index, profile ->
                    if (profile.isSelected) {
                        Box {
                            ProfileImage(
                                path = profile.petDto.petImageUrl,
                                onCLick = {
                                }
                            )
                            ProfileImage(
                                path = R.drawable.ic_check_white,
                                modifier = Modifier
                                    .background(colorResource(id = R.color.dim), shape = CircleShape),
                                onCLick = {
                                }
                            )
                        }
                    } else {
                        ProfileImage(
                            path = profile.petDto.petImageUrl,
                            onCLick = {
                                onClick(index)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }

        else -> {
        }
    }
}