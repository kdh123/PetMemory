package com.dohyun.petmemory.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.StateFragment
import com.dohyun.petmemory.databinding.FragmentProfileBinding
import com.dohyun.petmemory.extension.setShowSideItems
import com.dohyun.petmemory.ui.profile.adapter.ProfileAdapter
import com.dohyun.petmemory.ui.profile.adapter.ProfileAddAdapter
import com.dohyun.petmemory.util.ViewUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment(override val layoutId: Int = R.layout.fragment_profile) : StateFragment<FragmentProfileBinding, ProfileState>() {

    override val fragmentActivity: FragmentActivity?
        get() = activity

    override val stateViewModel: ProfileViewModel by viewModels()

    private val profileAdapter : ProfileAdapter by lazy {
        ProfileAdapter {
            Intent(context, ProfileActivity::class.java).apply {
                putExtra(ProfileActivity.KEY_PET_DATA, it)
            }.run {
                startActivity(this)

                activity?.overridePendingTransition(
                    R.anim.anim_slide_right_enter,
                    R.anim.anim_slide_none
                )
            }
        }
    }
    private val profileAddAdapter : ProfileAddAdapter by lazy {
        ProfileAddAdapter {
            Intent(context, ProfileActivity::class.java).run {
                startActivity(this)

                activity?.overridePendingTransition(
                    R.anim.anim_slide_right_enter,
                    R.anim.anim_slide_none
                )
            }
        }
    }

    private val concatAdapter = ConcatAdapter(profileAdapter)

    @Inject
    lateinit var viewUtil: ViewUtil

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onResume() {
        super.onResume()

        stateViewModel.getProfile()
    }

    override fun render(state: ProfileState) {
        when (state) {
            is ProfileState.SuccessLoad -> {
                profileAdapter.submitList(state.petList)
                concatAdapter.addAdapter(profileAddAdapter)
                binding.pager.currentItem = 0
            }

            else -> {
            }
        }
    }

    override fun initView() {
        with(binding) {
            pager.apply {
                adapter = concatAdapter
            }.setShowSideItems(0, viewUtil.convertDPtoPX(30))

            clSendOpinion.setOnClickListener {
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:dnlwkem39@gmail.com")
                }.run {
                    startActivity(this)
                }
            }

            clPrivacy.setOnClickListener {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/petmemory/")
                )
                startActivity(browserIntent)
            }
        }
    }

    override fun initBinding() {
    }

    override fun initData() {
    }
}