package com.dohyun.petmemory.ui.profile

import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.BaseBottomSheet
import com.dohyun.petmemory.databinding.LayoutBottomSheetDateBinding
import com.dohyun.petmemory.util.DateUtil

class ProfileDateBottomSheet :
    BaseBottomSheet<LayoutBottomSheetDateBinding>(R.layout.layout_bottom_sheet_date) {

    companion object {
        const val KEY_DATE_TYPE = "date_type"
        const val KEY_DATE_DATA = "date_data"
    }

    private var onProfileDateClickListener: OnProfileDateClickListener? = null

    private var type = ProfileActivity.TYPE_BIRTH_DAY
    private var date = ProfileActivity.DateDto()

    override fun initView() {
        with(binding) {
            tvSave.setOnClickListener {
                val year = datePicker.year
                val month = (datePicker.month + 1)
                val day = datePicker.dayOfMonth

                onProfileDateClickListener?.onDateSaveClickListener(type, year, month, day)
                dismissAllowingStateLoss()
            }
            ivClose.setOnClickListener {
                dismissAllowingStateLoss()
            }

            val todayDate = DateUtil.run {
                convertStringToDate(todayDate("yyyy-MM-dd"))
            }

            datePicker.minDate= DateUtil.convertStringToDate("1980-01-01")!!.time
            datePicker.maxDate= todayDate!!.time
        }
    }

    override fun initData() {
        type = arguments?.getInt(KEY_DATE_TYPE, 0)!!
        date = arguments?.getSerializable(KEY_DATE_DATA) as? ProfileActivity.DateDto
            ?: ProfileActivity.DateDto()

        binding.tvTitle.text = if (type == ProfileActivity.TYPE_BIRTH_DAY) {
            "생일 날짜"
        } else {
            "가족이 된 날짜"
        }

        binding.datePicker.run {
            init(
                date.year, date.month, date.day
            ) { _, _, _, _ -> }
        }
    }

    fun setOnClickListener(onProfileDateClickListener: OnProfileDateClickListener) {
        this.onProfileDateClickListener = onProfileDateClickListener
    }
}