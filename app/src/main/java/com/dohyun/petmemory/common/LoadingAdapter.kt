package com.dohyun.petmemory.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.ItemLoadingBinding

class LoadingAdapter : RecyclerView.Adapter<LoadingAdapter.DiaryLoadingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryLoadingViewHolder {
        return DiaryLoadingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: DiaryLoadingViewHolder, position: Int) {

    }

    inner class DiaryLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ItemLoadingBinding>(itemView)
    }
}