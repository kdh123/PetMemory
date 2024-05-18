package com.dohyun.petmemory.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dohyun.petmemory.R

@Composable
fun ProfileEditScreen(profileEditViewModel: ProfileEditViewModel = hiltViewModel()) {
    val uiState by profileEditViewModel.profileDetailUiState.collectAsStateWithLifecycle()
    val profile = (uiState as? ProfileEditUiState.Profile)?.pet



}

@Composable
fun BigTypeSpinner() {
    Row {
        Text(
            text = "반려동물",
            fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md)),
            modifier = Modifier.width(100.dp)
        )
    }
}

@Composable
fun TypeInput() {
    Row {
        Text(
            text = "종류",
            fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md)),
            modifier = Modifier
                .width(100.dp)
                .padding(end = 5.dp)
        )
        TextField(value = "", onValueChange = {

        })
    }
}

@Composable
fun NameInput() {
    Row {
        Text(
            text = "이름",
            fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md)),
            modifier = Modifier
                .width(100.dp)
                .padding(end = 5.dp)
        )
        TextField(value = "", onValueChange = {

        })
    }
}

@Composable
fun WeightInput() {
    Row {
        Text(
            text = "몸무게",
            fontFamily = FontFamily(Font(R.font.airbnbcereal_w_md)),
            modifier = Modifier
                .width(100.dp)
                .padding(end = 5.dp)
        )
        TextField(value = "", onValueChange = {

        })
    }
}


