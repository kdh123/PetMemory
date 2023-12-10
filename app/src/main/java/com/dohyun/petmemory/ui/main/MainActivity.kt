package com.dohyun.petmemory.ui.main

import android.content.Intent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.BaseActivity
import com.dohyun.petmemory.databinding.ActivityMainBinding
import com.dohyun.petmemory.ui.diary.CameraActivity
import com.dohyun.petmemory.ui.home.HomeFragment
import com.dohyun.petmemory.ui.main.adapter.MainAdapter
import com.dohyun.petmemory.ui.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity(override val layoutId: Int = R.layout.activity_main) : BaseActivity<ActivityMainBinding>() {

    private val fragmentList = listOf(
        HomeFragment.newInstance(),
        ProfileFragment.newInstance()
    )

    private lateinit var mainAdapter: MainAdapter
    private val viewModel : MainViewModel by viewModels()

    companion object {
        const val KEY_IS_MOVE_TO_ALBUM = "key_is_move_to_album"
        const val KEY_DIARY_DATA = "key_diary_data"
    }

    override fun onBackPressed() {
        if (binding.pager.currentItem != 0) {
            binding.pager.setCurrentItem(0, false)
            binding.bottomNavigationView.selectedItemId = R.id.menu_home
        } else {
            super.onBackPressed()
        }
    }

    fun setBottomSheetAlpha(alpha: Float) {
        with(binding) {
            bottomAppBar.alpha = alpha
            fabCamera.alpha = alpha

            if (alpha <= 0.0) {
                bottomAppBar.visibility = View.GONE
                fabCamera.visibility = View.GONE
            } else if (alpha > 0.1) {
                bottomAppBar.visibility = View.VISIBLE
                fabCamera.visibility = View.VISIBLE
            }
        }
    }

    override fun initView() {
        with(binding) {
            val scaleAnim = AnimationUtils.loadAnimation(
                this@MainActivity,
                R.anim.anim_scale_repeat
            )

            fabCamera.setOnClickListener {
                Intent(this@MainActivity, CameraActivity::class.java).run {
                    startActivity(this)
                }
                overridePendingTransition(
                    R.anim.anim_slide_down_enter,
                    R.anim.anim_slide_none
                )
            }

            fabCamera.startAnimation(scaleAnim)

            bottomNavigationView.apply {
                background = null
                menu.getItem(1).isEnabled = false
                setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.menu_home -> {
                            pager.setCurrentItem(0, false)
                            true
                        }

                        R.id.menu_profile -> {
                            pager.setCurrentItem(1, false)
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
            }
            mainAdapter = MainAdapter(this@MainActivity, fragmentList)

            /**
             * offscreenPageLimit을 설정해야 viewPage의 프래그먼트가 destroyView가 호출 안 되고 상태 보존 됨
             * */
            pager.apply {
                offscreenPageLimit = 3
                adapter = mainAdapter
                isUserInputEnabled = false
            }
        }
    }

    override fun initBinding() {
    }

    override fun initData(intent: Intent?) {
        val isMoveToAlbum = intent?.getBooleanExtra(KEY_IS_MOVE_TO_ALBUM, false) ?: false

        if (isMoveToAlbum) {
            binding.pager.currentItem = 1
        }
        viewModel.migrationPetProfile()
    }
}