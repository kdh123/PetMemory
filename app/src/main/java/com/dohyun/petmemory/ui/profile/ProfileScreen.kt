package com.dohyun.petmemory.ui.profile

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dohyun.domain.pet.PetDto
import com.dohyun.petmemory.R

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val profileUiState by viewModel.profileUiState.collectAsStateWithLifecycle()

    ProfilesPager(uiState = profileUiState)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfilesPager(uiState: ProfileUiState) {
    val profiles = if (uiState is ProfileUiState.Success) {
        uiState.profiles
    } else {
        listOf()
    }

    val pagerState = rememberPagerState(pageCount = {
        profiles.size
    })

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            modifier = Modifier.padding(vertical = 32.dp),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 10.dp
        ) { page ->
            // page content
            Profile(profile = profiles[page])
        }

        Menu(text = "의견 남기기", resId = R.drawable.ic_revise)
        Spacer(modifier = Modifier.height(10.dp))
        Menu(text = "개인 정보 처리 방침", resId = R.drawable.ic_privacy)
    }
}

@Composable
fun Menu(text: String, resId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { /*onClick()*/ }
            .padding(horizontal = 32.dp)
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Profile(profile: PetDto) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.light_gray),
                shape = RoundedCornerShape(10.dp),
            )
            .padding(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 10.dp)
            ) {
                GlideImage(
                    model = profile.petImageUrl,
                    contentDescription = "profile image",
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(76.dp)
                        .height(76.dp)
                        .aspectRatio(1f)
                ) { builder ->
                    builder.centerCrop()
                }
                Text(
                    text = profile.petName,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 7.dp),
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.bmdohyun))
                )
                Text(text = profile.petType, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column() {
                Text(
                    text = "${profile.petAge}",
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
                    text = "${profile.petSex}",
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
                    text = profile.petBirthDay,
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