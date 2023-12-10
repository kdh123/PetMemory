package com.dohyun.petmemory.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.domain.pet.PetDto
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.ItemHomePetProfileBinding

class PetProfileAdapter(
    private val onPhotoClick: (Int) -> Unit
) : ListAdapter<PetProfileItem, PetProfileAdapter.HomePetProfileViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<PetProfileItem>() {
            override fun areItemsTheSame(oldItem: PetProfileItem, newItem: PetProfileItem): Boolean {
                return oldItem.petId == newItem.petId
            }

            override fun areContentsTheSame(oldItem: PetProfileItem, newItem: PetProfileItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePetProfileViewHolder {
        return HomePetProfileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_pet_profile, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomePetProfileViewHolder, position: Int) {
        holder.binding?.run {
            data = getItem(holder.bindingAdapterPosition)
            root.setOnClickListener {
                onPhotoClick(holder.bindingAdapterPosition)
            }
        }
    }

    inner class HomePetProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = DataBindingUtil.bind<ItemHomePetProfileBinding>(itemView)
    }
}

fun PetDto.toProfileItem(isChecked: Boolean) : PetProfileItem {
    return PetProfileItem(
        petId = petId,
        petName = petName,
        imageUrl = petImageUrl,
        isChecked = isChecked
    )
}