package com.dohyun.petmemory.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dohyun.petmemory.ui.main.MainActivity
import com.dohyun.petmemory.ui.profile.edit.ProfileEditScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSetActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProfileEditScreen(petId = 0) {
                Intent(this, MainActivity::class.java).run {
                    startActivity(this)
                    finish()
                }
            }
        }
    }
}