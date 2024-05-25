package com.dohyun.petmemory.ui.diary.write

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.BaseActivity
import com.dohyun.petmemory.databinding.ActivityDiaryWriteBinding
import com.dohyun.petmemory.ui.diary.detail.DiaryDetail
import com.dohyun.petmemory.util.LocationUtil
import com.dohyun.petmemory.util.MediaUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermissionUtil
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DiaryWriteActivity : BaseActivity<ActivityDiaryWriteBinding>() {
    override val layoutId: Int = R.layout.activity_diary_write

    private val viewModel: DiaryWriteViewModel by viewModels()

    @Inject
    lateinit var mediaUtil: MediaUtil

    @Inject
    lateinit var locationUtil: LocationUtil

    private var diary: Diary? = null
    private var isEdit = false

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

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(
            R.anim.anim_slide_none,
            R.anim.anim_slide_right_exit
        )
    }

    override fun initView() {
        isEdit = intent?.getBooleanExtra(KEY_IS_DIARY_EDIT, false) ?: false
        diary = intent?.getSerializableExtra(KEY_DIARY_DATA) as? Diary

        val diaryDetail = with(diary!!) {
            DiaryDetail(
                id, title, date, content, imageUrl, lat!!, lng!!, locationUtil.getAddress(lat!!, lng!!)
            )
        }

        setContent {
            DiaryWriteScreen(onFinish = { onBackPressed() }, isEdit = isEdit, diaryDetail = diaryDetail)
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
        diary = intent?.getSerializableExtra(KEY_DIARY_DATA) as? Diary

        viewModel.run {
            onAction(DiaryWriteAction.Edit(diary))
        }
    }
}