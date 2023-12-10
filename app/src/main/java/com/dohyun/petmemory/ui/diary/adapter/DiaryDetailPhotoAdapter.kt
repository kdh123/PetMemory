package com.dohyun.petmemory.ui.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.ItemDiaryDetailPhotoBinding

class DiaryDetailPhotoAdapter : ListAdapter<String, DiaryDetailPhotoAdapter.DiaryDetailPhotoViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryDetailPhotoViewHolder {
        return DiaryDetailPhotoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_diary_detail_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DiaryDetailPhotoViewHolder, position: Int) {
        holder.binding?.run {
            data = getItem(position)
        }
    }

    inner class DiaryDetailPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = DataBindingUtil.bind<ItemDiaryDetailPhotoBinding>(itemView)
    }
}