package com.dohyun.petmemory.ui.diary.write

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import android.util.Rational
import android.view.Surface.ROTATION_0
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import com.dohyun.domain.diary.Diary
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.BaseActivity
import com.dohyun.petmemory.common.LoadingDialog
import com.dohyun.petmemory.databinding.ActivityCameraBinding
import com.dohyun.petmemory.extension.showToast
import com.dohyun.petmemory.util.DateUtil
import com.dohyun.petmemory.util.MediaUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.common.util.concurrent.ListenableFuture
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermissionUtil
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : BaseActivity<ActivityCameraBinding>() {

    override val layoutId: Int = R.layout.activity_camera

    @Inject
    lateinit var mediaUtil: MediaUtil

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraProvider: ProcessCameraProvider

    private var imageName: String = ""
    private var imageUrl = mutableListOf<String>()
    private var cameraFrameWidth = 0

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 5 * 1000L
        fastestInterval = 1 * 1000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var lat: Double? = null
    private var lng: Double? = null

    private var isLocationLoading = false
    private val permissionLocation = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val loadingDialog = LoadingDialog()

    private var isSavingImage = false
    private var permissionCamera = arrayOf(
        Manifest.permission.CAMERA
    )

    companion object {
        const val REQ_IMAGE = 1
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(
            R.anim.anim_slide_none,
            R.anim.anim_slide_down_exit
        )
    }

    override fun onStop() {
        if (loadingDialog.isAdded) {
            loadingDialog.dismissAllowingStateLoss()
        }

        super.onStop()
    }

    override fun onDestroy() {
        if (loadingDialog.isAdded) {
            loadingDialog.dismissAllowingStateLoss()
        }

        super.onDestroy()
    }

    override fun initView() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProvider = cameraProviderFuture.get()
        with(binding) {
            ivBack.setOnClickListener {
                onBackPressed()
                overridePendingTransition(
                    R.anim.anim_slide_none,
                    R.anim.anim_slide_down_exit
                )
            }

            ivCamera.setOnClickListener {
                showLoading()
                takePhoto()
            }

            llGallery.setOnClickListener {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    startActivityForResult(this, REQ_IMAGE)
                }
            }

            flContainer.doOnLayout { frameLayout ->
                flContainer.updateLayoutParams {
                    cameraFrameWidth = frameLayout.width
                }
            }
        }
    }

    override fun initBinding() {

    }

    @SuppressLint("MissingPermission")
    override fun initData(intent: Intent?) {
        if (TedPermissionUtil.isGranted(*permissionCamera)) {
            setReadyCameraListener()
            if (TedPermissionUtil.isGranted(*permissionLocation)) {
                initLocation()
            } else {
                checkPermission(permissionLocation)
            }
        } else {
            checkPermission(permissionCamera)
        }
    }

    private fun setReadyCameraListener() {
        binding.pvCamera.previewStreamState.observe(this) {
            if (it == PreviewView.StreamState.STREAMING) {
                if (!isSavingImage) {
                    hideLoading()
                }
            } else if (it == PreviewView.StreamState.IDLE) {
                showLoading()
            }
        }
    }

    private fun checkPermission(permission: Array<String>) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                if (permission.contains(Manifest.permission.CAMERA)) {
                    if (TedPermissionUtil.isGranted(*permissionLocation)) {
                        initLocation()
                    } else {
                        checkPermission(permissionLocation)
                    }
                } else if (permission.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showLoading()
                    initLocation()
                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                if (deniedPermissions.contains(Manifest.permission.CAMERA)) {
                    "카메라 권한을 허용해주세요".showToast(this@CameraActivity)
                    finish()
                } else {
                    startCamera()
                }
            }
        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("카메라 권한을 허용해주세요")
            .setPermissions(*permission)
            .check()
    }

    @SuppressLint("MissingPermission")
    private fun initLocation() {
        isLocationLoading = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            requestCallback,
            Looper.getMainLooper()
        )
    }

    private val requestCallback = object : LocationCallback() {
        @SuppressLint("MissingPermission")
        override fun onLocationResult(p0: LocationResult) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    lat = location?.latitude
                    lng = location?.longitude

                    startCamera()
                }
            fusedLocationClient.removeLocationUpdates(this)
        }
    }

    private fun showLoading() {
        if (!loadingDialog.isAdded) {
            supportFragmentManager.beginTransaction()
                .add(loadingDialog, LoadingDialog::class.java.name)
                .commitAllowingStateLoss()
        }

    }

    private fun hideLoading() {
        if (loadingDialog.isAdded) {
            loadingDialog.dismissAllowingStateLoss()
        }
    }

    private fun startCamera() {
        hideLoading()
        cameraProviderFuture.addListener({
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.pvCamera.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )

            } catch (exc: Exception) {
                "카메라 불러오기를 실패하였습니다".showToast(this)
                finish()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        isSavingImage = true
        val imageCapture = ImageCapture
            .Builder()
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.pvCamera.surfaceProvider)
            }

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            val viewPort =
                ViewPort.Builder(Rational(cameraFrameWidth, cameraFrameWidth), ROTATION_0).build()
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture)
                .setViewPort(viewPort)
                .build()
            cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)

        } catch (exc: Exception) {
            "사진 촬영에 실패하였습니다".showToast(this)
            finish()
        }

        // Create time stamped name and MediaStore entry.
        imageName = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PetMemory")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    isSavingImage = false
                    "사진 촬영에 실패하였습니다".showToast(this@CameraActivity)
                    finish()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    isSavingImage = false

                    imageUrl.add(output.savedUri.toString())

                    try {
                        // Unbind use cases before rebinding
                        cameraProvider.unbindAll()

                        // Bind use cases to camera
                        cameraProvider.bindToLifecycle(
                            this@CameraActivity, cameraSelector, preview
                        )

                    } catch (exc: Exception) {
                        "카메라 불러오기를 실패하였습니다".showToast(this@CameraActivity)
                        finish()
                    }
                    hideLoading()

                    moveToWriteDiary()
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_IMAGE) {
                data?.data?.let {
                    imageName = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(System.currentTimeMillis())
                    imageUrl = mutableListOf(it.toString())
                    moveToWriteDiary()
                }
            }
        }
    }

    private fun moveToWriteDiary() {
        Intent(this@CameraActivity, DiaryWriteActivity::class.java).apply {
            val diary = Diary(
                id = imageName,
                title = "",
                date = DateUtil.todayDate(),
                content = "",
                imageUrl = imageUrl,
                lat = lat,
                lng = lng
            )

            putExtra(DiaryWriteActivity.KEY_DIARY_DATA, diary)
        }.run {
            startActivity(this)
        }
    }
}
