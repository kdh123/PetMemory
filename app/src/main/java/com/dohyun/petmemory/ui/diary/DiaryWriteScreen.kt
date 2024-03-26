package com.dohyun.petmemory.ui.diary

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryWriteScreen(
    onFinish: (DiaryData?) -> Unit,
    diaryWriteViewModel: DiaryWriteViewModel = hiltViewModel(),
    diary: DiaryDetail? = null,
    isEdit: Boolean
) {
    val lazyRowState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val diaryWriteUiState by diaryWriteViewModel.diaryWriteUiState.collectAsStateWithLifecycle()
    var title by remember {
        mutableStateOf(diary?.title ?: "")
    }
    var content by remember {
        mutableStateOf(diary?.content ?: "")
    }
    val imageUrls = if (diaryWriteUiState is DiaryWriteUiState.Writing) {
        (diaryWriteUiState as DiaryWriteUiState.Writing).diaryData.imageUrl.filter { it.isNotEmpty() } + listOf("")
    } else {
        listOf("")
    }
    val context = LocalContext.current
    var selectImageIndex = -1
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(it, flag)

            if (selectImageIndex >= 0) {
                diaryWriteViewModel.editImage(index = selectImageIndex, uri = uri)
            } else {
                diaryWriteViewModel.addImage(uri = uri)
            }

            if (selectImageIndex < 0) {
                scope.launch {
                    delay(100L)
                    lazyRowState.scrollToItem(imageUrls.size - 1)
                }
            }
        }
    }

    LaunchedEffect(true) {
        diaryWriteViewModel.setDiary(diary = diary?.toDiary())
    }

    val state = diaryWriteUiState

    if (state is DiaryWriteUiState.Save) {
        onFinish(state.diaryData)

        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        AppBar {
            diaryWriteViewModel.saveDiary(title = title, content = content, isEdit = isEdit)
        }

        TextField(
            value = title,
            onValueChange = {
                title = it
            },
            placeholder = {
                Text("제목을 입력해주세요 (선택사항)", fontSize = 18.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = colorResource(id = R.color.white))
        )

        TextField(
            value = content,
            onValueChange = {
                content = it
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
            text = "사진 등록 ${imageUrls.filter { it.isNotEmpty() }.size} / 10",
            fontSize = 14.sp,
            modifier = Modifier.padding(10.dp)
        )
        PetImages(lazyRowState = lazyRowState, launcher = launcher, images = imageUrls) { index ->
            selectImageIndex = index
        }
        Spacer(modifier = Modifier.height(10.dp))

        Profiles(uiState = diaryWriteUiState) { position ->
            diaryWriteViewModel.selectProfile(position)
        }
    }
}

@Composable
fun AppBar(onSaveClick: () -> Unit) {
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
fun PetImages(
    lazyRowState: LazyListState,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    images: List<String>,
    onChangeImage: (Int) -> Unit
) {
    LazyRow(modifier = Modifier.padding(start = 10.dp, bottom = 10.dp), state = lazyRowState) {
        itemsIndexed(images, key = { index, _ ->
            index
        }) { index, item ->
            if (item.isEmpty()) {
                PetImage(
                    path = R.drawable.ic_add_gray,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = colorResource(id = R.color.light_gray),
                        shape = RoundedCornerShape(10.dp)
                    ),
                    onClick = {
                        onChangeImage(-1)
                        launcher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
            } else {
                PetImage(
                    path = item,
                    onClick = {
                        onChangeImage(index)
                        launcher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PetImage(path: Any, modifier: Modifier = Modifier, onClick: () -> Unit) {
    GlideImage(
        model = path,
        contentDescription = "pet image",
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .width(140.dp)
            .height(140.dp)
            .clickable {
                onClick()
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
fun Profiles(uiState: DiaryWriteUiState, onClick: (Int) -> Unit) {
    when (uiState) {
        is DiaryWriteUiState.Writing -> {
            if (uiState.profiles == null) {
                return
            }
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