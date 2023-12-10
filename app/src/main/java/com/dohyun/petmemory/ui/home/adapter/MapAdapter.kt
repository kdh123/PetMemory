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
import com.dohyun.petmemory.databinding.ItemMapLocationBinding

class MapAdapter(
    private val onItemClickListener: (DiaryData) -> Unit
) : ListAdapter<DiaryData, MapAdapter.MapViewHolder>(diffUtil) {

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        return MapViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_map_location, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.binding?.apply {
            data = getItem(holder.bindingAdapterPosition)
            root.setOnClickListener {
                onItemClickListener(getItem(holder.bindingAdapterPosition))
            }
        }
    }

    inner class MapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = DataBindingUtil.bind<ItemMapLocationBinding>(itemView)
    }
}