package com.dohyun.petmemory.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dohyun.domain.pet.Pet
import com.dohyun.petmemory.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel2 = hiltViewModel(),
    onNavigateToEditScreen: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pets = uiState.pets + listOf(Pet())

    ProfilesPager(pets = pets, onNavigateToEditScreen = onNavigateToEditScreen)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfilesPager(pets: List<Pet>, onNavigateToEditScreen: (Int) -> Unit) {
    val pagerState = rememberPagerState(pageCount = {
        pets.size
    })
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            modifier = Modifier.padding(vertical = 32.dp),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 10.dp
        ) { page ->
            // page content
            Profile(pet = pets[page], onNavigateToEditScreen = onNavigateToEditScreen)
        }
        Menu(text = "의견 남기기", resId = R.drawable.ic_revise) {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:dnlwkem39@gmail.com")
            }.run {
                context.startActivity(this)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Menu(text = "개인 정보 처리 방침", resId = R.drawable.ic_privacy) {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/petmemory/")
            )
            context.startActivity(browserIntent)
        }
    }
}

@Composable
fun Menu(text: String, resId: Int, onNavigate: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 32.dp)
            .clickable {
                onNavigate()
            }
    ) {
        Image(
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
                .padding(end = 10.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = resId),
            contentDescription = null
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = text,
            fontFamily = FontFamily(Font(R.font.bmdohyun_ttf)),
            fontSize = 18.sp
        )
    }
}

@Composable
fun Profile(pet: Pet, onNavigateToEditScreen: (Int) -> Unit) {
    val localDensity = LocalDensity.current
    var height by remember {
        mutableStateOf(0.dp)
    }
    val isEdit = pet.id != 0
    val editAlpha = if (isEdit) {
        1f
    } else {
        0f
    }

    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.light_gray),
                shape = RoundedCornerShape(10.dp),
            )
            .padding(10.dp)
            .clickable {
                onNavigateToEditScreen(pet.id)
            }
            .onGloballyPositioned {
                with(localDensity) {
                    height = it.size.height.toDp()
                }
            }
    ) {
        if (!isEdit) {
            Image(
                painter = painterResource(id = R.drawable.ic_add_gray),
                contentDescription = null,
                modifier = Modifier
                    .padding(18.dp)
                    .width(72.dp)
                    .height(72.dp)
                    .align(Alignment.Center)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(editAlpha)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 10.dp)
            ) {
                GlideImage(
                    imageModel = pet.imageUrl,
                    contentDescription = "profile image",
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(76.dp)
                        .height(76.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = pet.name,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 7.dp),
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.bmdohyun))
                )
                Text(text = pet.type, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "${pet.age}",
                    fontFamily = FontFamily(Font(R.font.bmdohyun)),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                )
                Text(
                    text = "나이",
                    fontFamily = FontFamily(Font(R.font.airbnbcereal_w_lt)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = colorResource(id = R.color.gray))
                        .padding(bottom = 10.dp)
                )

                Text(
                    text = "${pet.sex}",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    fontFamily = FontFamily(Font(R.font.bmdohyun)),
                )
                Text(
                    text = "성별",
                    fontFamily = FontFamily(Font(R.font.airbnbcereal_w_lt)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = colorResource(id = R.color.gray))
                )

                Text(
                    text = pet.birthDay,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    fontFamily = FontFamily(Font(R.font.bmdohyun)),
                )

                Text(
                    text = "생년월일",
                    fontFamily = FontFamily(Font(R.font.airbnbcereal_w_lt)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}