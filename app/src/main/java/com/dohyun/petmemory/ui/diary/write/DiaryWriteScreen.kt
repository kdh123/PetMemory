package com.dohyun.petmemory.ui.diary.write

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.R
import com.dohyun.petmemory.ui.diary.detail.DiaryDetail
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryWriteScreen(
    viewModel: DiaryWriteViewModel = hiltViewModel(),
    diaryDetail: DiaryDetail? = null,
    isEdit: Boolean,
    onFinish: (Diary?) -> Unit,
) {
    val lazyRowState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val diary = uiState.diary
    val imageUrls = diary.imageUrl.filter { it.isNotEmpty() } + listOf("")
    var selectImageIndex = -1
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION

            context.contentResolver.takePersistableUriPermission(it, flag)
            viewModel.onAction(
                action = DiaryWriteAction.Image(
                    index = selectImageIndex,
                    uri = uri,
                    isEdit = selectImageIndex >= 0
                )
            )

            if (selectImageIndex < 0) {
                scope.launch {
                    delay(100L)
                    lazyRowState.scrollToItem(imageUrls.size - 1)
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onAction(action = DiaryWriteAction.Edit(diary = diaryDetail?.toDiary()))
    }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onFinish(uiState.diary)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        AppBar {
            diary.run {
                viewModel.onAction(action = DiaryWriteAction.Save(title, content, isEdit))
            }
        }
        TextField(
            value = diary.title,
            onValueChange = {
                viewModel.onAction(action = DiaryWriteAction.Edit(diary = diary.copy(title = it)))
            },
            placeholder = {
                Text("제목을 입력해주세요 (선택사항)", fontSize = 18.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = colorResource(id = R.color.white))
        )
        TextField(
            value = diary.content,
            onValueChange = {
                viewModel.onAction(action = DiaryWriteAction.Edit(diary = diary.copy(content = it)))
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
        PetImages(lazyRowState = lazyRowState, images = imageUrls) { index ->
            selectImageIndex = index
            launcher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Profiles(uiState = uiState) { position ->
            viewModel.onAction(
                action = DiaryWriteAction.SelectPet(position = position)
            )
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
            .padding(horizontal = 10.dp, vertical = 18.dp)

        ) {
        Image(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back",
        )

        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = "추억 쌓기",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.bmdohyun_ttf)),
                modifier = Modifier.align(Alignment.Center)
            )
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
                    }
                )
            } else {
                PetImage(
                    path = item,
                    onClick = {
                        onChangeImage(index)
                    }
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun PetImage(path: Any, modifier: Modifier = Modifier, onClick: () -> Unit) {
    GlideImage(
        imageModel = path,
        contentDescription = "pet image",
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .width(140.dp)
            .height(140.dp)
            .clickable {
                onClick()
            }
    )
}

@Composable
fun ProfileImage(path: Any, modifier: Modifier = Modifier, onCLick: () -> Unit) {
    GlideImage(
        imageModel = path,
        contentDescription = "profile image",
        modifier = modifier
            .clip(CircleShape)
            .width(78.dp)
            .height(78.dp)
            .clickable {
                onCLick()
            }
    )
}

@Composable
fun Profiles(uiState: DiaryWriteUiState, onClick: (Int) -> Unit) {
    LazyRow(modifier = Modifier.padding(10.dp)) {
        itemsIndexed(uiState.pets) { index, profile ->
            if (profile.isSelected) {
                Box {
                    ProfileImage(
                        path = profile.pet.imageUrl,
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
                    path = profile.pet.imageUrl,
                    onCLick = {
                        onClick(index)
                    }
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}