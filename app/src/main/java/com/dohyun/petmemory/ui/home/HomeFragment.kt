package com.dohyun.petmemory.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dohyun.domain.diary.DiaryData
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.StateFragment
import com.dohyun.petmemory.databinding.FragmentHomeBinding
import com.dohyun.petmemory.extension.handle
import com.dohyun.petmemory.extension.setShowSideItems
import com.dohyun.petmemory.extension.showToast
import com.dohyun.petmemory.ui.album.AlbumActivity
import com.dohyun.petmemory.ui.diary.DiaryDetailActivity
import com.dohyun.petmemory.ui.diary.SyncDiaryData
import com.dohyun.petmemory.common.LoadingAdapter
import com.dohyun.petmemory.extension.repeatOnStart
import com.dohyun.petmemory.ui.home.adapter.PetProfileAdapter
import com.dohyun.petmemory.ui.home.adapter.TimelineAdapter
import com.dohyun.petmemory.ui.home.adapter.AddPetProfileAdapter
import com.dohyun.petmemory.ui.home.adapter.TimelineEmptyAdapter
import com.dohyun.petmemory.ui.home.adapter.toProfileItem
import com.dohyun.petmemory.ui.main.MainActivity
import com.dohyun.petmemory.ui.home.adapter.MapAdapter
import com.dohyun.petmemory.ui.profile.ProfileActivity
import com.dohyun.petmemory.util.ViewUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment(override val layoutId: Int = R.layout.fragment_home) : StateFragment<FragmentHomeBinding, HomeState>(), OnMapReadyCallback {
    override val fragmentActivity: FragmentActivity?
        get() = activity

    override val stateViewModel: HomeViewModel by viewModels()

    private val petProfileAdapter = PetProfileAdapter(onPhotoClick = ::petProfileClick)
    private val addPetProfileAdapter = AddPetProfileAdapter {
        petProfileClick(isAdd = true)
    }
    private val profileConcatAdapter = ConcatAdapter(petProfileAdapter)

    private val timelineAdapter = TimelineAdapter(onClickListener = ::onDiaryItemClickListener)
    private val timelineEmptyAdapter = TimelineEmptyAdapter()
    private val timelineConcatAdapter = ConcatAdapter()
    private val loadingAdapter = LoadingAdapter()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val mapAdapter = MapAdapter(onItemClickListener = ::onLocationItemClick)

    private var currentPosition = 0
    private var isPagingLoading = false

    @Inject
    lateinit var viewUtil: ViewUtil

    private var isInit = false
    private var mapMarkerList = mutableListOf<Marker>()
    private var bottomSheetState = STATE_COLLAPSED
    private val receiver = DiarySyncBroadcastReceiver()

    companion object {
        const val REQ_DETAIL = 0
        fun newInstance() = HomeFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.map.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        binding.map.onStart()
    }

    override fun onResume() {
        super.onResume()

        binding.map.onResume()
        loadHomeData()
    }

    private fun loadHomeData() {
        viewLifecycleOwner.lifecycleScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                val currentList = stateViewModel.getPetList()
                    .map {
                        it.toProfileItem(isChecked = false)
                    }

                withContext(Dispatchers.Main) {
                    petProfileAdapter.submitList(currentList)
                    profileConcatAdapter.addAdapter(addPetProfileAdapter)

                    if (!isInit) {
                        stateViewModel.getDiary()
                    } else {
                        updateTimelineList(stateViewModel.currentDiaryList)
                        updateMapPager(stateViewModel.currentDiaryList)
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.rvPetProfile.scrollToPosition(0)
                    }, 300L)
                }
            })
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.map.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    override fun onDestroyView() {
        binding.map.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }

    override fun render(state: HomeState) {
        when (state) {
            is HomeState.Load -> {
                updateTimelineList(diaryList = state.diaryList ?: emptyList())
                updateMapPager(diaryList = state.diaryList ?: emptyList())

                binding.map.getMapAsync(this@HomeFragment)

                if (!isInit) {
                    isInit = true
                }

                if (state.isLoadMore) {
                    timelineConcatAdapter.addAdapter(loadingAdapter)
                } else {
                    timelineConcatAdapter.removeAdapter(loadingAdapter)
                }
            }

            is HomeState.Fail -> {
                timelineConcatAdapter.removeAdapter(loadingAdapter)
                stateViewModel.hideLoading()
                context?.let {
                    state.message.showToast(it)
                }
            }

            else -> {
            }
        }
    }

    override fun observeData() {
        super.observeData()

        renderWeather()
    }

    private fun renderWeather() {
        repeatOnStart {
            stateViewModel.weatherState.collect { state ->
                when (state) {
                    is WeatherState.Success -> {
                        //todo : 날씨 정보 노출
                    }

                    is WeatherState.Fail -> {
                        state.message?.showToast(context)
                    }

                    else -> {
                    }
                }
            }
        }
    }

    private fun updateTimelineList(diaryList: List<DiaryData>) {
        if (diaryList.isEmpty()) {
            timelineConcatAdapter.removeAdapter(timelineAdapter)
            timelineConcatAdapter.addAdapter(timelineEmptyAdapter)
            return
        }

        val currentList = diaryList.map { it.copy() }.toList()

        timelineConcatAdapter.removeAdapter(timelineEmptyAdapter)
        timelineConcatAdapter.addAdapter(timelineAdapter)
        timelineAdapter.submitList(currentList)
        isPagingLoading = false
    }

    private fun updateMapPager(diaryList: List<DiaryData>) {
        if (diaryList.isEmpty()) {
            return
        }

        val mapList = diaryList.filter {
            it.lat != 0.0 && it.lng != 0.0
        }.map { it.copy() }

        binding.pager.visibility = if (mapList.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        mapAdapter.submitList(mapList)
    }

    private fun onLocationItemClick(diaryData: DiaryData) {
        Intent(context, DiaryDetailActivity::class.java).apply {
            putExtra(DiaryDetailActivity.KEY_DIARY_DATA, diaryData)
        }.run {
            startActivity(this)
            activity?.overridePendingTransition(
                R.anim.anim_slide_right_enter,
                R.anim.anim_slide_none
            )
        }
    }

    override fun initView() {
        with(binding) {
            pager.apply {
                adapter = mapAdapter
            }.setShowSideItems(viewUtil.convertDPtoPX(20), viewUtil.convertDPtoPX(30))

            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    currentPosition = position
                    binding.map.getMapAsync(this@HomeFragment)

                    if (currentPosition >= stateViewModel.currentLocationList.size - 1) {
                        stateViewModel.getDiary(isPaging = true)
                    }
                }
            })

            bottomSheetBehavior = BottomSheetBehavior.from(
                lBottomSheetHomeTimeline.root
            )

            bottomSheetBehavior.expandedOffset = viewUtil.convertDPtoPX(dp = 108)

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (bottomSheetState == STATE_EXPANDED && slideOffset < 0.7) {
                        bottomSheetBehavior.state = STATE_COLLAPSED
                    } else if (bottomSheetState == STATE_COLLAPSED && slideOffset >= 0.3) {
                        bottomSheetBehavior.state = STATE_EXPANDED
                    }

                    if (activity is MainActivity) {
                        (activity as MainActivity).setBottomSheetAlpha(slideOffset)
                    }
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // state changed
                    if (newState == STATE_EXPANDED || newState == STATE_COLLAPSED) {
                        bottomSheetState = newState
                    }
                }
            })

            rvPetProfile.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = profileConcatAdapter
            }

            timelineConcatAdapter.addAdapter(timelineAdapter)

            lBottomSheetHomeTimeline.rvDiaryLocation.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = timelineConcatAdapter

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        if (!isPagingLoading && !recyclerView.canScrollVertically(1) && dy > 0) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                stateViewModel.getDiary(isPaging = true)
                            }, 300L)
                        }
                    }
                })
            }

            ivAlbum.setOnClickListener {
                Intent(context, AlbumActivity::class.java).run {
                    startActivity(this)
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                bottomSheetBehavior.state = STATE_EXPANDED
            }, 300L)
        }
    }

    private fun onDiaryItemClickListener(position: Int) {
        lifecycleScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                val data = stateViewModel.currentDiaryList[position]

                Intent(context, DiaryDetailActivity::class.java).apply {
                    putExtra(DiaryDetailActivity.KEY_DIARY_DATA, data)
                }.run {
                    startActivity(this)
                    activity?.overridePendingTransition(
                        R.anim.anim_slide_right_enter,
                        R.anim.anim_slide_none
                    )
                }
            })
    }

    private fun petProfileClick(position: Int = -1, isAdd: Boolean = false) {
        Intent(context, ProfileActivity::class.java).apply {
            if (!isAdd) {
                putExtra(ProfileActivity.KEY_PET_DATA, stateViewModel.petList[position])
            }
        }.run {
            startActivity(this)
        }

        activity?.overridePendingTransition(
            R.anim.anim_slide_right_enter,
            R.anim.anim_slide_none
        )
    }

    override fun initBinding() {

    }

    override fun initData() {
        //stateViewModel.getWeather()
        registerBroadcastReceiver()
    }

    override fun onMapReady(naverMap: NaverMap) {
        mapMarkerList.forEach {
            it.map = null
        }
        mapMarkerList.clear()

        val locationList = stateViewModel.currentLocationList

        if (locationList.isEmpty()) {
            return
        }

        locationList.forEach { diaryDto ->
            Marker().apply {
                isHideCollidedMarkers = true
                position = LatLng(diaryDto.lat!!, diaryDto.lng!!)
                icon = OverlayImage.fromResource(R.drawable.img_map_pin)
                width = 120
                height = 120
                setOnClickListener {
                    Intent(context, DiaryDetailActivity::class.java).apply {
                        putExtra(DiaryDetailActivity.KEY_DIARY_DATA, diaryDto)
                    }.run {
                        startActivity(this)
                        activity?.overridePendingTransition(
                            R.anim.anim_slide_right_enter,
                            R.anim.anim_slide_none
                        )
                    }
                    true
                }
                map = naverMap
            }.run {
                mapMarkerList.add(this)
            }

            val cameraUpdate = CameraUpdate.scrollTo(
                LatLng(
                    locationList[currentPosition].lat!!,
                    locationList[currentPosition].lng!!
                )
            ).animate(CameraAnimation.Easing)

            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun registerBroadcastReceiver() {
        val filter = IntentFilter()
        filter.addAction(SyncDiaryData.KEY_SYNC_EVENT)

        context?.registerReceiver(receiver, filter)
    }

    inner class DiarySyncBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val syncDiaryData = intent?.getSerializableExtra(SyncDiaryData.KEY_SYNC_EVENT) as? SyncDiaryData

            syncDiaryData?.let {
                stateViewModel.commitSync(diaryEvent = it.event, diaryData = it.diaryData)
            }
        }
    }
}