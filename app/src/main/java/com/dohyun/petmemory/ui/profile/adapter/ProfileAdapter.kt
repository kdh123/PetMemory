package com.dohyun.petmemory.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.domain.pet.PetDto
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.ItemProfileBinding

class ProfileAdapter(
    private val onItemClickListener: (PetDto) -> Unit
) : ListAdapter<PetDto, ProfileAdapter.ViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<PetDto>() {
            override fun areItemsTheSame(oldItem: PetDto, newItem: PetDto): Boolean {
                return oldItem.petId == newItem.petId
            }

            override fun areContentsTheSame(oldItem: PetDto, newItem: PetDto): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.apply {
            data = getItem(holder.bindingAdapterPosition)
            root.setOnClickListener {
                onItemClickListener(getItem(holder.bindingAdapterPosition))
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = DataBindingUtil.bind<ItemProfileBinding>(itemView)
    }
}