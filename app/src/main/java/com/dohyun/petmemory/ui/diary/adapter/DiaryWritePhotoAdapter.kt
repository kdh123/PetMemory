package com.dohyun.petmemory.ui.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.ItemPhotoAddBinding

class DiaryWritePhotoAdapter(
    private val onPhotoClick: (Int) -> Unit
) : ListAdapter<String, DiaryWritePhotoAdapter.DiaryWritePhotoViewHolder>(diffUtil) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryWritePhotoViewHolder {
        return DiaryWritePhotoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_photo_add, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DiaryWritePhotoViewHolder, position: Int) {
        holder.binding?.run {
            data = getItem(position)
            root.setOnClickListener {
                onPhotoClick(position)
            }
        }
    }

    inner class DiaryWritePhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = DataBindingUtil.bind<ItemPhotoAddBinding>(itemView)
    }
}