package com.dohyun.petmemory.ui.splash

sealed class SplashState {
    object None : SplashState()
    object Loading : SplashState()
    data class Success(val destination: Destination) : SplashState()
    object Fail : SplashState()
}

enum class Destination {
    Guide,
    Main
}
