package com.dohyun.petmemory.ui.home

import androidx.compose.runtime.Composable
import com.dohyun.petmemory.base.BaseComposeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment2 : BaseComposeFragment() {

    companion object {
        fun newInstance() = HomeFragment2()
    }

    @Composable
    override fun InitView() {
        //HomeScreen()
    }

}