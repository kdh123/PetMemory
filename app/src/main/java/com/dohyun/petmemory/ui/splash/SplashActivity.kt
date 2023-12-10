package com.dohyun.petmemory.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Looper
import androidx.activity.viewModels
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.StateActivity
import com.dohyun.petmemory.databinding.ActivitySplashBinding
import com.dohyun.petmemory.ui.guide.GuideActivity
import com.dohyun.petmemory.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity(override val layoutId: Int = R.layout.activity_splash) : StateActivity<ActivitySplashBinding, SplashState>() {
    override val stateViewModel: SplashViewModel by viewModels()

    override fun render(state: SplashState) {
        when (state) {
            is SplashState.Success -> {
                if (state.destination == Destination.Guide) {
                    GuideActivity::class.java
                } else {
                    MainActivity::class.java
                }.run {
                    val intent = Intent(this@SplashActivity, this)
                    startActivity(intent)
                }
                finish()
            }

            else -> {
            }
        }
    }

    override fun initView() {
    }

    override fun initBinding() {
    }

    override fun initData(intent: Intent?) {
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            stateViewModel.getDestination()
        }, 300L)
    }
}