package com.dohyun.petmemory.ui.diary

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.dohyun.domain.diary.DiaryData
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.StateActivity
import com.dohyun.petmemory.databinding.ActivityDiaryWriteBinding
import com.dohyun.petmemory.extension.showToast
import com.dohyun.petmemory.ui.diary.adapter.DiaryWritePhotoAdapter
import com.dohyun.petmemory.util.LocationUtil
import com.dohyun.petmemory.util.MediaUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermissionUtil
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DiaryWriteActivity : StateActivity<ActivityDiaryWriteBinding, DiaryState>() {
    override val layoutId: Int = R.layout.activity_diary_write
    override val stateViewModel: DiaryViewModel by viewModels()

    private val viewModel: DiaryWriteViewModel by viewModels()

    @Inject
    lateinit var mediaUtil: MediaUtil

    @Inject
    lateinit var locationUtil: LocationUtil

    private var imageUrlList: MutableList<String> = mutableListOf()
    private lateinit var diaryWritePhotoAdapter: DiaryWritePhotoAdapter
    private val imageNeedSaveToGalleryList = mutableSetOf<Int>()

    private var diaryData: DiaryData? = null
    private var isEdit = false

    private var currentPhotoIndex = 0

    private val permissionStorageList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    companion object {
        const val KEY_DIARY_DATA = "key_diary_data"
        const val KEY_IS_DIARY_EDIT = "key_is_diary_revise"

        const val REQ_IMAGE = 0
    }

    override fun render(state: DiaryState) {
        when (state) {
            is DiaryState.Loading -> {
                stateViewModel.showLoading()
            }

            is DiaryState.Edit -> {
                val intent = Intent().apply {
                    putExtra(KEY_DIARY_DATA, state.diaryData)
                }

                setResult(RESULT_OK, intent)
                finish()
            }

            is DiaryState.Save -> {
                val intent = Intent(SyncDiaryData.KEY_SYNC_EVENT).apply {
                    putExtra(SyncDiaryData.KEY_SYNC_EVENT, SyncDiaryData(diaryData = state.diaryData, event = DiaryEvent.Save))
                }

                sendBroadcast(intent)
                finish()
            }

            is DiaryState.Fail -> {
                state.message.showToast(this)
            }

            else -> {
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(
            R.anim.anim_slide_none,
            R.anim.anim_slide_right_exit
        )
    }

    override fun initView() {
        isEdit = intent?.getBooleanExtra(KEY_IS_DIARY_EDIT, false) ?: false

        setContent {
            DiaryWriteScreen(isEdit = isEdit, onBackClick = ::onBackPressed) { diaryData ->
                val intent = Intent(SyncDiaryData.KEY_SYNC_EVENT).apply {
                    putExtra(SyncDiaryData.KEY_SYNC_EVENT, SyncDiaryData(diaryData = diaryData, event = DiaryEvent.Save))
                }

                sendBroadcast(intent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_IMAGE) {
                data?.data?.let {
                    if (imageUrlList.size < 10 && imageUrlList[currentPhotoIndex].isEmpty()) {
                        imageUrlList.add("")
                    }

                    if (Build.MANUFACTURER.contains("Google")) {
                        val path = mediaUtil.convertUriToPath(contentUri = it) ?: ""

                        imageUrlList[currentPhotoIndex] = path
                        imageNeedSaveToGalleryList.add(currentPhotoIndex)
                    } else {
                        imageUrlList[currentPhotoIndex] = it.toString()
                    }

                    val photoCount = imageUrlList.filter { url -> url.isNotEmpty() }.size

                    binding.tvPhotoLimit.text = "$photoCount / 10"

                    diaryWritePhotoAdapter.run {
                        submitList(imageUrlList)
                        notifyItemChanged(currentPhotoIndex)
                    }
                    binding.rvPhoto.scrollToPosition(currentPhotoIndex + 1)
                }
            }
        }
    }

    override fun initBinding() {

    }

    private fun checkPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    startActivityForResult(this, REQ_IMAGE)
                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
            }
        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("")
            .setPermissions(*permissionStorageList)
            .check()
    }

    override fun initData(intent: Intent?) {
        if (TedPermissionUtil.isGranted(*permissionStorageList)) {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                startActivityForResult(this, REQ_IMAGE)
            }
        } else {
            checkPermission()
        }
        diaryData = intent?.getSerializableExtra(KEY_DIARY_DATA) as? DiaryData

        viewModel.run {
            initState(diaryData = diaryData)
            initProfileState()
        }
    }
}