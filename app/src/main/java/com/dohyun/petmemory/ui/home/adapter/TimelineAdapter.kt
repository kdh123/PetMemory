package com.dohyun.petmemory.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.domain.diary.DiaryData
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.ItemHomeTimelineBinding

class TimelineAdapter(
    private val onClickListener: (Int) -> Unit
) : ListAdapter<DiaryData, TimelineAdapter.DiaryViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<DiaryData>() {
            override fun areItemsTheSame(oldItem: DiaryData, newItem: DiaryData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DiaryData, newItem: DiaryData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        return DiaryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_timeline, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.binding?.run {
            data = getItem(holder.bindingAdapterPosition)
            root.setOnClickListener {
                onClickListener(holder.bindingAdapterPosition)
            }
        }
    }

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ItemHomeTimelineBinding>(itemView)
    }
}