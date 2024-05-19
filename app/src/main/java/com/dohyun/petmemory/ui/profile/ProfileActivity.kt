package com.dohyun.petmemory.ui.profile

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dohyun.domain.pet.Pet
import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.StateActivity
import com.dohyun.petmemory.databinding.ActivityProfileBinding
import com.dohyun.petmemory.extension.showToast
import com.dohyun.petmemory.ui.main.MainActivity
import com.dohyun.petmemory.ui.profile.ProfileDateBottomSheet.Companion.KEY_DATE_DATA
import com.dohyun.petmemory.ui.profile.ProfileDateBottomSheet.Companion.KEY_DATE_TYPE
import com.dohyun.petmemory.util.DateUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermissionUtil
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class ProfileActivity(override val layoutId: Int = R.layout.activity_profile) : StateActivity<ActivityProfileBinding, ProfileState>(), OnProfileDateClickListener {

    override val stateViewModel: ProfileViewModel by viewModels()

    private var isProfileInit = false

    private var petBigType = PET_TYPE_DOG
    private var petSex = PET_SEX_MALE
    private var petImageUrl = ""

    private var petBirthDay = DateDto()
    private var petSinceDay = DateDto()

    private var isProfileUpdate: Boolean = false

    private val petTypeList = arrayListOf("강아지", "고양이", "기타")
    private val petSexList = arrayListOf("남", "여")

    private var pet : Pet? = null

    private val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    companion object {
        const val KEY_IS_PROFILE_INIT = "key_is_profile_init"
        const val KEY_PET_DATA = "KEY_PET_DATA"

        const val PET_TYPE_DOG = 0
        const val PET_TYPE_CAT = 1
        const val PET_TYPE_OTHER = 2

        const val PET_SEX_MALE = 0
        const val PET_SEX_FEMALE = 1

        const val TYPE_BIRTH_DAY = 0
        const val TYPE_SINCE_DAY = 1

        const val REQ_IMAGE = 0
    }

    override fun render(state: ProfileState) {
        when (state) {
            is ProfileState.SuccessSave -> {
                if (isProfileInit) {
                    Intent(this, MainActivity::class.java).run {
                        startActivity(this)
                    }
                    finish()
                } else {
                    finish()
                }
            }

            else -> {
            }
        }
    }

    private fun initSpinnerPetType() {
        with(binding) {
            val spinnerAdapter = ProfileSpinnerAdapter(
                this@ProfileActivity,
                R.layout.layout_spinner,
                petTypeList
            )
            spinnerPetType.adapter = spinnerAdapter

            spinnerPetType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    petBigType = getPetType(petTypeList[pos])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun getPetType(type: String): Int {
        return when (type) {
            "강아지" -> PET_TYPE_DOG
            "고양이" -> PET_TYPE_CAT
            else -> PET_TYPE_OTHER
        }
    }

    private fun getPetSex(sex: String): Int {
        return when (sex) {
            "남" -> PET_SEX_MALE
            else -> PET_SEX_FEMALE
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
        with(binding) {
            ivBack.setOnClickListener {
                onBackPressed()
                overridePendingTransition(
                    R.anim.anim_slide_none,
                    R.anim.anim_slide_right_exit

                )
            }

            initSpinnerPetType()

            cbMale.isChecked = true

            cbMale.setOnClickListener {
                petSex = getPetSex(petSexList[0])
                cbFemale.isChecked = false
            }

            cbFemale.setOnClickListener {
                petSex = getPetSex(petSexList[1])
                cbMale.isChecked = false
            }

            etBirthDay.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .add(ProfileDateBottomSheet().apply {
                        arguments = Bundle().apply {
                            putInt(KEY_DATE_TYPE, TYPE_BIRTH_DAY)
                            putSerializable(KEY_DATE_DATA, petBirthDay)
                        }
                        setOnClickListener(this@ProfileActivity)
                    }, ProfileDateBottomSheet::class.java.name)
                    .commitAllowingStateLoss()
            }

            etSinceDay.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .add(ProfileDateBottomSheet().apply {
                        arguments = Bundle().apply {
                            putInt(KEY_DATE_TYPE, TYPE_SINCE_DAY)
                            putSerializable(KEY_DATE_DATA, petSinceDay)
                        }
                        setOnClickListener(this@ProfileActivity)
                    }, ProfileDateBottomSheet::class.java.name)
                    .commitAllowingStateLoss()
            }

            ivProfile.setOnClickListener {
                if (TedPermissionUtil.isGranted(*permissionList)) {
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        startActivityForResult(this, REQ_IMAGE)
                    }
                } else {
                    checkMediaPermission()
                }
            }

            tvJoin.setOnClickListener {
                if (etPetSort.text.toString().isEmpty()) {
                    "종을 입력해주세요".showToast(this@ProfileActivity)
                    return@setOnClickListener
                } else if (etPetName.text.toString().isEmpty()) {
                    "이름을 입력해주세요".showToast(this@ProfileActivity)
                    return@setOnClickListener
                } else if (etBirthDay.text.toString().isEmpty()) {
                    "생일을 입력해주세요".showToast(this@ProfileActivity)
                    return@setOnClickListener
                } else if (etSinceDay.text.toString().isEmpty()) {
                    "가족이 된 날짜를 입력해주세요".showToast(this@ProfileActivity)
                    return@setOnClickListener
                } else if (etWeight.text.toString().isEmpty()) {
                    "몸무게를 입력해주세요".showToast(this@ProfileActivity)
                    return@setOnClickListener
                } else if (etWeight.text.toString().toDouble() <= 0.0) {
                    "몸무게를 0보다 큰 값으로 입력해주세요".showToast(this@ProfileActivity)
                    return@setOnClickListener
                } else if (petImageUrl.isEmpty()) {
                    "이미지를 등록해주세요".showToast(this@ProfileActivity)
                    return@setOnClickListener
                }

                val thisYear = DateUtil.todayDate("yyyy").toInt()
                val birthDayYear = etBirthDay.text.toString().substring(0, 4).toInt()
                val birthDay = DateUtil.convertStringToDate(etBirthDay.text.toString())
                val sinceDay = DateUtil.convertStringToDate(etSinceDay.text.toString())

                if (birthDay != null && sinceDay != null) {
                    if (birthDay.after(sinceDay)) {
                        "생일 날짜를 가족이 된 날짜 이전으로 설정해주세요".showToast(this@ProfileActivity)
                        return@setOnClickListener
                    }
                }

                var getPet = Pet(
                    bigType = petBigType,
                    type = etPetSort.text.toString(),
                    name = etPetName.text.toString(),
                    age = thisYear - birthDayYear,
                    birthDay = etBirthDay.text.toString(),
                    sinceDay = etSinceDay.text.toString(),
                    weight = etWeight.text.toString(),
                    sex = petSex,
                    imageUrl = petImageUrl
                )

                if (pet != null) {
                    getPet = getPet.copy(id = pet!!.id)
                }

                stateViewModel.saveProfile(pet = getPet, isUpdate = isProfileUpdate)
            }
        }
    }

    private fun checkMediaPermission() {
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
            .setPermissions(*permissionList)
            .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_IMAGE) {
                data?.data?.let {
                    petImageUrl = it.toString()
                    Glide.with(binding.ivPetProfile)
                        .load(it)
                        .circleCrop()
                        .into(binding.ivPetProfile)
                }
            }
        }
    }

    override fun initBinding() {
    }

    override fun initData(intent: Intent?) {
        isProfileInit = intent?.getBooleanExtra(KEY_IS_PROFILE_INIT, false) ?: false
        pet = intent?.getSerializableExtra(KEY_PET_DATA) as? Pet
        isProfileUpdate = pet != null

        if (isProfileInit) {
            binding.run {
                etBirthDay.setText(DateUtil.todayDate("yyyy-MM-dd"))
                etSinceDay.setText(DateUtil.todayDate("yyyy-MM-dd"))
            }
        }

        pet?.let {
            petImageUrl = it.imageUrl
            petBirthDay = DateUtil.getDateYearMonthDay(it.birthDay)
            petSinceDay = DateUtil.getDateYearMonthDay(it.sinceDay)

            setInfo(pet = it)
        }
    }

    private fun setInfo(pet: Pet) {
        with(binding) {
            pet.run {
                etPetName.setText(name)
                etPetSort.setText(type)
                etPetName.setText(name)
                etBirthDay.setText(birthDay)
                etSinceDay.setText(sinceDay)
                etWeight.setText(weight.toString())

                spinnerPetType.setSelection(bigType)

                if (sex == 0) {
                    cbMale.isChecked = true
                } else {
                    cbFemale.isChecked = true
                }

                Glide.with(ivPetProfile)
                    .load(imageUrl)
                    .circleCrop()
                    .into(ivPetProfile)
            }
        }
    }

    override fun onDateSaveClickListener(type: Int, year: Int, month: Int, day: Int) {
        if (type == TYPE_BIRTH_DAY) {
            binding.etBirthDay.setText("$year-$month-$day")
        } else if (type == TYPE_SINCE_DAY) {
            binding.etSinceDay.setText("$year-$month-$day")
        }

    }

    data class DateDto(
        val year: Int = 2023,
        val month: Int = 7,
        val day: Int = 1
    ) : Serializable
}