package com.dohyun.petmemory.ui.profile.edit

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dohyun.domain.pet.Pet
import com.dohyun.petmemory.R
import com.dohyun.petmemory.util.DateUtil
import com.skydoves.landscapist.glide.GlideImage

enum class DateType {
    BirthDay, SinceDay
}

@Composable
fun ProfileEditScreen(
    petId: Int,
    viewModel: ProfileEditViewModel = hiltViewModel(),
    onCompleted: () -> Unit
) {
    val context = LocalContext.current
    var showBirthDayCalender by remember {
        mutableStateOf(false)
    }
    var showSinceDayCalender by remember {
        mutableStateOf(false)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pet = uiState.pet
    val isEdit = petId != 0
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION

            context.contentResolver.takePersistableUriPermission(it, flag)
            viewModel.onAction(
                action = ProfileEditAction.Edit(pet = pet.copy(imageUrl = uri.toString()))
            )

        }
    }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onCompleted()
        }
    }

    LaunchedEffect(true) {
        if (isEdit) {
            viewModel.onAction(action = ProfileEditAction.Load(petId = petId))
        }
    }

    if (showBirthDayCalender) {
        Calender(
            pet = uiState.pet,
            onSave = viewModel::onAction,
            dateType = DateType.BirthDay,
            onDismiss = {
                showBirthDayCalender = false
            }
        )
    }

    if (showSinceDayCalender) {
        Calender(
            pet = uiState.pet,
            onSave = viewModel::onAction,
            dateType = DateType.SinceDay,
            onDismiss = {
                showSinceDayCalender = false
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AppBar(pet = uiState.pet, isEdit = isEdit, onAction = viewModel::onAction)
        ProfileImage(pet = uiState.pet, onAction = {
            launcher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        })
        BigTypeSpinner(pet = uiState.pet, onAction = viewModel::onAction)
        TypeInput(pet = uiState.pet, onAction = viewModel::onAction)
        NameInput(pet = uiState.pet, onAction = viewModel::onAction)
        BirthDayInput(pet = uiState.pet, showBottomSheet = { showBirthDayCalender = true })
        SinceDayInput(pet = uiState.pet, showBottomSheet = { showSinceDayCalender = true })
        WeightInput(pet = uiState.pet, onAction = viewModel::onAction)
        SexInput(pet = uiState.pet, onAction = viewModel::onAction)
    }
}

@Composable
fun AppBar(pet: Pet, isEdit: Boolean, onAction: (ProfileEditAction) -> Unit) {
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
                text = "프로필 입력",
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
                    if (isEdit) {
                        onAction(ProfileEditAction.Edit(pet = pet, isCompleted = true))
                    } else {
                        onAction(ProfileEditAction.Add(pet = pet))
                    }
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    AppBar(pet = Pet(), isEdit = false, onAction = {})
}


@Composable
fun ProfileEditContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AppBar(pet = Pet(), isEdit = false, onAction = { })
        ProfileImage(pet = Pet(), onAction = { })
        BigTypeSpinner(pet = Pet(), onAction = { })
        TypeInput(pet = Pet(), onAction = { })
        NameInput(pet = Pet(), onAction = { })
        BirthDayInput(pet = Pet(), showBottomSheet = { })
        SinceDayInput(pet = Pet(), showBottomSheet = { })
        WeightInput(pet = Pet(), onAction = { })
        SexInput(pet = Pet(), onAction = { })
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileEditScreenPreview() {
    ProfileEditContent()
}

@Composable
fun ProfileImage(pet: Pet, onAction: () -> Unit) {
    val size = if (pet.imageUrl.isNotEmpty()) {
        120.dp
    } else {
        64.dp
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .padding(vertical = 36.dp)
                .align(Alignment.Center)
                .aspectRatio(1f)
                .background(color = colorResource(id = R.color.brown), shape = CircleShape),
        ) {
            GlideImage(
                imageModel = pet.imageUrl,
                modifier = Modifier
                    .clip(CircleShape)
                    .width(size)
                    .height(size)
                    .align(Alignment.Center)
                    .clickable {
                        onAction()
                    },
                placeHolder = painterResource(id = R.drawable.ic_camera_primary),
                error = painterResource(id = R.drawable.ic_camera_primary),
                previewPlaceholder = R.drawable.ic_camera_primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileImagePreview() {
    ProfileImage(pet = Pet(), onAction = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BigTypeSpinner(pet: Pet, onAction: (ProfileEditAction) -> Unit) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .padding(start = 10.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "반려동물",
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md))
                )
            }

            Box(
                modifier = Modifier
                    .width(0.dp)
                    .weight(1.0f)
            ) {
                val bigType = when (pet.bigType) {
                    0 -> "강아지"
                    1 -> "고양이"
                    else -> "기타"
                }
                OutlinedTextField(
                    value = bigType,
                    onValueChange = {
                        onAction(ProfileEditAction.Edit(pet = pet.copy(type = it)))
                    },
                    modifier = Modifier.clickable {
                        isExpanded = true
                    },
                    enabled = false,
                    colors = TextFieldDefaults.textFieldColors(
                        disabledTextColor = Color.Black,
                        disabledIndicatorColor = Color.Black,
                        containerColor = Color.White
                    )
                )

                DropdownMenu(
                    modifier = Modifier
                        .clickable {
                            isExpanded = true
                        },
                    expanded = isExpanded,
                    onDismissRequest = { // 사라질때
                        isExpanded = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "강아지") },
                        onClick = {
                            isExpanded = false
                            onAction(ProfileEditAction.Edit(pet = pet.copy(bigType = 0)))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "고양이") },
                        onClick = {
                            isExpanded = false
                            onAction(ProfileEditAction.Edit(pet = pet.copy(bigType = 1)))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "기타") },
                        onClick = {
                            isExpanded = false
                            onAction(ProfileEditAction.Edit(pet = pet.copy(bigType = 2)))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeInput(pet: Pet, onAction: (ProfileEditAction) -> Unit) {
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "종류",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md))
            )
        }
        OutlinedTextField(
            value = pet.type,
            textStyle = LocalTextStyle.current.copy(color = colorResource(id = R.color.black)),
            onValueChange = {
                onAction(ProfileEditAction.Edit(pet = pet.copy(type = it)))
            },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Black,
                disabledIndicatorColor = Color.Black,
                containerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInput(pet: Pet, onAction: (ProfileEditAction) -> Unit) {
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "이름",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md))
            )
        }
        OutlinedTextField(
            value = pet.name,
            textStyle = LocalTextStyle.current.copy(color = colorResource(id = R.color.black)),
            onValueChange = {
                onAction(ProfileEditAction.Edit(pet = pet.copy(name = it)))
            },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Black,
                disabledIndicatorColor = Color.Black,
                containerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDayInput(pet: Pet, showBottomSheet: () -> Unit) {
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "생일",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md))
            )
        }
        OutlinedTextField(
            value = pet.birthDay,
            enabled = false,
            textStyle = LocalTextStyle.current.copy(color = colorResource(id = R.color.black)),
            onValueChange = {
            },
            modifier = Modifier.clickable {
                showBottomSheet()
            },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Black,
                disabledIndicatorColor = Color.Black,
                containerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calender(
    pet: Pet,
    dateType: DateType,
    onSave: (ProfileEditAction) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= System.currentTimeMillis()
        }
    })

    val selectedDate = datePickerState.selectedDateMillis?.let {
        DateUtil.convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                when (dateType) {
                    DateType.BirthDay -> {
                        onSave(ProfileEditAction.Edit(pet.copy(birthDay = selectedDate)))
                    }

                    DateType.SinceDay -> {
                        onSave(ProfileEditAction.Edit(pet.copy(sinceDay = selectedDate)))
                    }
                }
                onDismiss()
            }

            ) {
                Text(text = "확인")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "취소")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinceDayInput(pet: Pet, showBottomSheet: () -> Unit) {
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "가족이 된 날",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md))
            )
        }
        OutlinedTextField(
            value = pet.sinceDay,
            enabled = false,
            textStyle = LocalTextStyle.current.copy(color = colorResource(id = R.color.black)),
            onValueChange = {
            },
            modifier = Modifier.clickable {
                showBottomSheet()
            },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Black,
                disabledIndicatorColor = Color.Black,
                containerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightInput(pet: Pet, onAction: (ProfileEditAction) -> Unit) {
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "몸무게",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md))
            )
        }
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            value = pet.weight,
            textStyle = LocalTextStyle.current.copy(color = colorResource(id = R.color.black)),
            onValueChange = {
                onAction(ProfileEditAction.Edit(pet = pet.copy(weight = it)))
            },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Black,
                disabledIndicatorColor = Color.Black,
                containerColor = Color.White
            )
        )
    }
}

@Composable
fun SexInput(pet: Pet, onAction: (ProfileEditAction) -> Unit) {
    Row {
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "성별",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md))
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "남",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md)),
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
                Checkbox(
                    checked = pet.sex == 0,
                    onCheckedChange = {
                        onAction(ProfileEditAction.Edit(pet = pet.copy(sex = 0)))
                    },
                    modifier = Modifier.padding(end = 10.dp)
                )
            }

            Row {
                Box(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "여",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md)),
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
                Checkbox(checked = pet.sex == 1, onCheckedChange = {
                    onAction(ProfileEditAction.Edit(pet = pet.copy(sex = 1)))
                })
            }
        }
    }
}



