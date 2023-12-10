package com.dohyun.petmemory.ui.album

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.StateActivity
import com.dohyun.petmemory.databinding.ActivityAlbumBinding
import com.dohyun.petmemory.extension.showToast
import com.dohyun.petmemory.ui.diary.DiaryDetailActivity
import com.dohyun.petmemory.ui.diary.DiaryWriteActivity
import com.dohyun.petmemory.ui.diary.SyncDiaryData
import com.dohyun.petmemory.ui.diary.adapter.DiaryAdapter
import com.dohyun.petmemory.common.GridSpacingItemDecoration
import com.dohyun.petmemory.common.LoadingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumActivity(override val layoutId: Int = R.layout.activity_album) : StateActivity<ActivityAlbumBinding, AlbumState>() {

    override val stateViewModel: AlbumViewModel by viewModels()

    private val receiver = DiarySyncBroadcastReceiver()

    private lateinit var diaryAdapter: DiaryAdapter
    private val loadingAdapter = LoadingAdapter()
    private lateinit var concatAdapter: ConcatAdapter

    private var isInit = false

    companion object {
        const val REQUEST_CODE_DIARY_WRITE = 1
        const val REQUEST_CODE_DIARY_DETAIL = 2
    }

    override fun onResume() {
        super.onResume()

        if (!isInit) {
            stateViewModel.getDiary()
        } else {
            diaryAdapter.submitList(stateViewModel.currentDiaryList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    override fun render(state: AlbumState) {
        when (state) {
            is AlbumState.Load -> {
                binding.srlMemory.isRefreshing = false

                var isLoadMore = false

                state.diaryList?.run {
                    if (isEmpty()) {
                        with(binding) {
                            srlMemory.visibility = View.GONE
                            tvNoMemory.visibility = View.VISIBLE
                        }
                        return@run
                    }

                    with(binding) {
                        srlMemory.visibility = View.VISIBLE
                        tvNoMemory.visibility = View.GONE
                    }

                    if (size % 3 == 0) {
                        isLoadMore = true
                    }

                    val currentList = map { it.copy() }
                    diaryAdapter.submitList(currentList)
                }

                if (isLoadMore && state.isLoadMore) {
                    concatAdapter.addAdapter(loadingAdapter)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        concatAdapter.removeAdapter(loadingAdapter)
                    }, 300L)
                }

                isInit = true
            }

            is AlbumState.Fail -> {
                binding.srlMemory.isRefreshing = false
                stateViewModel.hideLoading()

                state.message.showToast(context = this)
            }

            else -> {
            }
        }
    }

    override fun initView() {
        with(binding) {
            diaryAdapter = DiaryAdapter(onClickListener = ::moveToDetailDiary)

            tvUpload.setOnClickListener {
                Intent(this@AlbumActivity, DiaryWriteActivity::class.java).run {
                    startActivityForResult(this, REQUEST_CODE_DIARY_WRITE)
                    overridePendingTransition(
                        R.anim.anim_slide_right_enter,
                        R.anim.anim_slide_none
                    )
                }
            }

            concatAdapter = ConcatAdapter(diaryAdapter)

            rvPetDiary.apply {
                adapter = concatAdapter

                itemAnimator = null

                val gridLayoutManager = GridLayoutManager(context, 3)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        when (position) {
                            (concatAdapter.itemCount - 1) -> {
                                var isLoading = false

                                concatAdapter.adapters.forEach {
                                    if (it is LoadingAdapter) {
                                        isLoading = true
                                        return@forEach
                                    }
                                }

                                return if (isLoading) {
                                    3
                                } else {
                                    1
                                }
                            }

                            else -> {
                                return 1
                            }
                        }
                    }
                }
                layoutManager = gridLayoutManager
                addItemDecoration(GridSpacingItemDecoration(3, 10, true))

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        if (!recyclerView.canScrollVertically(1) && dy > 0) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                stateViewModel.getDiary(isPaging = true)
                            }, 300L)
                        }
                    }
                })
            }

            srlMemory.setOnRefreshListener {
                if (!stateViewModel.loadingState.value) {
                    stateViewModel.getDiary()
                }
            }
        }
    }

    override fun initBinding() {
    }

    override fun initData(intent: Intent?) {
        registerBroadcastReceiver()
    }

    private fun moveToDetailDiary(view: View, position: Int) {
        val data = stateViewModel.currentDiaryList[position]

        Intent(this@AlbumActivity, DiaryDetailActivity::class.java).apply {
            putExtra(DiaryDetailActivity.KEY_DIARY_DATA, data)
        }.run {
            val pairThumb = androidx.core.util.Pair(view, view.transitionName)

            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@AlbumActivity, pairThumb)
            startActivityForResult(this, REQUEST_CODE_DIARY_DETAIL, optionsCompat.toBundle())
        }
    }

    private fun registerBroadcastReceiver() {
        val filter = IntentFilter()
        filter.addAction(SyncDiaryData.KEY_SYNC_EVENT)

        registerReceiver(receiver, filter)
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