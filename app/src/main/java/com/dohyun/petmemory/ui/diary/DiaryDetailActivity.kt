package com.dohyun.petmemory.ui.diary

import android.content.Intent
import androidx.activity.viewModels
import androidx.core.view.doOnPreDraw
import androidx.viewpager2.widget.ViewPager2
import com.dohyun.domain.diary.DiaryData
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.StateActivity
import com.dohyun.petmemory.databinding.ActivityDiaryDetailBinding
import com.dohyun.petmemory.ui.diary.adapter.DiaryDetailPhotoAdapter
import com.dohyun.petmemory.ui.home.HomeFragment
import com.dohyun.petmemory.util.LocationUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DiaryDetailActivity : StateActivity<ActivityDiaryDetailBinding, DiaryState>(), OnMapReadyCallback {

    override val layoutId: Int = R.layout.activity_diary_detail

    override val stateViewModel: DiaryViewModel by viewModels()

    @Inject
    lateinit var locationUtil: LocationUtil

    private val diaryDetailPhotoAdapter = DiaryDetailPhotoAdapter()

    private val bottomSheetDialog =  DiaryDetailBottomSheet()

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private var imageSize = 0
    private var diaryId: String = ""
    private var diaryData: DiaryData? = null

    companion object {
        const val KEY_DIARY_DATA = "key_diary_data"

        const val REQ_EDIT = 0
    }

    override fun render(state: DiaryState) {
        when (state) {
            is DiaryState.Delete -> {
                stateViewModel.hideLoading()

                val intent = Intent(SyncDiaryData.KEY_SYNC_EVENT).apply {
                    putExtra(SyncDiaryData.KEY_SYNC_EVENT, SyncDiaryData(diaryData = state.diaryData, event = DiaryEvent.Delete))
                }

                sendBroadcast(intent)
                finish()
            }

            is DiaryState.Fail -> {
                stateViewModel.hideLoading()
            }

            else -> {
            }
        }
    }

    private fun setInfo(diaryData: DiaryData) {
        with(binding) {
            tvDiaryTitle.text = diaryData.title.ifEmpty { "제목 없음" }
            tvContent.text = diaryData.content?.let {
                it.ifEmpty {
                    "내용 없음"
                }
            } ?: kotlin.run {
                "내용 없음"
            }
            diaryId = diaryData.id
            imageSize = diaryData.imageUrl.size

            tvImageCount.text = "1 / $imageSize"

            diaryDetailPhotoAdapter.submitList(diaryData.imageUrl)

            if (diaryData.lat != null && diaryData.lng != null) {
                lat = diaryData.lat!!
                lng = diaryData.lng!!
            }

            tvLocation.text = locationUtil.getAddress(lat = lat, lng = lng)

            pager.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        setResult(HomeFragment.REQ_DETAIL)
        supportFinishAfterTransition()
        overridePendingTransition(
            R.anim.anim_slide_none,
            R.anim.anim_slide_right_exit
        )
    }

    override fun initView() {
        postponeEnterTransition()
        with(binding) {
            ivBack.setOnClickListener {
                onBackPressed()
                overridePendingTransition(
                    R.anim.anim_slide_none,
                    R.anim.anim_slide_right_exit
                )
            }
            pager.apply {
                adapter = diaryDetailPhotoAdapter

                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        tvImageCount.text = "${position + 1} / $imageSize"
                    }
                })
            }

            ivOption.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .add(bottomSheetDialog, DiaryDetailBottomSheet::class.java.name)
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun initBinding() {
    }

    override fun initData(intent: Intent?) {
        diaryData = intent?.getSerializableExtra(KEY_DIARY_DATA) as? DiaryData
        diaryData?.let {
            setInfo(diaryData = it)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        val marker = Marker()
        marker.position = LatLng(lat, lng)
        marker.map = naverMap

        naverMap.cameraPosition = CameraPosition(LatLng(lat, lng), 15.0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_EDIT) {
                val getData = data?.getSerializableExtra(DiaryWriteActivity.KEY_DIARY_DATA) as? DiaryData ?: return

                setInfo(diaryData = getData)
            }
        }
    }

    fun moveToDiaryWrite() {
        bottomSheetDialog.dismissAllowingStateLoss()

        Intent(this, DiaryWriteActivity::class.java).apply {
            putExtra(DiaryWriteActivity.KEY_DIARY_DATA, diaryData)
            putExtra(DiaryWriteActivity.KEY_IS_DIARY_EDIT, true)
        }.run {
            startActivityForResult(this, REQ_EDIT)
            overridePendingTransition(
                R.anim.anim_slide_right_enter,
                R.anim.anim_slide_none
            )
        }
    }

    fun deleteDiary() {
        bottomSheetDialog.dismissAllowingStateLoss()
        stateViewModel.deleteDiary(diaryData = diaryData!!)
    }
}