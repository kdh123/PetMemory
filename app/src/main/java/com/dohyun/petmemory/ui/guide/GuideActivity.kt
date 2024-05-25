package com.dohyun.petmemory.ui.guide

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.dohyun.domain.setting.SettingRepository
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.BaseActivity
import com.dohyun.petmemory.databinding.ActivityGuideBinding
import com.dohyun.petmemory.extension.handle
import com.dohyun.petmemory.ui.main.MainActivity
import com.dohyun.petmemory.ui.profile.ProfileSetActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class GuideActivity : BaseActivity<ActivityGuideBinding>() {
    override val layoutId: Int = R.layout.activity_guide

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun initView() {
        binding.tvOK.setOnClickListener {
            lifecycleScope.handle(
                dispatcher = Dispatchers.IO,
                block = {
                if (!settingRepository.getIsLogin()) {
                    Intent(this@GuideActivity, ProfileSetActivity::class.java).apply {
                        //putExtra(ProfileActivity.KEY_IS_PROFILE_INIT, true)
                    }
                } else {
                    Intent(this@GuideActivity, MainActivity::class.java)
                }.run {
                    withContext(Dispatchers.Main) {
                        startActivity(this@run)
                        finish()
                    }
                }
            },
                error = {
                    val a = it
                    val b = ""
                }
            )
        }
    }

    override fun initBinding() {
    }

    override fun initData(intent: Intent?) {
    }
}