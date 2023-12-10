package com.dohyun.petmemory.ui.splash

import androidx.lifecycle.viewModelScope
import com.dohyun.domain.setting.SettingRepository
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.extension.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : StateViewModel<SplashState>(SplashState.None) {

    fun getDestination() {
        _state.value = SplashState.Loading

        viewModelScope.handle(block = {
            val isLogin = settingRepository.getIsLogin()

            if (isLogin) {
                _state.value = SplashState.Success(destination = Destination.Main)
            } else {
                _state.value = SplashState.Success(destination = Destination.Guide)
            }
        })
    }
}