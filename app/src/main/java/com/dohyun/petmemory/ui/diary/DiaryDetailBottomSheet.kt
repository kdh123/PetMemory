package com.dohyun.petmemory.ui.diary

import com.dohyun.petmemory.R
import com.dohyun.petmemory.base.BaseBottomSheet
import com.dohyun.petmemory.databinding.LayoutBottomSheetDiaryDetailBinding

class DiaryDetailBottomSheet : BaseBottomSheet<LayoutBottomSheetDiaryDetailBinding>(R.layout.layout_bottom_sheet_diary_detail) {
    override fun initView() {
        with(binding) {
            llRevise.setOnClickListener {
                if (activity is DiaryDetailActivity) {
                    (activity as DiaryDetailActivity).moveToDiaryWrite()
                }
            }

            llDelete.setOnClickListener {
                if (activity is DiaryDetailActivity) {
                    (activity as DiaryDetailActivity).deleteDiary()
                }
            }

            ivClose.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }

    override fun initData() {
    }
}